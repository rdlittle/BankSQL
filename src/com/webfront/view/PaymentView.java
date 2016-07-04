/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.bean.AccountManager;
import com.webfront.bean.CategoryManager;
import com.webfront.bean.LedgerManager;
import com.webfront.bean.PaymentManager;
import com.webfront.bean.StoresManager;
import com.webfront.model.Category;
import com.webfront.model.Ledger;
import com.webfront.model.Payment;
import com.webfront.model.Stores;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
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
public class PaymentView extends Pane implements ViewInterface {

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

    private ReadOnlyObjectWrapper<Payment> selectedPaymentProperty;
    protected Payment prevPayment;
    protected Payment prevPaymentCopy;

    private final DeleteListener deleteListener = new DeleteListener();
    private final CreateListener createListener = new CreateListener();
    private final UpdateListener updateListener = new UpdateListener();

    Button btnAdd;

    protected PaymentView() {
        super();

        storeAdded = new SimpleBooleanProperty();
        selectedPaymentProperty = new ReadOnlyObjectWrapper<>();

        storeAdded.set(false);
        GridPane grid = new GridPane();

        paymentManager = PaymentManager.getInstance();
        storesManager = StoresManager.getInstance();
        categoryManager = CategoryManager.getInstance();
        ledgerManager = LedgerManager.getInstance();

        storeList = FXCollections.observableArrayList();
        categoryList = (ObservableList<Category>) categoryManager.getCategories();
        list = FXCollections.<Payment>observableArrayList();
        Platform.runLater(() -> loadData());

        table = new TableView<>();
        table.setMinWidth(1325.0);
        table.setMinHeight(640.0);
        table.setEditable(true);

        idColumn = new TableColumn("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory("id"));
        idColumn.setMinWidth(10.0);
        idColumn.setMaxWidth(55.0);

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
//                    List<Payment> tmpList = l.getPayment();
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

        paymentManager.addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change c) {
                list.setAll(paymentManager.getList(""));
                table.getItems().setAll(list);
                paymentManager.removeListener(this);
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

        /*
            Open a new PayementForm, add ListChangeListener
         */
        btnAdd.setOnAction((ActionEvent event) -> {
            selectedPaymentProperty().setValue(new Payment());
            showPaymentForm();
        });

        EventHandler<MouseEvent> click;
        click = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    prevPayment = (Payment) table.getSelectionModel().getSelectedItem();
                    showPaymentForm();
                } else if (event.isSecondaryButtonDown()) {

                }
            }
        };

        table.addEventHandler(MouseEvent.MOUSE_CLICKED, click);

        grid.setHgap(10.0);
        grid.add(table, 0, 0);
        HBox buttons = new HBox();
        buttons.setAlignment(Pos.BOTTOM_RIGHT);
        buttons.setPadding(new Insets(10, 10, 10, 10));
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

    public void showPaymentForm() {
        ListChangeListener<Payment> listListener = new ListChangeListener<Payment>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Payment> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        for (Payment p : c.getAddedSubList()) {
                            table.getItems().add(p);
                        }
                    } else if (c.wasRemoved()) {
                        for (Payment p : c.getRemoved()) {
                            table.getItems().remove(p);
                        }
                    } else if (c.wasUpdated()) {
                    }
                }
                getTable().getItems().sort(Payment.PaymentComparator);
            }
        };
        list.addListener(listListener);

        prevPaymentCopy = Payment.copy(prevPayment);
        selectedPaymentProperty().setValue(prevPaymentCopy);
        PaymentForm paymentForm = new PaymentForm(prevPayment);

        paymentForm.getCreatedProperty().addListener(createListener);
        paymentForm.getUpdatedProperty().addListener(updateListener);
        paymentForm.getDeletedProperty().addListener(deleteListener);

