/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.controller;

import com.webfront.bean.CategoryManager;
import com.webfront.model.Category;
import com.webfront.model.Ledger;
import com.webfront.view.LedgerForm;
import com.webfront.view.LedgerView;
import java.util.List;
import java.util.Objects;
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
    Category rollBackCategory;

    private MenuItem addMenu;
    private MenuItem editMenu;
    private MenuItem deleteMenu;
    private ContextMenu contextMenu;

    @FXML
    public void initialize() {
        contextMenu = new ContextMenu();
        addMenu = new MenuItem("Add");
        editMenu = new MenuItem("Edit");
        deleteMenu = new MenuItem("Delete");

        addMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });

        deleteMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });

        editMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });

        contextMenu.getItems().addAll(addMenu, editMenu, deleteMenu);

        EventHandler<MouseEvent> clickHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(null == categoryTree.getSelectionModel().getSelectedItem().getValue()) {
                    return;
                }
                selectedCategory = (Category) categoryTree.getSelectionModel().getSelectedItem().getValue();
                if (event.getButton() == MouseButton.PRIMARY) {
                    contextMenu.hide();
                }
                if (event.getButton() == MouseButton.SECONDARY) {
                    switch(selectedCategory.getParent()) {
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

        btnCatDelete.disableProperty().set(true);
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
                        if (newValue.isLeaf()) {
                            btnCatDelete.disableProperty().set(false);
                        } else {
                            btnCatDelete.disableProperty().set(true);
                        }
                        selectedCategory = newValue.getValue();
                        rollBackCategory = selectedCategory;
                        Integer parentId = selectedCategory.getParent();
                        if (parentId == null) {
                            txtCategoryName.disableProperty().set(true);
                            btnCatOk.setDisable(true);
                            btnCatCancel.setDisable(true);
                            return;
                        }
                        cbParentCategory.disableProperty().setValue(parentId == 0);
                        txtCategoryName.disableProperty().set(false);
                        if (selectedCategory.getId() > 0) {
                            txtCategoryName.setText(selectedCategory.getDescription());
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
                });

        txtCategoryName.textProperty().addListener((observable, oldValue, newValue) -> {
            selectedCategory.setDescription(newValue);
            btnCatOk.disableProperty().setValue(Boolean.FALSE);
            btnCatCancel.disableProperty().setValue(Boolean.FALSE);
        });
    }

    @FXML
    public void onBtnCatAdd() {
          
//        txtCategoryName.requestFocus();
//        btnCatOk.disableProperty().set(false);
    }

    @FXML
    public void onBtnCatDelete() {

    }

    @FXML
    public void onBtnCatOkClicked() {
        CategoryManager.getInstance().update(selectedCategory);
        btnCatOk.disableProperty().setValue(Boolean.TRUE);
        btnCatCancel.disableProperty().setValue(Boolean.TRUE);
        updateUi();
    }

    @FXML
    public void onBtnCatCancelClicked() {
        selectedCategory = rollBackCategory;
        updateUi();
    }

    private void updateUi() {
        TreeItem ti = categoryTree.getSelectionModel().getSelectedItem();
        ti.setValue(selectedCategory);
        categoryTree.refresh();
        txtCategoryName.setText(selectedCategory.getDescription());
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
