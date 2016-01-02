/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.bean.StoresManager;
import com.webfront.model.Stores;
import java.util.Comparator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;

/**
 *
 * @author rlittle
 */
public class StoresView extends Pane {

    private TableView<Stores> table;
    private final ObservableList<Stores> list;
    TableColumn<Stores, Integer> storeIdCol;
    TableColumn<Stores, String> storeNameCol;
    final StoresManager storesManager;
    private static StoresView view;
    MenuItem delMenu;
    MenuItem editMenu;

    protected StoresView() {
        super();
        list = FXCollections.<Stores>observableArrayList();
        storesManager = new StoresManager();
        VBox vbox = new VBox();
        Button btnAdd;
        btnAdd = new Button("Add store");
        delMenu = new MenuItem("Delete");
        editMenu = new MenuItem("Edit");

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
                ConfirmDialog cd = new ConfirmDialog("Preparing to delete " + s.getStoreName());
                int r = cd.getResult();
                System.out.println(r);
                if (r == ConfirmDialog.CONFIRM_YES) {
                    storesManager.delete(s);
                    list.remove(s);
                }
            }
        });

        editMenu.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                int row = table.getSelectionModel().getSelectedIndex();
                StoreForm form = new StoreForm(view, table.getItems().get(row));
                form.showForm();
            }
        });

        Platform.runLater(() -> loadData());

        table = new TableView<>();
        table.addEventHandler(MouseEvent.MOUSE_CLICKED, doubleClick);
        list.addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change c) {
                table.getItems().setAll(list);
            }
        });
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

        table.getColumns().addAll(storeIdCol, storeNameCol);
        
        btnAdd.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                Stores store = new Stores();
                table.getItems().add(store);
                table.scrollTo(store);
                table.getSelectionModel().select(store);
                int row = table.getSelectionModel().getSelectedIndex();
                table.getFocusModel().focus(row, storeIdCol);
                table.edit(row, storeNameCol);
            }
        });        

        HBox buttons = new HBox();
        buttons.setAlignment(Pos.BOTTOM_RIGHT);
        buttons.setPadding(new Insets(10, 10, 0, 10));
        buttons.setSpacing(10.0);
        buttons.getChildren().add(btnAdd);
        vbox.getChildren().addAll(table, buttons);
        getChildren().addAll(vbox);
    }

    public static synchronized StoresView getInstance() {
        if (view == null) {
            view = new StoresView();
        }
        return view;
    }

    private void loadData() {
        list.setAll(storesManager.getList("SELECT * FROM stores ORDER BY storeName"));
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
        this.list.setAll(list);
    }

    public static Comparator<Stores> StoreComparator = new Comparator<Stores>() {
        @Override
        public int compare(Stores o1, Stores o2) {
            String name1 = o1.getStoreName();
            String name2 = o2.getStoreName();
            return name1.compareToIgnoreCase(name2);
        }
    };

    static class ConfirmDialog extends Popup {

        Stage stage;
        Scene scene;
        Pane pane;
        Button btnYes;
        Button btnNo;
        Button btnCancel;
        private int result;
        private String prompt;
        private String message;

        public static final int CONFIRM_CANCEL = -1;
        public static final int CONFIRM_NO = 0;
        public static final int CONFIRM_YES = 1;

        public ConfirmDialog(String msg) {
            super();
            message = msg;
            prompt = new String();
            init();
            showDialog();
        }

        private void init() {

            stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);

            btnYes = new Button("Yes");
            btnNo = new Button("No");
            btnCancel = new Button("Cancel");

            HBox buttons = new HBox();
            buttons.alignmentProperty().setValue(Pos.BASELINE_RIGHT);
            buttons.setSpacing(20.0);
            buttons.getChildren().addAll(btnYes, btnNo, btnCancel);

            VBox vbox = new VBox();
            vbox.setPadding(new Insets(10, 10, 10, 10));
            vbox.setSpacing(10.0);
            vbox.alignmentProperty().set(Pos.CENTER);
            Label lblPrompt = new Label("Are you sure?");
            Label promptMessage = new Label(getMessage());
            lblPrompt.setPrefHeight(100);
            vbox.getChildren().addAll(promptMessage, lblPrompt, buttons);

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

            stage.setWidth(300.0);
            stage.setHeight(150.0);
            scene = new Scene(vbox);

            stage.setTitle("Confirm Action");
            stage.setScene(scene);

        }

        private void showDialog() {
            stage.requestFocus();
            stage.showAndWait();
        }

        public void setPrompt(String text) {
            this.prompt = text;
        }

        public String getPrompt() {
            if (prompt == null || prompt.isEmpty()) {
                return "Are you sure?";
            }
            return prompt;
        }

        public int getResult() {
            return result;
        }

        private void setResult(int r) {
            this.result = r;
        }

        /**
         * @return the message
         */
        public String getMessage() {
            if (this.message == null || this.message.isEmpty()) {
                return "";
            }
            return this.message;
        }

        /**
         * @param message the message to set
         */
        public void setMessage(String message) {
            this.message = message;
        }
    }

}
