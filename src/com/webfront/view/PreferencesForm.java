/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.app.utils.States;
import com.webfront.bean.AccountManager;
import com.webfront.model.Account;
import com.webfront.model.Account.AccountStatus;
import com.webfront.model.Account.AccountType;
import com.webfront.model.Account.StatementFormat;
import com.webfront.model.Config;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 *
 * @author rlittle
 */
public class PreferencesForm extends AnchorPane {

    Stage stage;
    Scene scene;
    private static PreferencesForm form = null;
    private FXMLLoader loader = null;

    @FXML
    TabPane tabPane;

    @FXML
    Tab accountTab;

    @FXML
    Tab generalTab;

    @FXML
    TextField txtInstallLocation;
    @FXML
    Button btnBrowse;
    @FXML
    TextField txtTmpLoc;
    @FXML
    TextField txtImportDir;

    @FXML
    ComboBox<String> cbAccounts;
    @FXML
    Button btnNew;

    @FXML
    TextField txtAccountName;
    @FXML
    TextField txtAccountNumber;
    @FXML
    TextField txtRoutingNumber;
    @FXML
    ComboBox cbStatementFormat;
    @FXML
    TextField txtConfigName;
    @FXML
    TextField txtBankName;
    @FXML
    TextField txtAddress1;
    @FXML
    TextField txtAddress2;
    @FXML
    TextField txtCity;
    @FXML
    TextField txtPostalCode;
    @FXML
    TextField txtPhone;

    @FXML
    ComboBox<String> cbStates;

    @FXML
    RadioButton rbChecking;
    @FXML
    RadioButton rbSavings;
    @FXML
    RadioButton rbCreditCard;
    @FXML
    ToggleGroup accountTypes;

    @FXML
    RadioButton rbActive;
    @FXML
    RadioButton rbInactive;
    @FXML
    RadioButton rbClosed;
    @FXML
    ToggleGroup accountStatus;

    @FXML
    Button btnOk;
    @FXML
    Button btnCancel;

    private final AccountManager acctMgr;
    private final ObservableList<Account> accountList;
    private final HashMap<String, Integer> accountMap;
    private Config config;

    public SimpleBooleanProperty hasChanged;
    public SimpleBooleanProperty accountSelected;
    public SimpleStringProperty installDirProperty;

    public SimpleStringProperty bankNameProperty;
    public SimpleStringProperty accountNumberProperty;
    public SimpleStringProperty accountNameProperty;
    public SimpleStringProperty routingNumberProperty;
    public SimpleStringProperty address1Property;
    public SimpleStringProperty cityProperty;
    public SimpleStringProperty phoneProperty;
    public SimpleStringProperty postalCodeProperty;
    public SimpleStringProperty configNameProperty;

    public boolean isNewAccount;
    public Account account;

    private PreferencesForm() {
        URL location = getClass().getResource("/com/webfront/app/fxml/SetupForm.fxml");
        ResourceBundle resources = ResourceBundle.getBundle("com.webfront.app.bank");
        hasChanged = new SimpleBooleanProperty(false);
        accountSelected = new SimpleBooleanProperty(false);

        installDirProperty = new SimpleStringProperty();
        loader = new FXMLLoader(location, resources);
        stage = new Stage();
        scene = new Scene(this);
        stage.setScene(scene);
        stage.setTitle("Preferences");

        txtInstallLocation = new TextField();
        txtTmpLoc = new TextField();
        txtImportDir = new TextField();
        txtAccountNumber = new TextField();
        txtAccountName = new TextField();
        txtRoutingNumber = new TextField();
        txtBankName = new TextField();
        txtAddress1 = new TextField();
        txtCity = new TextField();
        txtPhone = new TextField();
        txtPostalCode = new TextField();
        txtConfigName = new TextField();

        cbAccounts = new ComboBox<>();
        cbStates = new ComboBox<>();
        cbStatementFormat = new ComboBox();
        btnBrowse = new Button();

        rbChecking = new RadioButton();
        rbSavings = new RadioButton();
        rbCreditCard = new RadioButton();

        rbActive = new RadioButton();
        rbInactive = new RadioButton();
        rbClosed = new RadioButton();

        acctMgr = new AccountManager();
        accountList = FXCollections.observableArrayList(acctMgr.getAccounts());
        accountMap = new HashMap<>();
        accountList.stream().forEach((a) -> {
            accountMap.put(a.getAccountName(), a.getId());
        });

        btnOk = new Button();
    }

