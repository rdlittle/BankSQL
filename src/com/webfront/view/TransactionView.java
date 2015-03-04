/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.bean.CategoryManager;
import com.webfront.bean.DBManager;
import com.webfront.model.Category;
import com.webfront.model.Entry;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import static javafx.scene.layout.Region.USE_PREF_SIZE;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

public abstract class TransactionView extends Pane {

    public int accountId;
    private TableView table;
    private ObservableList list;
    public DBManager dbManager;
    private CategoryManager categoryManager;

    private TableColumn dateColumn;
    private TableColumn descColumn;
    private TableColumn primaryCatColumn;
    private TableColumn<Entry, String> subCatColumn;
    private TableColumn<Entry, Float> transAmtColumn;

    Button btnSearch;
    Button btnReset;

    public TransactionView() {
    }

    public abstract void setDbManager();

    public void init() {
        table = new TableView<>();
        table.setMaxWidth(USE_PREF_SIZE);

        dateColumn = new TableColumn("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory("transDate"));
        dateColumn.setCellFactory(new CellFormatter<>());

        descColumn = new TableColumn("Description");
        descColumn.setCellValueFactory(new PropertyValueFactory("transDesc"));
        descColumn.setMinWidth(620.00);

        primaryCatColumn = new TableColumn("Cat 1");
        primaryCatColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Entry, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Entry, String> param) {
                if (param.getValue().getPrimaryCat() != null) {
                    return new SimpleStringProperty(param.getValue().getPrimaryCat().getDescription());
                }
                return null;
            }
        });        
        
        primaryCatColumn.setMinWidth(150.0);

        subCatColumn = new TableColumn("Cat 2");
        subCatColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Entry, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Entry, String> param) {
                if (param.getValue().getDistribution() != null) {
                    if (!param.getValue().getDistribution().isEmpty()) {
                        Category c = param.getValue().getDistribution().get(0).getCategory();
                        if (c != null) {
                            String desc = c.getDescription();
                            return new SimpleStringProperty(desc);
                        }
                    }
                }
                return null;
            }
        });
        
        subCatColumn.setMinWidth(215.0);

        transAmtColumn = new TableColumn("Amount");
        transAmtColumn.setCellValueFactory(new PropertyValueFactory<>("transAmt"));
        transAmtColumn.setMinWidth(100.0);
        transAmtColumn.setCellFactory(new CellFormatter<>(TextAlignment.RIGHT));
    }
}
