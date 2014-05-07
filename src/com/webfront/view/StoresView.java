/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.bean.StoresManager;
import com.webfront.model.Stores;
import java.util.Comparator;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author rlittle
 */
public class StoresView extends Pane {

    private TableView<Stores> table;
    private ObservableList<Stores> list;
    TableColumn<Stores, Integer> storeIdCol;
    TableColumn<Stores, String> storeNameCol;
    final StoresManager storesManager;
    static StoresView view;
    MenuItem delMenu;
    MenuItem editMenu;

    public StoresView() {
        super();
        storesManager = new StoresManager();
        view = this;
        VBox vbox = new VBox();
        Button btnAdd;
        btnAdd = new Button("Add store");
        delMenu = new MenuItem("Delete");
        editMenu = new MenuItem("Edit");

        btnAdd.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                table.getItems().add(new Stores());
                table.getSelectionModel().selectLast();
                table.edit(0, storeNameCol);
            }
        });

        EventHandler<MouseEvent> doubleClick = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    int row = table.getSelectionModel().getSelectedIndex();
                    if (row > -1) {
                        Stores s = (Stores) table.getSelectionModel().getSelectedItem();
                        if (s.getStoreName() != null && !s.getStoreName().isEmpty()) {
                            new StoreForm(view, s).showForm();
                        }
                    }
                }
            }
        };

        delMenu.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                int row = table.getSelectionModel().getSelectedIndex();
                Stores s = table.getItems().get(row);
                int r = (new ConfirmDialog()).getResult();
                System.out.println(r);
                if (r == ConfirmDialog.CONFIRM_YES) {
                    storesManager.delete(s);
                    list.remove(s);
                    System.out.println("Deleted item " + s.getStoreName());
                } else {
                    System.out.println("Item " + s.getStoreName()+" not deleted");
                }
            }
        });

        editMenu.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                int row = table.getSelectionModel().getSelectedIndex();
                System.out.println("Edit item " + row);
            }
        });

        list = storesManager.getList("SELECT * FROM stores ORDER BY storeName");
        table = new TableView<>();

        table.addEventHandler(MouseEvent.MOUSE_CLICKED, doubleClick);
        table.setEditable(true);
        table.setMaxWidth(USE_PREF_SIZE);
        table.setContextMenu(new ContextMenu(editMenu, delMenu));

        storeIdCol = new TableColumn<>("ID");
        storeIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        storeNameCol = new TableColumn<>("Name");
        storeNameCol.setMinWidth(500.0);
        storeNameCol.setCellValueFactory(new PropertyValueFactory<>("storeName"));
        storeNameCol.setCellFactory(TextFieldTableCell.<Stores>forTableColumn());

        storeNameCol.setOnEditCommit(
                (CellEditEvent<Stores, String> t) -> {
                    ((Stores) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())).setStoreName(t.getNewValue());
                    Stores s = (Stores) t.getRowValue();
                    storesManager.create(s);
                    int idx = t.getTableView().getSelectionModel().getSelectedIndex();
                    t.getTableView().getItems().set(idx, s);

                });

        table.setItems(list);
        table.getColumns().addAll(storeIdCol, storeNameCol);

        vbox.getChildren().addAll(table, btnAdd);
        getChildren().addAll(vbox);
    }

    /**
     * @return the table
     */
    public TableView<Stores> getTable() {
        return table;
    }

    /**
     * @param table the table to set
     */
    public void setTable(TableView<Stores> table) {
        this.table = table;
    }

    /**
     * @return the list
     */
    public ObservableList<Stores> getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(ObservableList<Stores> list) {
        this.list = list;
    }

    public static Comparator<Stores> StoreComparator = new Comparator<Stores>() {
        @Override
        public int compare(Stores o1, Stores o2) {
            String name1 = o1.getStoreName();
            String name2 = o2.getStoreName();
            return name1.compareToIgnoreCase(name2);
        }
    };

    static class ConfirmDialog extends PopupControl {

        Stage stage;
        Scene scene;
        Pane pane;
        Button btnYes;
        Button btnNo;
        Button btnCancel;
        private static int result;

        public static final int CONFIRM_CANCEL = -1;
        public static final int CONFIRM_NO = 0;
        public static final int CONFIRM_YES = 1;

        public ConfirmDialog() {
            super();
            stage = new Stage();
            pane = new Pane();
            scene = new Scene(pane);
            btnYes = new Button("Yes");
            btnNo = new Button("No");
            btnCancel = new Button("Cancel");
            VBox vbox = new VBox();
            vbox.getChildren().add(new Label("Are you sure?"));
            HBox buttons = new HBox();
            buttons.getChildren().addAll(btnYes, btnNo, btnCancel);
            vbox.getChildren().add(buttons);
            pane.getChildren().add(vbox);
            stage.setScene(scene);
            btnYes.setOnAction(new EventHandler() {
                @Override
                public void handle(Event event) {
                    setResult(CONFIRM_YES);
                    stage.close();
                }
            });
            btnNo.setOnAction(new EventHandler() {
                @Override
                public void handle(Event event) {
                    setResult(CONFIRM_NO);
                    stage.close();
                }
            });
            btnCancel.setOnAction(new EventHandler() {
                @Override
                public void handle(Event event) {
                    setResult(CONFIRM_CANCEL);
                    stage.close();
                }
            });
            stage.setTitle("Confirm Action");
            stage.setMinHeight(200);
            stage.setMinWidth(250);
            stage.showAndWait();
        }

        public static int getResult() {
            return ConfirmDialog.result;
        }

        private void setResult(int r) {
            this.result = r;
        }
    }

}
