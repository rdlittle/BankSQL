/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.controller;

import com.webfront.bean.StoresManager;
import com.webfront.model.Stores;
import com.webfront.view.StoreForm;
import java.util.Comparator;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
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
public class StoreController {

    private final ObservableList<Stores> list;
    final StoresManager storesManager;
    EventHandler<MouseEvent> doubleClick;
    Stores selectedStore;

    MenuItem delMenu;
    MenuItem editMenu;

    @FXML
    TableView<Stores> storesTable;

    @FXML
    TableColumn<Stores, Integer> storeIdCol;

    @FXML
    TableColumn<Stores, String> storeNameCol;

    @FXML
    Button btnStoreAdd;

    @FXML
    Button btnStoreDelete;

    public StoreController() {
        storesManager = new StoresManager();
        list = FXCollections.<Stores>observableArrayList();
        delMenu = new MenuItem("Delete");
        editMenu = new MenuItem("Edit");
    }

    @FXML
    public void initialize() {
        doubleClick = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    int row = storesTable.getSelectionModel().getSelectedIndex();
                    if (row > -1) {
                        Stores s = (Stores) storesTable.getSelectionModel().getSelectedItem();
                        if (s.getStoreName() != null && !s.getStoreName().isEmpty()) {
//                            new StoreForm(view, s).showForm();
                        }
                    }
                }
            }
        };

        delMenu.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                int row = storesTable.getSelectionModel().getSelectedIndex();
                Stores s = storesTable.getItems().get(row);
                StoreController.ConfirmDialog cd = new StoreController.ConfirmDialog("Preparing to delete " + s.getStoreName());
                int r = cd.getResult();
                System.out.println(r);
                if (r == StoreController.ConfirmDialog.CONFIRM_YES) {
                    storesManager.delete(s);
                    list.remove(s);
                }
            }
        });

        editMenu.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                int row = storesTable.getSelectionModel().getSelectedIndex();
                StoreForm form = new StoreForm(storesTable.getItems().get(row));
//                form.showForm();
            }
        });

        storesTable.addEventHandler(MouseEvent.MOUSE_CLICKED, doubleClick);

        list.addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change c) {
                storesTable.getItems().setAll(list);
            }
        });

        if (Platform.isFxApplicationThread()) {
            loadData();
        } else {
            Platform.runLater(() -> loadData());
        }

        storesTable.getSelectionModel().selectedIndexProperty().addListener(new RowSelectListener());
                
        storesTable.setEditable(true);
        storesTable.setContextMenu(new ContextMenu(editMenu, delMenu));

        storeIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        storeNameCol.setCellValueFactory(new PropertyValueFactory<>("storeName"));
        storeNameCol.setCellFactory(TextFieldTableCell.<Stores>forTableColumn());

        storeNameCol.setOnEditCommit(
                (TableColumn.CellEditEvent<Stores, String> t) -> {
                    ((Stores) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())).setStoreName(t.getNewValue());
                    Stores s = (Stores) t.getRowValue();
                    storesManager.create(s);
                    int idx = t.getTableView().getSelectionModel().getSelectedIndex();
                    t.getTableView().getItems().set(idx, s);
                    storesTable.getSelectionModel().clearSelection();
                });

    }

    private void loadData() {
        list.setAll(storesManager.getList("SELECT * FROM stores ORDER BY storeName"));
    }

    @FXML
    public void onBtnStoreAddClick() {
        selectedStore = new Stores();
        list.add(selectedStore);
        int row = list.size() - 1;
        storesTable.requestFocus();
        storesTable.scrollTo(selectedStore);
        storesTable.getSelectionModel().select(row);
        storesTable.getFocusModel().focus(row);
    }

    @FXML
    public void onBtnStoreDeleteClick() {
        selectedStore = storesTable.getSelectionModel().getSelectedItem();
        list.remove(selectedStore);
        storesManager.delete(selectedStore);
        btnStoreDelete.disableProperty().set(true);
    }

    public static Comparator<Stores> StoreComparator = new Comparator<Stores>() {
        @Override
        public int compare(Stores o1, Stores o2) {
            String name1 = o1.getStoreName();
            String name2 = o2.getStoreName();
            return name1.compareToIgnoreCase(name2);
        }
    };

    private class RowSelectListener implements ChangeListener {

        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            int idx = ((Number) newValue).intValue();
            if(idx == list.size() || idx < 0) {
                return;
            }
            Stores store = list.get(idx);
            selectedStore = store;
            btnStoreDelete.disableProperty().set(false);
        }
        
    }

   
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