//        getSelectedPaymentProperty().setValue(prevPayment);
        paymentForm.stage.setOnCloseRequest(new EventHandler() {
            @Override
            public void handle(Event event) {
                int idx = list.indexOf(selectedPaymentProperty().getValue());
                Payment p = selectedPaymentProperty().getValue();
                handlePettyCash(prevPayment, p);
                if (p.getLedgerEntry() != null) {
                    p.getLedgerEntry().getPayment().add(p);
                }
                list.removeListener(listListener);
                selectedPaymentProperty().unbindBidirectional(paymentForm.getSelectedPayment());
                paymentForm.getCreatedProperty().removeListener(createListener);
                paymentForm.getUpdatedProperty().removeListener(updateListener);
                paymentForm.getDeletedProperty().removeListener(deleteListener);
            }
        });

    }

    private void loadData() {
        list.setAll(paymentManager.getList(""));
    }

    /**
     * @return the table
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
    public PaymentManager getPaymentManager() {
        return paymentManager;
    }

    @Override
    public void updateItem(Payment p) {
        int idx = getTable().getSelectionModel().getSelectedIndex();
        getTable().getItems().set(idx, p);
        PaymentManager.getInstance().update(p);
    }

    @Override
    public void removeItem(Payment p) {
        int idx = getTable().getSelectionModel().getSelectedIndex();
        getTable().getItems().remove(idx);
    }

    @Override
    public void updateTrans(int idx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Ledger getLedgerItem(String id) {
        return LedgerManager.getInstance().getItem(Integer.parseInt(id));
    }

    public void handlePettyCash(Payment prev, Payment curr) {
        Integer cashAcct = AccountManager.getInstance().getAccount("Petty Cash").getId();
        Float amount = curr.getTransAmt();
        if (prev == null || curr == null) {
            return;
        }

        if (prev.getAccountNum() == curr.getAccountNum()) {
            return;
        }

        if (prev.getAccountNum() != cashAcct && curr.getAccountNum() != cashAcct) {
            return;
        }

        if (prev.getAccountNum() == cashAcct) {
            // Reverse the previous petty cash ledger entry if any
            Ledger l = prev.getLedgerEntry();
            if(l != null && l.getAccountNum() == cashAcct) {
                LedgerManager.getInstance().delete(l);
                prev.setLedgerEntry(null);
            }
        } else {
            Float bal = LedgerManager.getInstance().getBalance(cashAcct);
            curr.setTransAmt(curr.getTransAmt() * -1);
            Ledger l = curr.getLedgerEntry();
            if(l == null) {
                l = new Ledger();
                l.setAccountNum(cashAcct);
                l.setTransDate(curr.getTransDate());
                l.setTransDesc(curr.getTransDesc());
                l.setTransAmt(curr.getTransAmt());
                l.setTransBal(bal - curr.getTransAmt());
                ArrayList<Payment> pList = new ArrayList<>();
                pList.add(curr);
                l.setPayment(pList);
                curr.setLedgerEntry(l);
            }
        }
    }

    /**
     * @return the selectedPaymentProperty
     */
    public SimpleObjectProperty<Payment> selectedPaymentProperty() {
        return selectedPaymentProperty;
    }

    /**
     * @param selectedPaymentProperty the selectedPaymentProperty to set
     */
//    public void setSelectedPaymentProperty(ReadOnlyObjectWrapper<Payment> selectedPaymentProperty) {
//        this.selectedPaymentProperty = selectedPaymentProperty;
//    }
    private class DeleteListener implements InvalidationListener {

        @Override
        public void invalidated(Observable observable) {
            getPaymentManager().delete(selectedPaymentProperty().getValue());
            removeItem(selectedPaymentProperty().getValue());
        }

    }

    private class UpdateListener implements InvalidationListener {

        @Override
        public void invalidated(Observable observable) {
            handlePettyCash(selectedPaymentProperty().getValue(), prevPayment);
            updateItem(prevPayment);
            selectedPaymentProperty().setValue(prevPayment);
        }

    }

    private class CreateListener implements InvalidationListener {

        @Override
        public void invalidated(Observable observable) {
            getPaymentManager().create(selectedPaymentProperty().getValue());
            getList().add(selectedPaymentProperty().getValue());
        }

    }

}
