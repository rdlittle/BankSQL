/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.model.Stores;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author rlittle
 */
public class StoreForm extends AnchorPane {

    StoresView view;
    Stores store;
    Stage stage;
    Scene scene;

    @FXML
    Button btnOk;
    @FXML
    Button btnCancel;
    @FXML
    Button btnDelete;
    @FXML
    TextField txtStoreName;

    public StoreForm() {
        btnOk = new Button();
        btnCancel = new Button();
        btnDelete = new Button();
        txtStoreName = new TextField();
        stage = new Stage();
        scene = new Scene(this);
    }

    public StoreForm(Stores store) {
        this();
        this.store=store;
    }
    
    public StoreForm(StoresView view, Stores store) {
        this();
        this.view = view;
        this.store = store;
    }

    public void showForm() {

        URL location = new StoreForm().getClass().getResource("/com/webfront/app/fxml/StoreForm.fxml");
        ResourceBundle resources = ResourceBundle.getBundle("com.webfront.app.bank");
        FXMLLoader loader = new FXMLLoader(location, resources);
        
        stage.initModality(Modality.APPLICATION_MODAL);
        
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(StoreForm.class.getName()).log(Level.SEVERE, null, ex);
        }

        setFormData();
        stage.setScene(scene);
        stage.setTitle("Store Form");
        stage.show();

    }

    private void setFormData() {
        txtStoreName.setText(store.getStoreName());
    }

    @FXML
    private void submit() {
        if (store != null) {
            if (store.getId() == null) {
                String storename = txtStoreName.getText();
                if (storename !=null && !storename.isEmpty()) {
                    store.setStoreName(txtStoreName.getText());
                    view.storesManager.create(store);
                    boolean add = view.getList().add(store);
                }
            } else {
                store.setStoreName(txtStoreName.getText());
                view.storesManager.update(store);
                int idx = view.getTable().getSelectionModel().getSelectedIndex();
                view.getTable().getItems().set(idx, store);
            }
        }
        stage.close();
    }
    
    @FXML
    private void closeForm() {
        stage.close();
    }
    
    @FXML
    private void delete() {
        if(store!=null && store.getId()!=null) {
            if(store.getId()>0) {
               view.storesManager.delete(store);
               view.getList().remove(store);
            }
        }
        closeForm();
    }
}
