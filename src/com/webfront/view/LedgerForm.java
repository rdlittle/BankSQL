/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.bean.BankManager;
import com.webfront.bean.LedgerManager;
import com.webfront.model.Account;
import com.webfront.model.Category;
import com.webfront.model.Ledger;
import com.webfront.model.Stores;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author rlittle
 */
public final class LedgerForm extends AnchorPane {

    static LedgerView view;
    Ledger oldItem, newItem;

    @FXML
    DatePicker transDate;
    @FXML
    ChoiceBox<Integer> accountNum;
    @FXML
    ComboBox<String> primaryCat;
    @FXML
    ComboBox<String> subCat;
    @FXML
    TextField transId;
    @FXML
    TextField transAmt;
    @FXML
    TextField transDescription;
    @FXML
    TextField transBalance;
    @FXML
    TextField checkNum;
    @FXML
    Button btnOk;
    @FXML
    Button btnCancel;
    @FXML
    Label lblDescription;
    @FXML
    Hyperlink editLink;

    @FXML
    Pane paymentView;

    Stage stage;
    Scene scene;

    HashMap<String, Stores> storeMap;
    HashMap<String, Category> categoryMap, subCatMap;

    @FXML
    PaymentView paymentTable;

    private boolean pettyCash = false;
    private Float pettyCashAmount = new Float(0.0);

    public LedgerForm(LedgerView lv, Ledger item) {
        view = lv;
        oldItem = item;
        newItem = new Ledger();
        transDate = new DatePicker();
        accountNum = new ChoiceBox<>();
        primaryCat = new ComboBox<>();
        subCat = new ComboBox<>();
        transId = new TextField();
        transAmt = new TextField();
        transBalance = new TextField();
        transDescription = new TextField();
        checkNum = new TextField();
        btnOk = new Button();
        btnCancel = new Button();
        storeMap = new HashMap<>();
        categoryMap = new HashMap<>();
        subCatMap = new HashMap<>();
        paymentView = new Pane();
        lblDescription = new Label();
        lblDescription.setLabelFor(transDescription);
        buildForm();
        setFormData();
    }

