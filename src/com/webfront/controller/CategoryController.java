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
    ComboBox<Category> cbParentCategory;

    @FXML
    Button btnCatOk;

    @FXML
    Button btnCatCancel;

    @FXML
    TreeView<Category> categoryTree;

    private Category selectedCategory;
    Category previousValue;

    @FXML
    public void initialize() {
        List<Category> olist = CategoryManager.getInstance().getTree();
        selectedCategory = new Category();
        TreeItem<Category> root = new TreeItem<>(new Category(-1, "Primary Categories"));
        root.setExpanded(true);
        categoryTree.setShowRoot(true);

        for (Category c : olist) {
            if (c.getParent() == 0) {
                root.getChildren().add(new TreeItem<>(c));
                cbParentCategory.getItems().add(c);
            } else {
                for (TreeItem<Category> ti : root.getChildren()) {
                    if (Objects.equals(c.getParent(), ti.getValue().getId())) {
                        ti.getChildren().add(new TreeItem<>(c));
                    }
                }
            }
        }
        categoryTree.setRoot(root);

        categoryTree.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        selectedCategory = newValue.getValue();
                        previousValue = selectedCategory;
                        txtCategoryName.setText(selectedCategory.getDescription());
                        Integer parentId = selectedCategory.getParent();
                        cbParentCategory.disableProperty().setValue(parentId == 0);
                        for (TreeItem ti : root.getChildren()) {
                            Category p = (Category) ti.getValue();
                            if (Objects.equals(p.getId(), parentId)) {
                                cbParentCategory.getSelectionModel().select(p);
                                break;
                            }
                        }
                    }
                });

        txtCategoryName.textProperty().addListener((observable, oldValue, newValue) -> {
            btnCatOk.disableProperty().setValue(Boolean.FALSE);
            btnCatCancel.disableProperty().setValue(Boolean.FALSE);
        });

    }

    @FXML
    public void onBtnCatOkClicked() {
        CategoryManager.getInstance().update(selectedCategory);
    }

    @FXML
    public void onBtnCatCancelClicked() {
        selectedCategory = previousValue;
        btnCatOk.disableProperty().setValue(Boolean.TRUE);
        btnCatCancel.disableProperty().setValue(Boolean.TRUE);
    }

    private void updateUi() {
        txtCategoryName.setText(selectedCategory.getDescription());
        cbParentCategory.getSelectionModel().select(0);
        btnCatOk.disableProperty().setValue(Boolean.TRUE);
        btnCatCancel.disableProperty().setValue(Boolean.TRUE);        
    }
}
