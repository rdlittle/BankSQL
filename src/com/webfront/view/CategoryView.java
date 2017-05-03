/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.bean.CategoryManager;
import com.webfront.model.Category;
import java.util.List;
import java.util.Objects;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;

/**
 *
 * @author rlittle
 */
public class CategoryView extends Pane {

    private final ObservableList<Category> list;
    private static CategoryView instance;
    private final CategoryManager categoryManager;
    private TreeView<Category> treeView;
    private ChangeListener<Tab> tabChangeListener;

    protected CategoryView() {
        list = FXCollections.<Category>observableArrayList();
        categoryManager = CategoryManager.getInstance();
        tabChangeListener = new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
                
            }
        };
        init();
    }

    private void init() {
        List<Category> olist = categoryManager.getTree();
        TreeItem<Category> root = new TreeItem<>(new Category(-1,"Primary Categories"));
        treeView = new TreeView(root);
        root.setExpanded(true);
        treeView.setShowRoot(true);
        treeView.setPrefHeight(500);
        
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
        categoryManager.addListener(listListener);
        treeView.getSelectionModel().selectedItemProperty().addListener(treeListener);
    }
    
    public synchronized TreeView<Category> getTreeView() {
        return treeView;
    }

    public synchronized static CategoryView getInstance() {
        if (instance == null) {
            instance = new CategoryView();
        }
        return instance;
    }

    // Listen for a change in the list stored in CategoryManager
    private final ListChangeListener<Category> listListener = new ListChangeListener<Category>() {
        @Override
        public void onChanged(ListChangeListener.Change<? extends Category> c) {
            if (Platform.isFxApplicationThread()) {
                updateTreeView(c);
            } else {
                Platform.runLater(() -> updateTreeView(c));
            }
        }
    };

    private void updateTreeView(ListChangeListener.Change<? extends Category> change) {
        if (change.wasAdded()) {
            while(change.next()) {
                
            }
        } else if (change.wasUpdated()) {
        } else if (change.wasRemoved()) {
        }
    }

    private final ChangeListener<TreeItem<Category>> treeListener
            = (ov, oldValue, newValue) -> {

            };

    public void addListener(ListChangeListener<? super Category> listListener) {
        list.addListener(listListener);
    }

    public void removeListener(ListChangeListener<? super Category> listListener) {
        list.removeListener(listListener);
    }

    public void addListener(InvalidationListener listener) {
        list.addListener(listener);
    }

    public void removeListener(InvalidationListener listener) {
        list.removeListener(listener);
    }
    
    public void addListener(ChangeListener<Tab> listener) {
        tabChangeListener = listener;
    }
    
    public void removeListener(ChangeListener<Tab> listener) {
        
    }

}
