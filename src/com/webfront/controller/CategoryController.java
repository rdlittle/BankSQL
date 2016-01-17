/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.controller;

import com.webfront.bean.CategoryManager;
import com.webfront.model.Category;
import java.util.List;
import java.util.Objects;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;

/**
 *
 * @author rlittle
 */
public class CategoryController {
    
    @FXML
    VBox catPropertiesPanel;
    
    @FXML
    TextField txtCategoryName;
    
    @FXML
    ComboBox cbParentCategory;

    @FXML
    Button btnCatOk;
    
    @FXML
    Button btnCatCancel;
    
    @FXML
    TreeView categoryTree;
    
    @FXML 
    public void initialize() {
        List<Category> olist = CategoryManager.getInstance().getTree();
        TreeItem<Category> root = new TreeItem<>(new Category(-1,"Primary Categories"));
        root.setExpanded(true);
        categoryTree.setShowRoot(true);
        
        for (Category c : olist) {
            if (c.getParent() == 0) {
                root.getChildren().add(new TreeItem<>(c));
            } else {
                for (TreeItem<Category> ti : root.getChildren()) {
                    if (Objects.equals(c.getParent(), ti.getValue().getId())) {
                        ti.getChildren().add(new TreeItem<>(c));
                    }
                }
            }
        }
        categoryTree.setRoot(root);
    }   
}
