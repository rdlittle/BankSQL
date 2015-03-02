/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.bean.CategoryManager;
import com.webfront.bean.DBManager;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

public abstract class TransactionView<T> extends Pane {
    private T type;
    public int accountId;
    private TransactionView view;
    private TableView table;
    private ObservableList list;
    private DBManager dbManager;
    private CategoryManager categoryManager;
    
    private TableColumn dateColumn;
    private TableColumn descColumn;
    private TableColumn primaryCatColumn;
    private TableColumn<T, String> subCatColumn;
    private TableColumn<T, Float> transAmtColumn;
    
    Button btnSearch;
    Button btnReset;

    public TransactionView() {
    }
    
    public void set(T t) {
        type=t;
    }
    
    public T get() {
        return type;
    }
}
