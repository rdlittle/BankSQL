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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    TextField txtStart;
    @FXML
    TextField txtEnd;
    @FXML
    TextField txtOpeningBalance;
    @FXML
    Button btnChooseStart;
    @FXML
    Button btnChooseEnd;
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
    EventHandler<MouseEvent> click;
    LedgerView parentView;
    TextField selectedField;
    int startTrans;
    int endTrans;

    private RebalanceForm() {
        this.loader = null;
        URL location = getClass().getResource("/com/webfront/app/fxml/RebalanceForm.fxml");
        ResourceBundle resources = ResourceBundle.getBundle("com.webfront.app.bank");
        loader = new FXMLLoader(location, resources);
        stage = new Stage();
        scene = new Scene(this);
        stage.setScene(scene);
        stage.setTitle("Rebalance");
        txtStart = new TextField();
        txtEnd = new TextField();
        txtOpeningBalance = new TextField();
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
        if(startTrans > endTrans) {
            form.lblMessage.setText("Starting transaction cannot be greater than ending transaction");
            form.txtEnd.requestFocus();
        } else {
            form.lblMessage.setText("");
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
                    if("txtStart".equals(selectedField.getId())) {
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
