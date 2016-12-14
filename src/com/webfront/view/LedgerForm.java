/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.bean.AccountManager;
import com.webfront.bean.BankManager;
import com.webfront.bean.CategoryManager;
import com.webfront.model.Account;
import com.webfront.model.Category;
import com.webfront.model.Ledger;
import com.webfront.model.Payment;
import com.webfront.model.Stores;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;

import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

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
    ChoiceBox<Account> cbAccount;
    @FXML
    ComboBox<Category> primaryCat;
    @FXML
    ComboBox<Category> subCat;
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
    TableView detailTable;

    @FXML
    TableColumn detailId;
    @FXML
    TableColumn detailDesc;
    @FXML
    TableColumn detailCat1;
    @FXML
    TableColumn detailCat2;
    @FXML
    TableColumn detailAmt;

    public LedgerForm(LedgerView lv, Ledger item) {
        view = lv;
        oldItem = item;
        newItem = new Ledger();
        transDate = new DatePicker();
        cbAccount = new ChoiceBox<>();
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

            cbAccount.converterProperty().setValue(new AccountManager.AccountConverter());
            cbAccount.itemsProperty().setValue(AccountManager.getInstance().getAccounts());

            primaryCat.converterProperty().setValue(new CategoryManager.CategoryConverter());
            primaryCat.itemsProperty().setValue(CategoryManager.getInstance().getCategories());
            primaryCat.valueProperty().addListener(new ChangeListener<Category>() {
                @Override
                public void changed(ObservableValue<? extends Category> observable, Category oldValue, Category newValue) {
                    CategoryManager.getInstance().getFilteredCategoryList().setPredicate((p) -> p.getParent() == newValue.getId());
                    btnOk.disableProperty().set(false);
                }
            });

            subCat.converterProperty().setValue(new CategoryManager.CategoryConverter());
            subCat.itemsProperty().setValue(CategoryManager.getInstance().getFilteredCategoryList());
            subCat.valueProperty().addListener(new ChangeListener<Category>() {
                @Override
                public void changed(ObservableValue<? extends Category> observable, Category oldValue, Category newValue) {
                    btnOk.disableProperty().set(false);
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

            detailId.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Payment, String>, SimpleStringProperty>() {
                @Override
                public SimpleStringProperty call(TableColumn.CellDataFeatures<Payment, String> param) {
                    Payment p = param.getValue();
                    if (p.getId() != null) {
                        return new SimpleStringProperty(p.getId().toString());
                    }
                    return null;
                }
            });

            detailDesc.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Payment, String>, SimpleStringProperty>() {
                @Override
                public SimpleStringProperty call(TableColumn.CellDataFeatures<Payment, String> param) {
                    Payment p = param.getValue();
                    if (p.getId() != null) {
                        return new SimpleStringProperty(p.getTransDesc());
                    }
                    return null;
                }
            });

            detailCat1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Payment, String>, SimpleStringProperty>() {
                @Override
                public SimpleStringProperty call(TableColumn.CellDataFeatures<Payment, String> param) {
                    Payment p = param.getValue();
                    if (p.getPrimaryCat() != null) {
                        return new SimpleStringProperty(p.getPrimaryCat().toString());
                    }
                    return null;
                }
            });

            detailCat2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Payment, String>, SimpleStringProperty>() {
                @Override
                public SimpleStringProperty call(TableColumn.CellDataFeatures<Payment, String> param) {
                    Payment p = param.getValue();
                    if (p.getSubCat() != null) {
                        return new SimpleStringProperty(p.getSubCat().getDescription());
                    }
                    return null;
                }
            });

            detailAmt.setCellValueFactory(new PropertyValueFactory("transAmt"));
            detailAmt.setCellFactory(new CellFormatter<>(TextAlignment.RIGHT));

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
            cbAccount.setValue(oldItem.getAccount());
            transId.setText(oldItem.getId().toString());
            if (oldItem.getPrimaryCat() != null) {
                primaryCat.setValue(oldItem.getPrimaryCat());
            }
            if (oldItem.getSubCat() != null) {
                Category c = oldItem.getSubCat();
                if (c != null) {
                    subCat.setValue(c);
                }
            }
            if (oldItem.getPayment() != null && detailTable != null) {
//                    detailTable.setList(FXCollections.observableList(oldItem.getPayment()));
                detailTable.itemsProperty().set(FXCollections.observableList(oldItem.getPayment()));
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
            cbAccount.setValue(newItem.getAccount());
            transId.setText(newItem.getId().toString());
            primaryCat.setValue(newItem.getPrimaryCat());
        }
    }

    public void updateModel(Ledger item) {
        item.setId(Integer.parseInt(transId.getText()));
        item.setAccount(cbAccount.getValue());
        item.setTransDesc(transDescription.getText());
        item.setCheckNum(checkNum.getText());
        item.setTransAmt(Float.parseFloat(transAmt.getText()));
        item.setTransBal(Float.parseFloat(transBalance.getText()));
        if (item.getPayment() == null || item.getPayment().isEmpty()) {
            if (primaryCat.getValue() != null) {
                item.setPrimaryCat(primaryCat.getValue());
            }
            if (subCat.getValue() != null) {
                item.setSubCat(subCat.getValue());
            }
        }
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
}