    private void addHandlers() {
        bankNameProperty = new SimpleStringProperty(form.account, "bankName");
        accountNameProperty = new SimpleStringProperty(form.account, "accountName");
        accountNumberProperty = new SimpleStringProperty(form.account, "accountNumber");
        routingNumberProperty = new SimpleStringProperty(form.account, "routingNumber");
        address1Property = new SimpleStringProperty(form.account, "address1");
        cityProperty = new SimpleStringProperty(form.account, "city");
        phoneProperty = new SimpleStringProperty(form.account, "phoneNumber");
        postalCodeProperty = new SimpleStringProperty(form.account, "postalCode");
        configNameProperty = new SimpleStringProperty(form.account, "configName");

        txtAccountName.textProperty().bindBidirectional(accountNameProperty);
        txtAccountNumber.textProperty().bindBidirectional(accountNumberProperty);
        txtRoutingNumber.textProperty().bindBidirectional(routingNumberProperty);
        txtAddress1.textProperty().bindBidirectional(address1Property);
        txtCity.textProperty().bindBidirectional(cityProperty);
        txtPhone.textProperty().bindBidirectional(phoneProperty);
        txtPostalCode.textProperty().bindBidirectional(postalCodeProperty);
        txtConfigName.textProperty().bindBidirectional(configNameProperty);
    }

