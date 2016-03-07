/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.util;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleGroup;

/**
 * FXML Controller class
 *
 * @author rlittle
 */
public class ImportSetupController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private RadioButton rbPdf;

    @FXML
    private ToggleGroup statementType;

    @FXML
    private RadioButton rbText;

    @FXML
    private ComboBox<String> cbFormat;

    @FXML
    private Spinner<String> spinColumns;

    @FXML
    private ComboBox<String> cbSeparator;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbFormat.disableProperty().bindBidirectional(rbPdf.selectedProperty());
    }

}
