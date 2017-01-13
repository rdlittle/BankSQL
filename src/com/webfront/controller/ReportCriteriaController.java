/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.controller;

import com.webfront.bean.AccountManager;
import com.webfront.model.Account;
import com.webfront.model.SearchCriteria;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author rlittle
 */
public class ReportCriteriaController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    Button btnOk;
    @FXML
    Button btnCancel;

    @FXML
    ComboBox<Account> cbAccount;

    @FXML
    DatePicker dpStart;
    @FXML
    DatePicker dpEnd;

    private SearchCriteria criteria;
    String stmt;
    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        criteria = new SearchCriteria();
        cbAccount.itemsProperty().set(AccountManager.getInstance().getAccounts());
        cbAccount.converterProperty().set(new AccountManager.AccountConverter());
        LocalDate today = LocalDate.now();
        dpStart.setValue(LocalDate.of(today.minusYears(1).getYear(), 1, 1));
        dpEnd.setValue(LocalDate.of(today.minusYears(1).getYear(), 12, 31));
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

    @FXML
    public void onCancel() {
        stage.close();
    }

    @FXML
    public void onOk() {
        if (cbAccount.getValue() != null) {
            criteria.getAccountProperty().set(cbAccount.getValue());
        }
        criteria.getStartDateProperty().set(dpStart.getValue());
        criteria.getEndDateProperty().set(dpEnd.getValue());
    }

}
