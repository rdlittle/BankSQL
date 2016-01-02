/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.bean.CategoryManager;
import com.webfront.bean.LedgerManager;
import com.webfront.bean.PaymentManager;
import com.webfront.bean.StoresManager;
import com.webfront.model.Category;
import com.webfront.model.Ledger;
import com.webfront.model.Payment;
import com.webfront.model.Stores;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
public class PaymentView extends Pane {

    private static PaymentView paymentView;

    private final ObservableList<Payment> list;
    private ObservableList<Stores> storeList;
    private ObservableList<Category> categoryList;
    private TableView<Payment> table;
    private BooleanProperty storeAdded;

    private final PaymentManager paymentManager;
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

    public PaymentView() {
        super();
        storeAdded = new SimpleBooleanProperty();
        storeAdded.set(false);
        GridPane grid = new GridPane();

        paymentManager = new PaymentManager();
        storesManager = new StoresManager();
        categoryManager = CategoryManager.getInstance();
        ledgerManager = LedgerManager.getInstance();

        storeList = FXCollections.observableArrayList();
        categoryList = (ObservableList<Category>) categoryManager.getCategories();
        list = FXCollections.<Payment>observableArrayList();
        Platform.runLater(() -> loadData());

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
        transDescColumn.setMinWidth(290.0);
        transDescColumn.setMaxWidth(315.0);

        transIdColumn = new TableColumn("Trans");
        transIdColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Payment, String>, SimpleStringProperty>() {
            @Override
            public SimpleStringProperty call(TableColumn.CellDataFeatures<Payment, String> param) {
                if (param.getValue().getLedgerEntry() != null) {
                    // From this payment (param.getValue()) get the Ledger item
                    Ledger l = param.getValue().getLedgerEntry();
                    List<Payment> tmpList = l.getPayment();
                    if (l.getPayment() != null && l.getPayment().size() > 0) {
                        return new SimpleStringProperty(l.getId().toString());
                    }
                    if (l.getPayment() == null) {
                        System.out.println(param.getValue().getId() + " Ledger.getPayment() is null when in PaymentView.transIdColumn callback");
                    } else {
                        System.out.println(param.getValue().getId() + " Ledger.getPayment() size is " + l.getPayment().size() + " when in PaymentView.transIdColumn callback");
                    }
                }
                return null;
            }
        });
        transIdColumn.setMinWidth(50.0);

        storeColumn = new TableColumn("Store");
        storeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Payment, String>, SimpleStringProperty>() {
            @Override
            public SimpleStringProperty call(TableColumn.CellDataFeatures<Payment, String> param) {
                if (param.getValue().getStore() != null) {
                    return new SimpleStringProperty(param.getValue().getStore().getStoreName());
                }
                return null;
            }
        });
        storeColumn.setMinWidth(180.0);

        primaryCatColumn = new TableColumn("Cat 1");
        primaryCatColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Payment, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Payment, String> param) {
                if (param.getValue().getPrimaryCat() != null) {
                    return new SimpleStringProperty(param.getValue().getPrimaryCat().getDescription());
                }
                return null;
            }
        });
        primaryCatColumn.setMinWidth(180.0);

        subCatColumn = new TableColumn("Cat 2");
        subCatColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Payment, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Payment, String> param) {
                if (param.getValue().getSubCat() != null) {
                    return new SimpleStringProperty(param.getValue().getSubCat().getDescription());
                }
                return null;
            }
        });
        subCatColumn.setMinWidth(220.0);

        accountNumColumn = new TableColumn("Acct");
        accountNumColumn.setCellValueFactory(new PropertyValueFactory("accountNum"));
        accountNumColumn.setMinWidth(20.0);

        transAmtColumn = new TableColumn("Amount");
        transAmtColumn.setCellValueFactory(new PropertyValueFactory("transAmt"));
        transAmtColumn.setCellFactory(new CellFormatter<>(TextAlignment.RIGHT));
        transAmtColumn.setMinWidth(100.0);
        
        list.addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change c) {
                table.getItems().addAll(list);
            }
        });        

        table.getColumns().add(idColumn);
        table.getColumns().add(transDateColumn);
        table.getColumns().add(transDescColumn);
        table.getColumns().add(storeColumn);
        table.getColumns().add(primaryCatColumn);
        table.getColumns().add(subCatColumn);
        table.getColumns().add(accountNumColumn);
        table.getColumns().add(transIdColumn);
        table.getColumns().add(transAmtColumn);

        btnAdd = new Button("Add Receipt");
        btnAdd.setOnAction((ActionEvent event) -> {
            PaymentForm paymentForm = new PaymentForm(getInstance(), new Payment());
        });

        EventHandler<MouseEvent> click = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    Payment payment = (Payment) table.getSelectionModel().getSelectedItem();
                    PaymentForm paymentForm = new PaymentForm(paymentView, payment);
                }
            }
        };

        table.addEventHandler(MouseEvent.MOUSE_CLICKED, click);

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

    public static PaymentView getInstance() {
        if (paymentView == null) {
            paymentView = new PaymentView();
        }
        return paymentView;
    }

    private void loadData() {
        list.setAll(paymentManager.getList("SELECT * FROM payment ORDER BY transDate DESC"));
    }
    /**
     * @return the table
     */
    public TableView<Payment> getTable() {
        return table;
    }

    /**
     * @param table the table to set
     */
    public void setTable(TableView<Payment> table) {
        this.table = table;
    }

    /**
     * @return the list
     */
    public ObservableList<Payment> getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(ObservableList<Payment> list) {
        this.list.setAll(list);
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

    /**
     * @return the paymentManager
     */
    public PaymentManager getPaymentManager() {
        return paymentManager;
    }
}