    @FXML
    public void btnBrowseOnAction() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select install location");
        File selectedFile = directoryChooser.showDialog(stage);
        String installDir = selectedFile.getAbsolutePath();
        txtInstallLocation.setText(installDir);
        config.setInstallDir(installDir);
        hasChanged.set(true);
    }

    @FXML
    public void btnBrowseImportDirOnAction() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select import location");
        File selectedFile = directoryChooser.showDialog(stage);
        String importDir = selectedFile.getAbsolutePath();
        txtImportDir.setText(importDir);
        config.setImportDir(importDir);
        hasChanged.set(true);
    }
    
    @FXML
    public void btnBrowseTmpOnAction() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select install location");
        File selectedFile = directoryChooser.showDialog(stage);
        String tmpDir = selectedFile.getAbsolutePath();
        txtTmpLoc.setText(tmpDir);
        config.setTmpDir(tmpDir);
        hasChanged.set(true);
    }

    @FXML
    public void btnNewOnAction() {
        form.account = new Account();
        form.cbAccounts.getItems().add("");
        form.cbAccounts.getSelectionModel().selectLast();
        form.loadAccount();
        form.txtBankName.requestFocus();
        form.accountSelected.set(true);
        form.isNewAccount = true;
    }

    @FXML
    public void saveConfig() {
        config.setConfig();
    }

    public static PreferencesForm getInstance(Config cfg) {
        if (form == null) {
            form = new PreferencesForm();
            form.config = cfg;
            form.loader.setRoot(form);
            form.loader.setController(form);

            try {
                form.loader.load();
                form.addHandlers();
                form.cbAccounts.getItems().addAll(form.accountMap.keySet());
                form.txtInstallLocation.setText(form.config.getInstallDir());
                form.txtTmpLoc.setText(form.config.getTmpDir());
                form.txtImportDir.setText(form.config.getImportDir());

                form.rbChecking.setUserData(AccountType.CHECKING);
                form.rbSavings.setUserData(AccountType.SAVINGS);
                form.rbCreditCard.setUserData(AccountType.CREDIT);

                form.rbActive.setUserData(AccountStatus.ACTIVE);
                form.rbInactive.setUserData(AccountStatus.INACTIVE);
                form.rbClosed.setUserData(AccountStatus.CLOSED);

                form.cbAccounts.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        if (form.accountMap.containsKey(newValue)) {
                            int i = form.accountMap.get(newValue);
                            for (Account a : form.accountList) {
                                if (i == a.getId().intValue()) {
                                    form.account = a;
                                    break;
                                }
                            }
                            form.loadAccount();
                        }
                    }
                });

                form.cbAccounts.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
                    form.account.setAccountName(newValue);
                });

                form.cbStates.getItems().addAll(new States().names.values());
                form.cbStatementFormat.getItems().addAll((Object[])Account.StatementFormat.values());
                form.cbStatementFormat.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
                    @Override
                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                        StatementFormat fmt = (StatementFormat) newValue;
                        form.account.setStatementFormat(fmt);
                        form.hasChanged.set(true);
                    }
                });

                form.accountSelected.addListener(new InvalidationListener() {
                    @Override
                    public void invalidated(Observable observable) {
                        form.hasChanged.set(false);
                        form.btnOk.setDisable(false);
                    }
                });

                form.hasChanged.addListener(new InvalidationListener() {
                    @Override
                    public void invalidated(Observable observable) {
                        String value = observable.toString();
                        form.btnOk.setDisable(false);
                    }
                });

                form.txtBankName.textProperty().addListener(new ChangeListener() {
                    @Override
                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                        if (newValue != null) {
                            if (!newValue.equals(oldValue)) {
                                form.account.setBankName(form.txtBankName.getText());
                                form.hasChanged.set(true);
                            }
                        }
                    }
                });

                form.txtAccountName.textProperty().addListener(new ChangeListener() {
                    @Override
                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                        if (newValue != null) {
                            if (!newValue.equals(oldValue)) {
                                form.account.setAccountName(form.txtAccountName.getText());
                                form.hasChanged.set(true);
                            }
                        }
                    }
                });

                form.accountStatus.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                    public void changed(ObservableValue<? extends Toggle> ov,
                            Toggle old_toggle, Toggle new_toggle) {
                        if (form.accountStatus.getSelectedToggle() != null) {
                            Object obj = form.accountStatus.getSelectedToggle().getUserData();
                            if (obj != null) {
                                AccountStatus status = (AccountStatus) obj;
                                form.account.setAccountStatus(status);
                                form.hasChanged.set(true);
                            }
                        }
                    }
                });

                form.accountTypes.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                    public void changed(ObservableValue<? extends Toggle> ov,
                            Toggle old_toggle, Toggle new_toggle) {
                        if (form.accountTypes.getSelectedToggle() != null) {
                            Object obj = form.accountTypes.getSelectedToggle().getUserData();
                            if (obj != null) {
                                AccountType type = (AccountType) obj;
                                form.account.setAccountType(type);
                                form.hasChanged.set(true);
                            }
                        }
                    }
                });

                form.txtConfigName.textProperty().addListener(new ChangeListener() {
                    @Override
                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                        if (newValue != null) {
                            if (!newValue.equals(oldValue)) {
                                form.account.setConfigName((String) newValue);
                                form.hasChanged.set(true);
                            }
                        }
                    }
                });

                form.btnOk.setDisable(true);
            } catch (IOException ex) {
                Logger.getLogger(PreferencesForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return form;
    }

    private void loadAccount() {
        form.txtAccountName.setText(form.account.getAccountName());
        form.txtAccountNumber.setText(form.account.getAccountNumber());
        form.txtBankName.textProperty().setValue(form.account.getBankName());
        form.txtAddress1.setText(form.account.getAddress1());
        form.txtAddress2.setText(form.account.getAddress2());
        form.txtCity.setText(form.account.getCity());
        form.txtPhone.setText(form.account.getPhoneNumber());
        form.txtPostalCode.setText(form.account.getPostalCode());
        form.txtRoutingNumber.setText(form.account.getRoutingNumber());
        form.cbStates.getSelectionModel().select(form.account.getStateAbbr());
        if (form.account.getAccountType() != null) {
            switch (form.account.getAccountType()) {
                case CHECKING:
                    form.rbChecking.setSelected(true);
                    break;
                case SAVINGS:
                    form.rbSavings.setSelected(true);
                    break;
                case CREDIT:
                    form.rbCreditCard.setSelected(true);
                    break;
            }
        }
        if (form.account.getAccountStatus() != null) {
            switch (form.account.getAccountStatus()) {
                case ACTIVE:
                    form.rbActive.setSelected(true);
                    break;
                case INACTIVE:
                    form.rbInactive.setSelected(true);
                    break;
                case CLOSED:
                    form.rbClosed.setSelected(true);
                    break;
            }
        }
        StatementFormat stmFmt = form.account.getStatementFormat();
        if (stmFmt != null) {
            form.cbStatementFormat.selectionModelProperty().setValue(stmFmt.toString());
        }
        form.txtConfigName.setText(form.account.getConfigName());
        hasChanged.set(false);
        form.accountSelected.set(false);
        form.btnOk.setDisable(true);
    }

    @FXML
    private void saveAccount() {
        if (isNewAccount) {
            acctMgr.create(form.account);
            form.accountList.add(form.account);
            form.accountMap.put(form.account.getAccountName(), form.account.getId());
            int idx = form.cbAccounts.getSelectionModel().getSelectedIndex();
            form.cbAccounts.getItems().set(idx, form.account.getAccountName());
        } else {
            if (form.account != null) {
                form.account.setBankName(form.txtBankName.getText());
                form.account.setAccountName(form.txtAccountName.getText());
                form.account.setAccountNumber(form.txtAccountNumber.getText());
                form.account.setRoutingNumber(form.txtRoutingNumber.getText());
                form.account.setAddress1(form.txtAddress1.getText());
                form.account.setCity(form.txtCity.getText());
                form.account.setStateAbbr(form.cbStates.getValue());
                form.account.setPostalCode(form.txtPostalCode.getText());
                form.account.setPhoneNumber(form.txtPhone.getText());
                form.account.setConfigName(form.txtConfigName.getText());
                if (form.account.getAccountName() == null || form.account.getAccountName().isEmpty()) {
                    form.account.setAccountName(form.account.getId().toString());
                }
                acctMgr.update(form.account);
            }
        }
        form.accountSelected.set(false);
        form.hasChanged.set(false);
        isNewAccount = false;
    }

    public void showForm() {
        stage.showAndWait();
    }

    @FXML
    public void closeForm() {
        form.stage.close();
    }
    
    public TabPane getTabPane() {
        return tabPane;
    }
    
    public Tab getGeneralTab() {
        return generalTab;
    }
    
    public Tab getAccountTab() {
        return accountTab;
    }
}
