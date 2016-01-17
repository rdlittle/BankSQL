/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.app.BankOld;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author rlittle
 */
public class AccountPickerForm extends AnchorPane {

    @FXML
    ComboBox<String> cbAccountList;

    @FXML
    Button btnOK;

    @FXML
    Button btnCancel;

    Stage stage;
    Scene scene;
    
    public int accountId;
    SimpleStringProperty accountIdProperty;
    
    private HashMap<String, Integer> accountList;

    public AccountPickerForm() {
        accountList = new HashMap<>();
        cbAccountList = new ComboBox<>();
        btnOK = new Button();
        btnCancel = new Button();
        accountIdProperty = new SimpleStringProperty();
        
        URL location = getClass().getResource("/com/webfront/app/fxml/AccountPicker.fxml");
        ResourceBundle resources = ResourceBundle.getBundle("com.webfront.app.bank");
        
        FXMLLoader loader = new FXMLLoader(location, resources);
        loader.setRoot(AccountPickerForm.this);
        loader.setController(AccountPickerForm.this);

        stage = new Stage();
        scene = new Scene(AccountPickerForm.this);

        try {
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(StoreForm.class.getName()).log(Level.SEVERE, null, ex);
        }

        stage.setScene(scene);
        
        BankOld.accountList.stream().forEach((acct) -> {
            accountList.put(acct.getAccountName(), acct.getId());
            cbAccountList.getItems().add(acct.getAccountName());
        });

        cbAccountList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (accountList.containsKey(newValue)) {
                    accountId = accountList.get(newValue);
                    accountIdProperty.set(newValue);
                }
            }
        });

    }

    @FXML
    public void closeForm() {
        stage.close();
    }

    public void showForm() {
        stage.showAndWait();
    }

}
