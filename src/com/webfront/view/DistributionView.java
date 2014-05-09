/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.model.Receipts;
import static com.webfront.view.ReceiptsView.receiptsView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

/**
 *
 * @author rlittle
 */
public class DistributionView extends TableView {

    TableColumn transDescColumn;
    TableColumn storeColumn;
    TableColumn primaryCatColumn;
    TableColumn subCatColumn;
    TableColumn transAmtColumn;
    
    private ObservableList<Receipts> list;
    
    public DistributionView(ObservableList<Receipts> receiptsList) {
        list=receiptsList;

        transDescColumn = new TableColumn("Description");
        transDescColumn.setCellValueFactory(new PropertyValueFactory("transDesc"));
        transDescColumn.setMinWidth(200.0);

        storeColumn = new TableColumn("Store");
        storeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Receipts, String>, SimpleStringProperty>() {
            @Override
            public SimpleStringProperty call(TableColumn.CellDataFeatures<Receipts, String> param) {
                if (param.getValue().getStore() != null) {
                    return new SimpleStringProperty(param.getValue().getStore().getStoreName());
                }
                return null;
            }
        });
        storeColumn.setMinWidth(180.0);

        primaryCatColumn = new TableColumn("Cat 1");
        primaryCatColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Receipts, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Receipts, String> param) {
                if (param.getValue().getPrimaryCat() != null) {
                    return new SimpleStringProperty(param.getValue().getPrimaryCat().getDescription());
                }
                return null;
            }
        });
        primaryCatColumn.setMinWidth(190.0);

        subCatColumn = new TableColumn("Cat 2");
        subCatColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Receipts, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Receipts, String> param) {
                if (param.getValue().getSubCat() != null) {
                    return new SimpleStringProperty(param.getValue().getSubCat().getDescription());
                }
                return null;
            }
        });
        subCatColumn.setMinWidth(200.0);

        transAmtColumn = new TableColumn("Amount");
        transAmtColumn.setCellValueFactory(new PropertyValueFactory("transAmt"));
        transAmtColumn.setCellFactory(new CellFormatter<>(TextAlignment.RIGHT));
        //transAmtColumn.setMinWidth(100.0);

        getColumns().addAll(transDescColumn,storeColumn,primaryCatColumn,subCatColumn,transAmtColumn);

        EventHandler<MouseEvent> click = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    Receipts receipt = (Receipts) getSelectionModel().getSelectedItem();
                    ReceiptForm receiptForm = new ReceiptForm(receiptsView, receipt);
                }
            }
        };

        addEventHandler(MouseEvent.MOUSE_CLICKED, click);
        setItems(list);
    }

    /**
     * @return the list
     */
    public ObservableList<Receipts> getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(ObservableList<Receipts> list) {
        this.list = list;
    }
}