    public void buildForm() {
        try {
            URL location = getClass().getResource("/com/webfront/app/fxml/LedgerEntryForm.fxml");
            ResourceBundle resources = ResourceBundle.getBundle("com.webfront.app.bank");
            FXMLLoader loader = new FXMLLoader(location, resources);

            btnOk.setDefaultButton(true);
            btnCancel.setCancelButton(true);

            stage = new Stage();
            scene = new Scene(this);
            stage.setScene(scene);
            stage.setTitle("Item Detail");

            loader.setRoot(this);
            loader.setController(this);
            loader.load();

            ObservableList<Category> cList = view.getCategoryManager().getCategories();

            ObservableList<Account> accountList = javafx.collections.FXCollections.observableArrayList(BankManager.getInstance().getList(""));
            accountList.stream().forEach((acct) -> {
                accountNum.getItems().add(acct.getId());
            });

            for (Category c : cList) {
                Integer parent = c.getParent();
                categoryMap.put(c.getDescription(), c);
                if (parent == 0) {
                    primaryCat.getItems().add(c.getDescription());
                } else if (oldItem != null) {
                    if (oldItem.getPrimaryCat() != null) {
                        if (parent == oldItem.getPrimaryCat().getId()) {
                            subCat.getItems().add(c.getDescription());
                        }
                    }
                }
            }

            if (oldItem != null) {
                if (oldItem.getSubCat() != null) {
                    Category c = oldItem.getSubCat();
                    if (c != null) {
                        String desc = c.getDescription();
                        if (!subCat.getItems().contains(desc)) {
                            subCat.getItems().add(desc);
                        }
//                        subCat.setValue(oldItem.getDistribution().get(0).getCategory().getDescription());
                    }
                }
            }

            primaryCat.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    String newCat = newValue.toString();
                    if (categoryMap.containsKey(newCat)) {
                        if (oldItem.getId() != null) {
                            Category c = categoryMap.get(newCat);
                            String sqlStmt = "SELECT * FROM categories WHERE parent = " + Integer.toString(c.getId());
                            sqlStmt += " order by description";
                            ObservableList<Category> subCatList = view.getCategoryManager().getCategories(sqlStmt);
                            subCat.getItems().clear();
                            subCatMap.clear();
                            for (Category cat2 : subCatList) {
                                subCatMap.put(cat2.getDescription(), cat2);
                            }
                            subCat.getItems().addAll(subCatMap.keySet());
                            if (oldItem.getPrimaryCat() == null) {
                                oldItem.setPrimaryCat(c);
                                btnOk.setDisable(false);
                            }
                            if (!oldItem.getPrimaryCat().getDescription().equals(primaryCat.getValue())) {
                                oldItem.setPrimaryCat(c);
                                btnOk.setDisable(false);
                            }
                        }
                    }
                }
            });

            subCat.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    if(oldValue == null) {
                        oldValue = "";
                    }
                    if (newValue != null) {
                        String newCat = newValue.toString();
                        String oldCat = oldValue.toString();
                        if (subCatMap.containsKey(newCat)) {
                            if (oldItem != null) {
                                oldItem.setSubCat(subCatMap.get(newCat));
                                if (!oldCat.equalsIgnoreCase("To Petty Cash")) {
                                    if (newCat.equalsIgnoreCase("To Petty Cash")) {
                                        pettyCash = true;
                                        pettyCashAmount = Math.abs(oldItem.getTransAmt());
                                    }
                                } else if (oldCat.equalsIgnoreCase("To Petty Cash")) {
                                    if (!newValue.toString().equalsIgnoreCase("To Petty Cash")) {
                                        pettyCash = true;
                                        pettyCashAmount = Math.abs(oldItem.getTransAmt()) * -1;
                                    }
                                }
                                btnOk.setDisable(false);
                            }
                        }
                    }
                }
            });

            transDescription.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if (!transDescription.getText().equals(oldItem.getTransDesc())) {
                        oldItem.setTransDesc(transDescription.getText());
                        btnOk.setDisable(false);
                    }
                }
            });

            transDescription.textProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    if (!oldValue.equals(newValue)) {
                        btnOk.setDisable(false);
                    }
                }
            });

            checkNum.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if (event.getCode() == KeyCode.TAB) {
                        if (!checkNum.getText().equals(oldItem.getCheckNum())) {
                            oldItem.setCheckNum(checkNum.getText());
                            btnOk.setDisable(false);
                        }
                    }
                }
            });

            transAmt.textProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    if (!oldValue.equals(newValue)) {
                        btnOk.setDisable(false);
                    }
                }
            });

            paymentView.setPrefSize(857.0, 175.0);
            stage.show();

        } catch (IOException ex) {
            Logger.getLogger(LedgerForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setFormData() {
        if (oldItem != null) {
            btnOk.setDisable(true);
            long ldate = oldItem.getTransDate().getTime();
            Date d2 = new Date(ldate);
            LocalDate localDate = d2.toLocalDate();
            transDate.setValue(localDate);
            transDescription.setText(oldItem.getTransDesc());
            transAmt.setText(Float.toString(oldItem.getTransAmt()));
            transBalance.setText(Float.toString(oldItem.getTransBal()));
            accountNum.setValue(oldItem.getAccountNum());
            transId.setText(oldItem.getId().toString());
            if (oldItem.getPrimaryCat() != null) {
                String str = oldItem.getPrimaryCat().getDescription();
                primaryCat.setValue(str);
            }
            if (oldItem.getSubCat() != null) {
                if (oldItem.getPayment() != null && paymentTable != null) {
                    paymentTable.setList(FXCollections.observableList(oldItem.getPayment()));
                }
                Category c = oldItem.getSubCat();
                if (c != null) {
                    subCat.setValue(c.getDescription());
                }
            }
            if (oldItem.getCheckNum() != null) {
                checkNum.setText(oldItem.getCheckNum());
            }
            transDate.setDisable(true);
            transId.setDisable(true);
            transAmt.setDisable(true);
            btnOk.setDisable(true);
        } else {
            Date date = new Date(newItem.getTransDate().getTime());
            transDate.setValue(date.toLocalDate());
            transAmt.setText(Float.toString(newItem.getTransAmt()));
            transDescription.setText(newItem.getTransDesc());
            accountNum.setValue(newItem.getAccountNum());
            transId.setText(newItem.getId().toString());
            primaryCat.setValue(newItem.getPrimaryCat().getDescription());
        }
    }

    public void updateModel(Ledger item) {
        item.setId(Integer.parseInt(transId.getText()));
        item.setAccountNum(Integer.parseInt(accountNum.getSelectionModel().getSelectedItem().toString()));
        item.setTransDesc(transDescription.getText());
        item.setAccountNum(Integer.parseInt(accountNum.getSelectionModel().getSelectedItem().toString()));
        item.setCheckNum(checkNum.getText());
        item.setTransAmt(Float.parseFloat(transAmt.getText()));
        item.setTransBal(Float.parseFloat(transBalance.getText()));
    }

    @FXML
    public void editLinkClicked() {
        transAmt.setDisable(false);
        transAmt.requestFocus();
    }

    @FXML
    public void onBtnCancel() {
        closeForm();
    }

    @FXML
    public void submitItem() {
        if (oldItem != null) {
            updateModel(oldItem);
            view.getLedgerManager().update(oldItem);
            TableView tv = (TableView) view.getTable();
            TableViewSelectionModel sm = tv.getSelectionModel();
//            int idx = view.getTable().getSelectionModel().getSelectedIndex();
            int idx;
            idx = sm.getSelectedIndex();
            view.getTable().getItems().set(idx, oldItem);
        } else {
            Ledger ledger = new Ledger();
            updateModel(ledger);
            view.getLedgerManager().create(ledger);
            view.getTable().getItems().add(ledger);
        }

        closeForm();
    }
    
    public Stage getStage() {
        return stage;
    }
    
    public void closeForm() {
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        stage.close();
    }

    /**
     * @return the adjustPettyCash
     */
    public boolean isPettyCash() {
        return pettyCash;
    }
    
    public Float getPettyCashAmount() {
        return pettyCashAmount;
    }
}
