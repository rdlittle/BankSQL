/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.bean.StoresManager;
import com.webfront.model.Category;
import com.webfront.model.Ledger;
import com.webfront.model.Receipts;
import com.webfront.model.Stores;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author rlittle
 */
public class ReceiptForm extends AnchorPane {

    @FXML
    DatePicker transDate;
    @FXML
    ChoiceBox accountNum;
    @FXML
    ChoiceBox<String> primaryCat;
    @FXML
    ChoiceBox<String> subCat;

    @FXML
    TextField transId;
    @FXML
    TextField transAmt;
    @FXML
    TextField transDescription;
    @FXML
    Button btnOk;
    @FXML
    Button btnCancel;
    @FXML
    Button btnDelete;

    @FXML
    ComboBox<String> cbStores;

    Stage stage;

    ReceiptsView receiptsView;
    private HashMap<String, Stores> storeMap;
    HashMap<String, Category> categoryMap, subCatMap;
    Receipts oldReceipt, newReceipt;

    public ReceiptForm(ReceiptsView parent, Receipts prevReceipt) {
        try {
            oldReceipt = prevReceipt;
            newReceipt = new Receipts();

            receiptsView = parent;
            transDate = new DatePicker();
            primaryCat = new ChoiceBox<>();

            subCat = new ChoiceBox<>();

            storeMap = new HashMap<>();
            categoryMap = new HashMap<>();
            subCatMap = new HashMap<>();
            btnOk = new Button();
            btnDelete = new Button();

            cbStores = new ComboBox<>();

            URL location = getClass().getResource("/com/webfront/app/fxml/ReceiptForm.fxml");
            ResourceBundle resources = ResourceBundle.getBundle("com.webfront.app.example");
            FXMLLoader loader = new FXMLLoader(location, resources);

            loader.setRoot(this);
            loader.setController(this);
            stage = new Stage();
            Scene scene = new Scene(this);
            loader.load();

            stage.setScene(scene);
            stage.setTitle("Receipt Form");

            // Populate store list
            for (Stores s : receiptsView.getStoreList()) {
                storeMap.put(s.getStoreName(), s);
                cbStores.getItems().add(s.getStoreName());
            };

            cbStores.setEditable(true);
            cbStores.setPromptText("Enter store name...");
            cbStores.setOnAction(new EventHandler() {
                @Override
                public void handle(Event event) {
                    ComboBox cb = (ComboBox) event.getSource();
                    if (cb.getValue() != null && !cb.getValue().toString().isEmpty()) {
                        String sName = cb.getValue().toString();
                    }
                }
            });

            // Populate category 1 and category 2 lists
            ObservableList<Category> subList = (ObservableList<Category>) receiptsView.getCategoryList();
            subList.stream().forEach((c) -> {
                
                categoryMap.put(c.getDescription(), c);
                if (c.getParent() == null || c.getParent()==0) {
                    primaryCat.getItems().add(c.getDescription());
                } else {
                    subCat.getItems().add(c.getDescription());
                }
            });

            transDate.setOnAction(new EventHandler() {
                @Override
                public void handle(Event event) {
                    if (oldReceipt.getId() != null) {
                        Date d = new Date(oldReceipt.getTransDate().getTime());
                        LocalDate oDate = d.toLocalDate();
                        String oldDate = oDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
                        String newDate = transDate.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE);
                        if (!oldDate.equals(newDate)) {
                            btnOk.setDisable(false);
                        }
                    }
                }
            });

            cbStores.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    if (oldReceipt.getId() != null) {
                        if (!oldReceipt.getStore().getStoreName().equals(cbStores.getValue())) {
                            btnOk.setDisable(false);
                        }
                    }
                }
            });

            primaryCat.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    String newCat = newValue.toString();
                    if (categoryMap.containsKey(newCat)) {
                        Category c = categoryMap.get(newValue);
                        String sqlStmt = "SELECT * FROM categories WHERE parent = " + Integer.toString(c.getId());
                        sqlStmt += " order by description";
                        ObservableList<Category> subCatList = receiptsView.categoryManager.getCategories(sqlStmt);
                        subCat.getItems().clear();
                        subCatMap.clear();
                        for (Category cat2 : subCatList) {
                            subCatMap.put(cat2.getDescription(), cat2);
                        }
                        subCat.getItems().addAll(subCatMap.keySet());
                        if (oldReceipt.getId() != null) {
                            if (oldReceipt.getPrimaryCat().getDescription() != primaryCat.getValue()) {
                                btnOk.setDisable(false);
                            }
                        }
                    }
                }
            });

            subCat.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    if (oldReceipt.getId() != null) {
                        if (oldReceipt.getSubCat().getDescription() != subCat.getValue()) {
                            btnOk.setDisable(false);
                        }
                    }
                }
            });

            transDescription.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if (event.getCode() == KeyCode.TAB) {
                        if (!transDescription.getText().equals(oldReceipt.getTransDesc())) {
                            btnOk.setDisable(false);
                        }
                    }
                }
            });

            transId.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if (event.getCode() == KeyCode.TAB) {
                        if (oldReceipt.getLedgerEntry() == null) {
                            if (transId.getText() != null) {
                                Ledger ledger;
                                int id = Integer.parseInt(transId.getText());
                                ledger = receiptsView.getLedgerManager().getItem(id);
                                oldReceipt.setLedgerEntry(ledger);
                                btnOk.setDisable(false);
                            }
                        }
                    }
                }
            });

            transId.textProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    btnOk.setDisable(false);
                }
            });

            transAmt.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if (event.getCode() == KeyCode.TAB) {
                        if (!transAmt.getText().equals(oldReceipt.getTransAmt())) {
                            btnOk.setDisable(false);
                        }
                    }
                }
            });

            // Set default values depending on whether you're editing an existing receipt or creating a new one
            if (oldReceipt.getId() != null) {
                btnOk.setDisable(true);
                long ldate = oldReceipt.getTransDate().getTime();
                Date d2 = new Date(ldate);
                LocalDate localDate = d2.toLocalDate();
                transDate.setValue(localDate);
                transDescription.setText(oldReceipt.getTransDesc());
                transAmt.setText(Float.toString(oldReceipt.getTransAmt()));
                accountNum.setValue(oldReceipt.getAccountNum().toString());
                Ledger l = oldReceipt.getLedgerEntry();
                if (l != null) {
                    transId.setText(l.getId().toString());
                }
                primaryCat.setValue(oldReceipt.getPrimaryCat().getDescription());
                subCat.setValue(oldReceipt.getSubCat().getDescription());
                if (oldReceipt.getStore().getStoreName() != null) {
                    cbStores.setValue(oldReceipt.getStore().getStoreName());
                }
            } else {
                transDate.setValue(LocalDate.now());
                accountNum.setValue("1");
                transId.setText("0");
                transAmt.setText("0");
                btnDelete.setDisable(true);
            }

            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(ReceiptForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void addItem() {
        Receipts receipt = newReceipt;
        String newStoreName = cbStores.getValue();
        String storeKey;
        Stores store = new Stores();

        if (!storeMap.containsKey(newStoreName)) {
            StoresManager storeManager = new StoresManager();
            store.setStoreName(newStoreName);
            storeManager.create(store);
            storeKey = store.getStoreName();
            getStoreMap().put(storeKey, store);
            receiptsView.getStoreList().add(store);
            receiptsView.getStoreAdded().set(true);
        }

        if (oldReceipt.getId() != null) {
            LocalDate localDate = transDate.getValue();
            String dateStr = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
            oldReceipt.setTransDate(Date.valueOf(dateStr));
            oldReceipt.setTransDesc(transDescription.getText());
            oldReceipt.setAccountNum(Integer.parseInt(accountNum.getSelectionModel().getSelectedItem().toString()));
            oldReceipt.setTransAmt(Float.parseFloat(transAmt.getText()));
            storeKey = cbStores.getValue();
            store = getStoreMap().get(storeKey);
            Category cat1 = categoryMap.get(primaryCat.getValue());
            Category cat2 = subCatMap.get(subCat.getValue());
            oldReceipt.setPrimaryCat(cat1);
            oldReceipt.setSubCat(cat2);
            oldReceipt.setStore(store);
            receiptsView.receiptsManager.update(oldReceipt);
            int idx = receiptsView.getTable().getSelectionModel().getSelectedIndex();
            receiptsView.getTable().getItems().set(idx, oldReceipt);
            closeForm();
        } else {
            LocalDate localDate = transDate.getValue();
            String dateStr = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
            receipt.setTransDate(Date.valueOf(dateStr));
            receipt.setTransDesc(transDescription.getText());
            receipt.setAccountNum(Integer.parseInt(accountNum.getSelectionModel().getSelectedItem().toString()));
            receipt.setTransAmt(Float.parseFloat(transAmt.getText()));
            storeKey = cbStores.getValue();
            store = getStoreMap().get(storeKey);
            Category cat1 = categoryMap.get(primaryCat.getValue());
            Category cat2 = subCatMap.get(subCat.getValue());
            receipt.setPrimaryCat(cat1);
            receipt.setSubCat(cat2);
            receipt.setStore(store);
            receiptsView.receiptsManager.create(receipt);
            receiptsView.getList().add(receipt);
            newReceipt = new Receipts();
        }
        transDescription.clear();
        transId.clear();
        transAmt.setText("0");
    }

    @FXML
    public void deleteItem() {
        if (oldReceipt != null) {
            if (oldReceipt.getId() != null) {
                receiptsView.receiptsManager.delete(oldReceipt);
                receiptsView.getTable().getItems().remove(oldReceipt);
                closeForm();
            }
        }
    }

    @FXML
    void closeForm() {
        stage.close();
    }

    /**
     * @return the storeMap
     */
    public HashMap<String, Stores> getStoreMap() {
        return storeMap;
    }

    
}
