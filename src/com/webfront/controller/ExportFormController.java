/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.controller;

import com.webfront.bean.AccountManager;
import com.webfront.model.Account;
import com.webfront.model.Config;
import java.io.File;
import java.time.LocalDate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author rlittle
 */
public class ExportFormController {
    
    @FXML
    private DatePicker dpStart;

    @FXML
    private DatePicker dpEnd;

    @FXML
    private ComboBox<Account> cbAccount;

    @FXML
    private ComboBox<String> cbFormat;

    @FXML
    private TextField txtPath;

    @FXML
    private Button btnBrowse;

    @FXML
    private Button btnOk;

    @FXML
    private Button btnCancel;
    
    private Stage stage;

    @FXML
    void initialize() {
        LocalDate now = LocalDate.now();
        dpStart.valueProperty().set(now.minusDays(30));
        dpEnd.valueProperty().set(now);
        cbAccount.converterProperty().set(new AccountManager.AccountConverter());
        cbAccount.itemsProperty().set(AccountManager.getInstance().getAccounts());
    }

    @FXML
    void onBtnBrowse(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select statement to import");
        fileChooser.setInitialDirectory(new File(Config.getInstance().getImportDir()));
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            
        }
    }

    @FXML
    void onBtnCancel(ActionEvent event) {

    }

    @FXML
    void onBtnOk(ActionEvent event) {
        LocalDate startDate = dpStart.valueProperty().get();
        LocalDate endDate = dpEnd.valueProperty().get();
        Account acct = cbAccount.getValue();
        String format = cbFormat.getValue();
    }

    /**
     * @return the stage
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * @param stage the stage to set
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
