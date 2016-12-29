/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.controller;

import com.webfront.bean.XrefManager;
import com.webfront.model.Category;
import com.webfront.model.Xref;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 *
 * @author rlittle
 */
public class XrefViewController implements Initializable, ControllerInterface {

    @FXML
    Button btnOk;
    
    @FXML
    TableView<Xref> table;
    
    @FXML
    TableColumn<TableView<Xref>,String> nameColumn;
    
    @FXML
    TableColumn<TableView<Xref>,Character> typeColumn;
    
    @FXML
    TableColumn<TableView<Xref>,Category> cat1Column;
    
    @FXML
    TableColumn<TableView<Xref>,Category> cat2Column;
    
    ObservableList<Category> cat1List;
    ObservableList<Category> cat2List;
    
    URL location;
    ResourceBundle resources;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        location = url;
        resources = rb;
        btnOk = new Button();
        table = new TableView<>();
        nameColumn = new TableColumn<>();
        typeColumn = new TableColumn<>();
        cat1Column = new TableColumn<>();
        cat2Column = new TableColumn<>();
        table.itemsProperty().setValue(XrefManager.getInstance().getList("Xref.findAll"));
    }    

    @Override
    public Button getBtnClose() {
        return this.btnOk;
    }
    
}
