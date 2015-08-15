/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author rlittle
 */
public class RebalanceForm extends AnchorPane {

    Stage stage;
    Scene scene;
    private static RebalanceForm form = null;
    private FXMLLoader loader;

    @FXML
    DatePicker dateStart;
    @FXML
    TextField txtStart;
    @FXML
    Button btnChooseStart;

    @FXML
    DatePicker dateEnd;
    @FXML
    TextField txtEnd;
    @FXML
    Button btnChooseEnd;
    
    @FXML
    TextField txtOpeningBalance;
    
    @FXML
    RadioButton rbDate;
    @FXML
    RadioButton rbTrans;
    @FXML
    RadioButton rbBoth;

    @FXML
    Button btnCancel;
    @FXML
    Button btnGo;
    @FXML
    Label lblMessage;

    public SimpleStringProperty startProperty;
    public SimpleStringProperty endProperty;
    public SimpleStringProperty balanceProperty;
    public SimpleBooleanProperty hasChanged;
    
    ToggleGroup rbGroup;
    EventHandler<MouseEvent> click;
    LedgerView parentView;
    TextField selectedField;
    int startTrans;
    int endTrans;
    float balance;

    private RebalanceForm() {
        this.loader = null;
        URL location = getClass().getResource("/com/webfront/app/fxml/RebalanceForm.fxml");
        ResourceBundle resources = ResourceBundle.getBundle("com.webfront.app.bank");
        loader = new FXMLLoader(location, resources);
        rbGroup = new ToggleGroup();
        stage = new Stage();
        scene = new Scene(this);
        stage.setScene(scene);
        stage.setTitle("Rebalance");
        dateStart = new DatePicker();
        txtStart = new TextField();
        dateEnd = new DatePicker();
        txtEnd = new TextField();
        txtOpeningBalance = new TextField();
        rbDate = new RadioButton();
        rbDate.setToggleGroup(rbGroup);
        
        rbTrans = new RadioButton();
        rbTrans.setToggleGroup(rbGroup);
        
        rbBoth = new RadioButton();
        rbBoth.setToggleGroup(rbGroup);        
        
        btnChooseStart = new Button();
        btnChooseEnd = new Button();
        selectedField = new TextField();
        btnGo = new Button();
        btnCancel = new Button();
        hasChanged = new SimpleBooleanProperty(false);
        lblMessage = new Label("");
        txtStart.setId("txtStart");
        txtEnd.setId("txtEnd");
        startTrans = 0;
        endTrans = 0;
        balance = 0;
    }

    public static RebalanceForm getInstance(LedgerView view) {
        if (form == null) {
            form = new RebalanceForm();
            form.loader.setRoot(form);
            form.loader.setController(form);
            form.parentView = view;
            try {
                form.loader.load();
                form.addHandlers();
            } catch (IOException ex) {
                Logger.getLogger(PreferencesForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return form;
    }

    @FXML
    public void btnChooseStartOnAction() {

    }

    @FXML
    public void btnChooseEndOnAction() {
        form.lblMessage.setText("");
    }

    @FXML
    public void btnGoOnAction() {
        if (startTrans > endTrans) {
            form.lblMessage.setText("Starting transaction cannot be greater than ending transaction");
            form.txtEnd.requestFocus();
        } else {
            form.lblMessage.setText("");
            if (!txtOpeningBalance.getText().isEmpty() && !"".equals(txtOpeningBalance.getText())) {
                balance = Float.parseFloat(txtOpeningBalance.getText());
            }
            hasChanged.set(true);
            closeForm();
        }
    }

    @FXML
    public void closeForm() {
        form.stage.close();
    }

    public void showForm() {
        stage.showAndWait();
    }

    private void addHandlers() {
        form.btnChooseStart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                selectedField = txtStart;
                form.lblMessage.setText("");
                form.stage.setIconified(true);
            }
        });

        form.btnChooseEnd.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                selectedField = txtEnd;
                form.lblMessage.setText("");
                form.stage.setIconified(true);
            }
        });

        click = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (stage.isIconified()) {
                    stage.setIconified(false);
                    selectedField.setText(form.parentView.selectedItem.getId().toString());
                    if ("txtStart".equals(selectedField.getId())) {
                        startTrans = Integer.parseInt(selectedField.getText());
                        txtEnd.requestFocus();
                    } else {
                        endTrans = Integer.parseInt(selectedField.getText());
                    }
                }
            }
        };
    }

}
