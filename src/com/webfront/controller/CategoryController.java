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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
    Button btnCatAdd;

    @FXML
    Button btnCatDelete;

    @FXML
    TreeView<Category> categoryTree;

    private Category selectedCategory;
    Category parentCategory;
    Category rollBackCategory;

    private MenuItem addMenu;
    private MenuItem editMenu;
    private MenuItem deleteMenu;
    private ContextMenu contextMenu;
    boolean isNew = false;
    boolean isRoot = false;

    @FXML
    public void initialize() {
        contextMenu = new ContextMenu();
        addMenu = new MenuItem("Add");
        editMenu = new MenuItem("Edit");
        deleteMenu = new MenuItem("Delete");

        addMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                isNew = true;
            }
        });

        deleteMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                isNew = false;
            }
        });

        editMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                isNew = false;
            }
        });

        contextMenu.getItems().addAll(addMenu, editMenu, deleteMenu);

        EventHandler<MouseEvent> clickHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                TreeItem<Category> ti = categoryTree.getSelectionModel().getSelectedItem();
                if (ti == null) {
                    return;
                }
                if (ti.getParent() == null) {
                    isRoot = true;
                    return;
                }

                isRoot = false;
                selectedCategory = (Category) ti.getValue();
                if (selectedCategory.getParent() == 0) {
                } else {
                    cbParentCategory.getSelectionModel().selectFirst();
                    for (TreeItem<Category> ti2 : categoryTree.getRoot().getChildren()) {
                        if (!ti2.isLeaf()) {
                            parentCategory = ti2.getValue();
                            if (Objects.equals(parentCategory.getId(), selectedCategory.getParent())) {
                                cbParentCategory.getSelectionModel().select(parentCategory);
                                break;
                            }
                        }
                    }

                }
                if (event.getButton() == MouseButton.PRIMARY) {
                    contextMenu.hide();
                }
                if (event.getButton() == MouseButton.SECONDARY) {
                    switch (selectedCategory.getParent()) {
                        case 0:
                            // Allow adding to a parent category
                            addMenu.disableProperty().set(false);
                            // Don't allow deleting a parent
                            deleteMenu.disableProperty().set(true);
                            break;
                        default:
                            // Don't allow adding to a child category
                            addMenu.disableProperty().set(true);
                            // Allow deletion of child category
                            deleteMenu.disableProperty().set(false);
                            break;
                    }
                    contextMenu.show(categoryTree, Side.TOP, event.getX(), event.getY());
                }
                if (event.getClickCount() == 2) {
                    if (selectedCategory != null) {
                        //getLedgerManager().refresh(selectedItem);
                        //LedgerForm form = new LedgerForm(LedgerView.this, selectedItem);
                    }
                }
            }
        };

        categoryTree.addEventHandler(MouseEvent.MOUSE_CLICKED, clickHandler);

        List<Category> olist = CategoryManager.getInstance().getTree();
        selectedCategory = new Category();
        TreeItem<Category> root = new TreeItem<>(new Category(-1, "Primary Categories"));
        cbParentCategory.getItems().add(new Category(-1, ""));

        root.setExpanded(true);
        categoryTree.setShowRoot(true);

        btnCatAdd.disableProperty().set(true);
        btnCatDelete.disableProperty().set(true);

        // Load the resultSet into the TreeView
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
                .addListener(new ChangeListener<TreeItem<Category>>() {
                    @Override
                    public void changed(ObservableValue<? extends TreeItem<Category>> observable, TreeItem<Category> oldTreeItem, TreeItem<Category> selectedTreeItem) {
                        if (selectedTreeItem != null) {
                            if (selectedTreeItem.isLeaf()) {
                                btnCatDelete.disableProperty().set(false);
                                btnCatAdd.disableProperty().set(true);
                            } else {
                                btnCatDelete.disableProperty().set(true);
                                btnCatAdd.disableProperty().set(false);
                            }
                            selectedCategory = selectedTreeItem.getValue();
                            rollBackCategory = selectedCategory;
                            if (selectedTreeItem.getParent() == null) {
                                isRoot = true;
                                txtCategoryName.setText("");
                                return;
                            }
                            Integer parentId = selectedCategory.getParent();
                            cbParentCategory.disableProperty().setValue(parentId == 0);
                            txtCategoryName.disableProperty().set(false);
                            txtCategoryName.setText(selectedCategory.getDescription());
                            if (selectedCategory.getParent() > 0) {
                                btnCatOk.setDisable(false);
                                btnCatCancel.setDisable(false);
                                for (TreeItem ti : root.getChildren()) {
                                    Category p = (Category) ti.getValue();
                                    if (Objects.equals(p.getId(), parentId)) {
                                        cbParentCategory.getSelectionModel().select(p);
                                        break;
                                    }
                                }
                            }

                        }
                    }
                });

        txtCategoryName.textProperty().addListener((observable, oldValue, newValue) -> {
            selectedCategory.setDescription(newValue);
            btnCatOk.disableProperty().setValue(Boolean.FALSE);
            btnCatCancel.disableProperty().setValue(Boolean.FALSE);
        });
    }

    @FXML
    public void onBtnCatAdd() {
        isNew = true;
        if (selectedCategory.getId() != -1) {
            cbParentCategory.getSelectionModel().select(selectedCategory);
        }
        txtCategoryName.setText("");
        txtCategoryName.requestFocus();
        btnCatOk.disableProperty().set(false);
    }

    @FXML
    public void onBtnCatDelete() {
        isNew = false;
        TreeItem ti = categoryTree.getSelectionModel().getSelectedItem();
        if (ti.isLeaf()) {
            Category cat = (Category) ti.getValue();
            CategoryManager.getInstance().delete(cat);
            updateUi();
        }
    }

    @FXML
    public void onBtnCatOkClicked() {
        if (isNew) {
            Category child = new Category();
            child.setDescription(txtCategoryName.getText());
            TreeItem<Category> root = categoryTree.getRoot();
            if (isRoot) {
                child.setParent(0);
            } else {
                Category parent = cbParentCategory.getSelectionModel().getSelectedItem();
                child.setParent(parent.getId());
            }
            
            // Look for duplicate item
            for (TreeItem<Category> t1 : root.getChildren()) {
                Category cat = t1.getValue();
                if (Objects.equals(cat.getParent(), child.getParent())) {
                    if (cat.getDescription().equalsIgnoreCase(child.getDescription())) {
                        return;
                    }
                }
            }
            
            // Add the new item to the tree
            if (isRoot) {
                root.getChildren().add(new TreeItem<>(child));
            } else {
                for (TreeItem<Category> ti : root.getChildren()) {
                    Category c = ti.getValue();
                    if (Objects.equals(c.getId(), child.getParent())) {
                        ti.getChildren().add(new TreeItem<>(child));
                        break;
                    }
                }
            }
            
            // Add the new item to the database
            CategoryManager.getInstance().create(child);
        } else {
            CategoryManager.getInstance().update(selectedCategory);
        }
        btnCatOk.disableProperty().setValue(Boolean.TRUE);
        btnCatCancel.disableProperty().setValue(Boolean.TRUE);
        updateUi();
        isNew = false;
    }

    @FXML
    public void onBtnCatCancelClicked() {
        selectedCategory = rollBackCategory;
        updateUi();
    }

    private void updateUi() {
        TreeItem ti = categoryTree.getSelectionModel().getSelectedItem();
        if (ti.getParent() != null) {
            ti.setValue(selectedCategory);
            txtCategoryName.setText(selectedCategory.getDescription());
        }
        categoryTree.refresh();
        cbParentCategory.getSelectionModel().select(0);
        btnCatOk.disableProperty().setValue(Boolean.TRUE);
        btnCatCancel.disableProperty().setValue(Boolean.TRUE);
    }

    private final class CategoryCell extends TreeCell<Category> {

        private TextField txtField;
        private Category cat;

        public CategoryCell() {
            cat = new Category();
        }

        @Override
        public void startEdit() {
            super.startEdit();
            if (txtField == null) {
                createTextField();
            }
            setText(null);
            setGraphic(txtField);
            txtField.selectAll();
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
//            setText((String) getItem());
            setGraphic(getTreeItem().getGraphic());
        }

        @Override
        public void updateItem(Category item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else if (isEditing()) {
                if (txtField != null) {
                    txtField.setText(getString());
                }
                setText(null);
                setGraphic(txtField);
            } else {
                setText(getString());
                setGraphic(getTreeItem().getGraphic());
            }
        }

        private void createTextField() {
            txtField = new TextField(getString());
            txtField.setOnKeyReleased((KeyEvent t) -> {
                if (t.getCode() == KeyCode.ENTER) {
                    commitEdit(cat);
                    //commitEdit(txtField.getText());
                } else if (t.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }

    }
}
