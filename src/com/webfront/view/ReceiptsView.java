/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.bean.CategoryManager;
import com.webfront.bean.LedgerManager;
import com.webfront.bean.ReceiptsManager;
import com.webfront.bean.StoresManager;
import com.webfront.model.Category;
import com.webfront.model.Ledger;
import com.webfront.model.Receipts;
import com.webfront.model.Stores;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

/**
 *
 * @author rlittle
 */
public class ReceiptsView extends Pane {

    static ReceiptsView receiptsView;

    private ObservableList<Receipts> list;
    private ObservableList<Stores> storeList;
    private ObservableList<Category> categoryList;
    private TableView<Receipts> table;
    private BooleanProperty storeAdded;

    final ReceiptsManager receiptsManager;
    final StoresManager storesManager;
    final CategoryManager categoryManager;
    private final LedgerManager ledgerManager;

    TableColumn idColumn;
    TableColumn transDateColumn;
    TableColumn transDescColumn;
    TableColumn transIdColumn;
    TableColumn storeColumn;
    TableColumn primaryCatColumn;
    TableColumn subCatColumn;
    TableColumn accountNumColumn;
    TableColumn transAmtColumn;

    Button btnAdd;

    public ReceiptsView() {
        super();
        receiptsView = this;
        storeAdded = new SimpleBooleanProperty();
        storeAdded.set(false);
        GridPane grid = new GridPane();

        receiptsManager = new ReceiptsManager();
        storesManager = new StoresManager();
        categoryManager = new CategoryManager();
        ledgerManager = new LedgerManager();

        storeList = FXCollections.observableArrayList();
        categoryList = (ObservableList<Category>) categoryManager.getCategories();
        list = receiptsManager.getList("SELECT * FROM receipts ORDER BY transDate DESC");

        btnAdd = new Button("Add Receipt");
        btnAdd.setOnAction((ActionEvent event) -> {
            ReceiptForm receiptForm = new ReceiptForm(receiptsView, new Receipts());
        });

        table = new TableView<>();
        table.setMinWidth(1300.0);
        table.setMinHeight(600.0);
        table.setEditable(true);

        idColumn = new TableColumn("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory("id"));
        idColumn.setMinWidth(50.0);

        transDateColumn = new TableColumn("Date");
        transDateColumn.setCellValueFactory(new PropertyValueFactory("transDate"));
        transDateColumn.setCellFactory(new CellFormatter<>());
        transDateColumn.setMinWidth(100.0);

        transDescColumn = new TableColumn("Description");
        transDescColumn.setCellValueFactory(new PropertyValueFactory("transDesc"));
        transDescColumn.setMinWidth(300.0);
        transDescColumn.setMaxWidth(315.0);

        transIdColumn = new TableColumn("Trans");
        transIdColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Receipts, String>, SimpleStringProperty>() {
            @Override
            public SimpleStringProperty call(TableColumn.CellDataFeatures<Receipts, String> param) {
                if (param.getValue().getLedgerEntry() != null) {
                    // From this receipt (param.getValue()) get the Ledger item
                    Ledger l = param.getValue().getLedgerEntry();
                    if (!l.getReceipts().isEmpty()) {
                        return new SimpleStringProperty(l.getId().toString());
                    }
                }
                return null;
            }
        });
        transIdColumn.setMinWidth(50.0);

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
        storeColumn.setMinWidth(50.0);

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
        primaryCatColumn.setMinWidth(50.0);

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
        subCatColumn.setMinWidth(50.0);

        accountNumColumn = new TableColumn("Acct");
        accountNumColumn.setCellValueFactory(new PropertyValueFactory("accountNum"));
        accountNumColumn.setMinWidth(50.0);

        transAmtColumn = new TableColumn("Amount");
        transAmtColumn.setCellValueFactory(new PropertyValueFactory("transAmt"));
        transAmtColumn.setCellFactory(new CellFormatter<>(TextAlignment.RIGHT));
        transAmtColumn.setMinWidth(100.0);

        table.getColumns().add(idColumn);
        table.getColumns().add(transDateColumn);
        table.getColumns().add(transDescColumn);
        table.getColumns().add(storeColumn);
        table.getColumns().add(primaryCatColumn);
        table.getColumns().add(subCatColumn);
        table.getColumns().add(accountNumColumn);
        table.getColumns().add(transIdColumn);
        table.getColumns().add(transAmtColumn);

        EventHandler<MouseEvent> click = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    Receipts receipt = (Receipts) table.getSelectionModel().getSelectedItem();
                    ReceiptForm receiptForm = new ReceiptForm(receiptsView, receipt);
                }
            }
        };

        table.addEventHandler(MouseEvent.MOUSE_CLICKED, click);
        table.setItems(list);

        grid.setHgap(10.0);
        grid.add(table, 0, 0);
        HBox buttons = new HBox();
        buttons.setAlignment(Pos.BOTTOM_RIGHT);
        buttons.setPadding(new Insets(10, 10, 0, 10));
        buttons.setSpacing(10.0);
        buttons.getChildren().add(btnAdd);
        grid.add(buttons, 0, 1);
        getChildren().add(grid);
    }

    /**
     * @return the table
     */
    public TableView<Receipts> getTable() {
        return table;
    }

    /**
     * @param table the table to set
     */
    public void setTable(TableView<Receipts> table) {
        this.table = table;
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

    /**
     * @return the storeList
     */
    public ObservableList<Stores> getStoreList() {
        return storeList;
    }

    /**
     * @param storeList the storeList to set
     */
    public void setStoreList(ObservableList<Stores> storeList) {
        this.storeList = storeList;
    }

    /**
     * @return the categoryList
     */
    public ObservableList<Category> getCategoryList() {
        return categoryList;
    }

    /**
     * @param categoryList the categoryList to set
     */
    public void setCategoryList(ObservableList<Category> categoryList) {
        this.categoryList = categoryList;
    }

    /**
     * @return the ledgerManager
     */
    public LedgerManager getLedgerManager() {
        return ledgerManager;
    }

    /**
     * @return the storeAdded
     */
    public BooleanProperty getStoreAdded() {
        return storeAdded;
    }

    /**
     * @param storeAdded the storeAdded to set
     */
    public void setStoreAdded(BooleanProperty storeAdded) {
        this.storeAdded = storeAdded;
    }
}
