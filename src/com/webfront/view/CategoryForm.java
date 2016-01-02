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
import javafx.collections.ListChangeListener;
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
public final class CategoryForm extends AnchorPane {

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

    private static CategoryForm instance = null;

    private ObservableList<Category> catList;
    CategoryManager catMgr;
    ArrayList<Category> addedCats;

    HashMap<String, Integer> primeCatMap;
    HashMap<String, Integer> subCatMap;
    HashMap<Integer, Category> parentCategories;
    HashMap<Integer, Category> childCategories;

    private final URL location = getClass().getResource("/com/webfront/app/fxml/CategoryForm.fxml");
    private final ResourceBundle resources = ResourceBundle.getBundle("com.webfront.app.bank");

    private final Stage stage;

    private static ListChangeListener<Category> listChangeListener;

    private CategoryForm() {
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
        stage = new Stage();

        listChangeListener = new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change c) {
                if(c.wasAdded() && c.wasRemoved()) {
                    System.out.println("category item updated");
                } else if(c.wasAdded()) {
                    System.out.println("category item added");
                } else if(c.wasRemoved()) {
                    System.out.println("category item removed");
                }
            }
        }; 
    }

    private void addHandlers() {
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
                    childCategories.clear();
                    subCatMap.clear();
                    catList.clear();
                    childCategories.clear();
                    catList = catMgr.getChildren(c.getId());
                    for (Category cat2 : catList) {
                        subCatMap.put(cat2.getDescription(), cat2.getId());
                        childCategories.put(cat2.getId(), cat2);
                    }
                    cbSubCat.getItems().addAll(subCatMap.keySet());
                }
            }
        });

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
                        String parentName = cbPrimeCat.getSelectionModel().getSelectedItem();
                        Integer parentId = primeCatMap.get(parentName);
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

    }

    public synchronized static CategoryForm getInstance() {
        if (instance == null) {
            instance = new CategoryForm();
            FXMLLoader loader = new FXMLLoader(instance.location, instance.resources);
            loader.setRoot(instance);
            loader.setController(instance);
            try {
                loader.load();
                instance.addHandlers();

                instance.catMgr = CategoryManager.getInstance();
                instance.catMgr.addListener(listChangeListener);

                instance.setCatList(FXCollections.observableArrayList(instance.catMgr.getList("Category.findAllParent")));

                instance.catList.stream().map((c) -> {
                    instance.primeCatMap.put(c.getDescription(), c.getId());
                    return c;
                }).forEach((c) -> {
                    instance.parentCategories.put(c.getId(), c);
                });

                instance.cbPrimeCat.getItems().addAll(instance.primeCatMap.keySet());
                instance.cbPrimeCat.setEditable(true);
                instance.cbSubCat.setEditable(true);

                instance.btnOK.setDisable(true);

                instance.stage.setTitle("Categories");
                Scene scene = new Scene(instance);
                instance.stage.setScene(scene);
            } catch (IOException ex) {
                Logger.getLogger(CategoryForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return instance;
    }

    public void showForm() {
        if (instance == null) {
            instance = getInstance();
        }
        instance.stage.showAndWait();
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
