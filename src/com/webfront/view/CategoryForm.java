/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.bean.CategoryManager;
import com.webfront.model.Category;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
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
public class CategoryForm extends AnchorPane {

    @FXML
    ComboBox<String> cbPrimeCat;
    @FXML
    ComboBox<String> cbSubCat;
    @FXML
    Button btnOK;
    @FXML
    Button btnDeleteParent;
    @FXML
    Button btnDeleteChild;
    @FXML
    Button btnDeleteAll;
    @FXML
    Button btnCancel;

    private ObservableList<Category> catList;
    CategoryManager catMgr;
    ArrayList<Category> addedCats;

    HashMap<String, Integer> primeCatMap;
    HashMap<String, Integer> subCatMap;
    HashMap<Integer, Category> parentCategories;
    HashMap<Integer, Category> childCategories;
    
    Stage stage;

    public CategoryForm() {
        primeCatMap = new HashMap<>();
        subCatMap = new HashMap<>();
        parentCategories = new HashMap<>();
        childCategories = new HashMap<>();
        addedCats = new ArrayList<>();
        btnOK = new Button();
        btnCancel = new Button();
        btnDeleteParent = new Button();
        btnDeleteChild = new Button();
        btnDeleteAll = new Button();
        cbPrimeCat = new ComboBox<>();
        cbSubCat = new ComboBox<>();

        URL location = getClass().getResource("/com/webfront/app/fxml/CategoryForm.fxml");
        ResourceBundle resources = ResourceBundle.getBundle("com.webfront.app.bank");
        FXMLLoader loader = new FXMLLoader(location, resources);
        loader.setRoot(this);
        loader.setController(this);
        stage = new Stage();
        Scene scene = new Scene(this);
        try {
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(CategoryForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        stage.setScene(scene);
        stage.setTitle("Categories");

        catList = FXCollections.observableArrayList();
        catMgr = new CategoryManager();
        catList = catMgr.getCategories("SELECT * FROM categories WHERE parent = 0 ORDER BY description");

        catList.stream().forEach((c) -> {
            primeCatMap.put(c.getDescription(), c.getId());
            parentCategories.put(c.getId(), c);
        });

        cbPrimeCat.getItems().addAll(primeCatMap.keySet());
        cbPrimeCat.setEditable(true);
        cbPrimeCat.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                ComboBox cb = (ComboBox) event.getSource();
                if (cb.getValue() != null && !cb.getValue().toString().isEmpty()) {
                    String sName = cb.getValue().toString();
                    if (!primeCatMap.containsKey(sName)) {
                        btnOK.setDisable(false);
                        Category newCat = new Category();
                        newCat.setDescription(sName);
                        addedCats.add(newCat);
                    }
                }
            }
        });

        cbPrimeCat.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                String newCat = newValue.toString();
                if (primeCatMap.containsKey(newCat)) {
                    Category c = parentCategories.get(primeCatMap.get(newCat));
                    String sqlStmt = "SELECT * FROM categories WHERE parent = " + Integer.toString(c.getId());
                    sqlStmt += " order by description";
                    childCategories.clear();
                    subCatMap.clear();
                    catList.clear();
                    childCategories.clear();
                    catList = catMgr.getCategories(sqlStmt);
                    for (Category cat2 : catList) {
                        subCatMap.put(cat2.getDescription(), cat2.getId());
                        childCategories.put(cat2.getId(), cat2);
                    }
                    cbSubCat.getItems().addAll(subCatMap.keySet());
                }
            }
        });

        cbSubCat.setEditable(true);
        cbSubCat.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                ComboBox cb = (ComboBox) event.getSource();
                if (cb.getValue() != null && !cb.getValue().toString().isEmpty()) {
                    String sName = cb.getValue().toString();
                    if (!subCatMap.containsKey(sName)) {
                        btnOK.setDisable(false);
                        Category newCat = new Category();
                        newCat.setDescription(sName);
                        String parentName=cbPrimeCat.getSelectionModel().getSelectedItem();
                        Integer parentId=primeCatMap.get(parentName);
                        Category parent = parentCategories.get(parentId);
                        newCat.setParent(parent.getId());
                        addedCats.add(newCat);
                    }
                }
            }
        });
        btnCancel.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                stage.close();
            }
        });

        btnOK.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                if (addedCats != null && !addedCats.isEmpty()) {
                    for (Category c : addedCats) {
                        if (c != null) {
                            catMgr.create(c);
                        }
                    }
                }
                stage.close();
            }
        });

        btnOK.setDisable(true);
        
    }

    public void showForm() {
        stage.showAndWait();
    }
    /**
     * @return the catList
     */
    public ObservableList<Category> getCatList() {
        return catList;
    }

    /**
     * @param catList the catList to set
     */
    public void setCatList(ObservableList<Category> catList) {
        this.catList = catList;
    }
}
