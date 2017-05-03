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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 *
 * @author rlittle
 */
public class SetupForm extends AnchorPane {

    Stage stage;
    Scene scene;
    private static SetupForm form = null;
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
    CheckBox chkXlateCat;
    @FXML
    CheckBox chkXlateStore;

    @FXML
    ComboBox<Account> cbAccounts;
    @FXML
    Button btnNew;

    @FXML
    TextField txtAccountName;
    @FXML
    TextField txtAccountNumber;
    @FXML
    TextField txtRoutingNumber;

    @FXML
    ComboBox<StatementFormat> cbStatementFormat;

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
    Label statusLabel;

    @FXML
    Button btnOk;
    @FXML
    Button btnCancel;

    private final AccountManager acctMgr;
    private Config config;

    public SimpleBooleanProperty hasChanged;
    public SimpleStringProperty installDirProperty;
    public SimpleStringProperty importDirProperty;
    public SimpleStringProperty tmpDirProperty;

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

    private SetupForm() {
        URL location = getClass().getResource("/com/webfront/app/fxml/SetupForm.fxml");
        ResourceBundle resources = ResourceBundle.getBundle("com.webfront.app.bank");
        hasChanged = new SimpleBooleanProperty(false);
        isNewAccount = false;

        installDirProperty = new SimpleStringProperty();
        importDirProperty = new SimpleStringProperty();
        tmpDirProperty = new SimpleStringProperty();

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

        acctMgr = AccountManager.getInstance();
        chkXlateCat = new CheckBox();
        chkXlateStore = new CheckBox();
        
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
        form.cbAccounts.getSelectionModel().selectLast();
        form.loadAccount();
        form.txtBankName.requestFocus();
        form.isNewAccount = true;
        form.statusLabel.setText("");
    }

    @FXML
    public void saveConfig() {
        config.setConfig();
    }

    public static SetupForm getInstance(Config cfg) {
        if (form == null) {
            form = new SetupForm();
            form.config = cfg;
            form.loader.setRoot(form);
            form.loader.setController(form);

            try {
                form.loader.load();
                form.addHandlers();

                form.txtInstallLocation.setText(form.config.getInstallDir());
                form.txtTmpLoc.setText(form.config.getTmpDir());
                form.txtImportDir.setText(form.config.getImportDir());

                form.rbChecking.setUserData(AccountType.CHECKING);
                form.rbSavings.setUserData(AccountType.SAVINGS);
                form.rbCreditCard.setUserData(AccountType.CREDIT);

                form.rbActive.setUserData(AccountStatus.ACTIVE);
                form.rbInactive.setUserData(AccountStatus.INACTIVE);
                form.rbClosed.setUserData(AccountStatus.CLOSED);

                form.cbAccounts.converterProperty().setValue(new AccountManager.AccountConverter());
                form.cbAccounts.itemsProperty().setValue(form.acctMgr.getAccounts());
                form.cbAccounts.valueProperty().addListener(new ChangeListener<Account>() {
                    @Override
                    public void changed(ObservableValue<? extends Account> observable, Account oldValue, Account newValue) {
                        form.loadAccount();
                    }
                });

                form.cbAccounts.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
                    form.account.setAccountName(newValue);
                });

                form.cbStates.getItems().addAll(new States().names.values());
                form.cbStatementFormat.getItems().addAll(Account.StatementFormat.values());


            } catch (IOException ex) {
                Logger.getLogger(SetupForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return form;
    }

    private void loadAccount() {
        form.account=form.cbAccounts.getValue();
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
            form.cbStatementFormat.getSelectionModel().select(stmFmt);
        }
        form.chkXlateStore.selectedProperty().set(form.account.isXlateStore());
        form.chkXlateCat.selectedProperty().set(form.account.isXlateCat());
        form.txtConfigName.setText(form.account.getConfigName());
        hasChanged.set(false);
        form.statusLabel.setText("");
    }

    @FXML
    private void saveAccount() {
        if (isNewAccount) {
            acctMgr.create(form.account);
            form.statusLabel.setText("Account created");
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
                if(form.cbStatementFormat.getValue()!=null) {
                    form.account.setStatementFormat(form.cbStatementFormat.getValue());
                }
                form.account.setXlateCat(form.chkXlateCat.isSelected());
                form.account.setXlateStore(form.chkXlateStore.isSelected());
                acctMgr.update(form.account);
                form.statusLabel.setText("Account updated");
            }
        }
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
