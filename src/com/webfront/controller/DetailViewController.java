/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.controller;

import com.webfront.bean.CategoryManager;
import com.webfront.bean.LedgerManager;
import com.webfront.bean.PaymentManager;
import com.webfront.bean.StoresManager;
import com.webfront.model.Account;
import com.webfront.model.Category;
import com.webfront.model.Ledger;
import com.webfront.model.Payment;
import com.webfront.model.Stores;
import com.webfront.view.PaymentForm;
import com.webfront.view.ViewInterface;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.ComboBoxTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author rlittle
 */
public class DetailViewController implements Initializable, ViewInterface {

    @FXML
    TreeTableView<Ledger> table;
    @FXML
    TreeTableColumn<Ledger, Integer> idColumn;
    @FXML
    TreeTableColumn<Ledger, Date> dateColumn;
    @FXML
    TreeTableColumn<Ledger, String> checkNumColumn;
    @FXML
    TreeTableColumn<Ledger, String> descriptionColumn;
    @FXML
    TreeTableColumn<Ledger, Float> amountColumn;
    @FXML
    TreeTableColumn<Payment, String> detailItemColumn;
    @FXML
    TreeTableColumn<Payment, Float> detailAmountColumn;
    @FXML
    TreeTableColumn<Payment, Category> categoryColumn;
    @FXML
    TreeTableColumn<Payment, Category> detailCat1Column;
    @FXML
    TreeTableColumn<Payment, Category> detailCat2Column;

    @FXML
    HBox buttonPanel;

    private final String dateFormat = "MM/dd/yyyy";
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);

    private final String decimalFormat = "###0.00";
    private final DecimalFormat decimalFormatter = new DecimalFormat(decimalFormat);

    public int accountNumber = 1;
    private final ObservableList<Ledger> list = FXCollections.<Ledger>observableArrayList();
    private final LedgerManager ledgerManager = LedgerManager.getInstance();

    private TreeItem<Ledger> root;
    private ObservableList<Category> parentList;
    private ObservableList<Category> childList;

    private Ledger selectedLedgerItem;
    private Ledger ledgerRollbackValue;
    private Payment selectedPaymentItem;
    private Payment paymentRollbackValue;
    SimpleObjectProperty<Payment> selectedPaymentProperty = new SimpleObjectProperty<>();
    int selectedRow;
    private ListChangeListener<Payment> paymentListListener;

    private final DeleteListener deleteListener = new DeleteListener();
    private final CreateListener createListener = new CreateListener();
    private final UpdateListener updateListener = new UpdateListener();

    public DetailViewController() {

    }

    @Override
    public BooleanProperty getStoreAdded() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ObservableList<Stores> getStoreList() {
        return StoresManager.getInstance().getList("");
    }

    @Override
    public Control getTable() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PaymentManager getPaymentManager() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ObservableList<Payment> getList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateItem(Payment p) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeItem(Payment p) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateTrans(int idx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Ledger getLedgerItem(String id) {
        return getLedgerManager().getItem(Integer.parseInt(id));
    }

    @Override
    public LedgerManager getLedgerManager() {
        return LedgerManager.getInstance();
    }

    enum ItemType {
        PAYMENT, LEDGER;
    }
    ItemType selectedItemType;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @FXML
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        selectedLedgerItem = new Ledger();
        root = new TreeItem<>();
        table.showRootProperty().set(false);
        parentList = FXCollections.<Category>observableArrayList(CategoryManager.getInstance().getList("Category.findAllParent"));
        childList = FXCollections.<Category>observableArrayList();

        decimalFormatter.setMaximumFractionDigits(2);

        // Ledger fields
        idColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("id"));
        dateColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("transDate"));
        descriptionColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("transDesc"));
        amountColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("transAmt"));

        dateColumn.setCellFactory(new Callback<TreeTableColumn<Ledger, Date>, TreeTableCell<Ledger, Date>>() {
            @Override
            public TreeTableCell<Ledger, Date> call(TreeTableColumn<Ledger, Date> p) {
                return new TreeTableCell<Ledger, Date>() {
                    @Override
                    protected void updateItem(Date item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(dateFormatter.format(item));
                        }
                    }
                };
            }
        });

        amountColumn.setCellFactory(new Callback<TreeTableColumn<Ledger, Float>, TreeTableCell<Ledger, Float>>() {
            @Override
            public TreeTableCell<Ledger, Float> call(TreeTableColumn<Ledger, Float> p) {
                return new TreeTableCell<Ledger, Float>() {
                    @Override
                    protected void updateItem(Float item, boolean empty) {
                        super.setAlignment(Pos.CENTER_RIGHT);
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(decimalFormatter.format(item));
                        }
                    }
                };
            }
        });

        // Payment fields
        detailItemColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("transDesc"));
        detailAmountColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("transAmt"));

        detailCat1Column.setCellFactory(ComboBoxTreeTableCell.<Payment, Category>forTreeTableColumn(parentList));
        detailCat1Column.setCellValueFactory(new TreeItemPropertyValueFactory<>("primaryCat"));

        detailCat2Column.setCellFactory(ComboBoxTreeTableCell.<Payment, Category>forTreeTableColumn(childList));
        detailCat2Column.setCellValueFactory(new TreeItemPropertyValueFactory<>("subCat"));

        detailAmountColumn.setCellFactory(new Callback<TreeTableColumn<Payment, Float>, TreeTableCell<Payment, Float>>() {
            @Override
            public TreeTableCell<Payment, Float> call(TreeTableColumn<Payment, Float> p) {
                return new TreeTableCell<Payment, Float>() {
                    @Override
                    protected void updateItem(Float item, boolean empty) {
                        super.setAlignment(Pos.CENTER_RIGHT);
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(decimalFormatter.format(item));
                        }
                    }
                };
            }
        });

        table.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue == null) {
                    return;
                }
                Object obj = ((TreeItem) newValue).getValue();
                if (obj instanceof com.webfront.model.Ledger) {
                    selectedLedgerItem = (Ledger) obj;
                    selectedItemType = ItemType.LEDGER;
                } else {
                    selectedPaymentItem = (Payment) obj;
                    selectedItemType = ItemType.PAYMENT;
                }
            }
        });
        Platform.runLater(() -> loadData());
    }

    public void loadData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("accountType", Account.AccountType.CHECKING);
        map.put("accountStatus", Account.AccountStatus.ACTIVE);
        list.setAll(ledgerManager.doNamedQuery("Ledger.findAllByType", map));
        Ledger unAssignedPayments = new Ledger();
        TreeItem orphans = new TreeItem<>(unAssignedPayments);
        for (Ledger l : list) {
            TreeItem ti = new TreeItem<>(l);
            for (Payment p : l.getPayment()) {
                ti.getChildren().add(new TreeItem<Payment>(p));
            }
            root.getChildren().add(ti);
        }
        table.setRoot(root);
    }

    @FXML
    public void onDetailCat1EditStart(TreeTableColumn.CellEditEvent<Payment, Category> event) {
        TreeItem ti = event.getRowValue();
        Object obj = ti.getValue();
        if (obj instanceof com.webfront.model.Ledger) {
            ledgerRollbackValue = (Ledger) obj;
        } else {
            paymentRollbackValue = (Payment) obj;
        }
    }

    @FXML
    public void onDetailCat1EditCommit(TreeTableColumn.CellEditEvent<Payment, Category> event) {
        Category newCat = event.getNewValue();
        Category oldCat = event.getOldValue();
        if (newCat == oldCat) {
            return;
        }
        TreeItem ti = event.getRowValue();
        Object obj = ti.getValue();
        if (obj instanceof com.webfront.model.Ledger) {
            Ledger l = (Ledger) obj;
            l.setPrimaryCat(newCat);

            ti.setValue(l);
        } else {
            Payment p = (Payment) obj;
            p.setPrimaryCat(newCat);
            ti.setValue(p);
        }
        childList.clear();
        childList.setAll(CategoryManager.getInstance().getChildren(newCat.getId()));
        if (selectedItemType == ItemType.LEDGER) {
            LedgerManager.getInstance().update((Ledger) obj);
        } else {
            PaymentManager.getInstance().update((Payment) obj);
        }
    }

    @FXML
    public void onDetailCat1EditCancel(TreeTableColumn.CellEditEvent<Object, Object> event) {

    }

    @FXML
    public void onDetailCat2EditStart(TreeTableColumn.CellEditEvent<Object, Object> event) {
        TreeItem ti = event.getRowValue();
        Object obj = ti.getValue();
        if (obj instanceof com.webfront.model.Ledger) {
            ledgerRollbackValue = (Ledger) obj;
            Category p1 = ledgerRollbackValue.getPrimaryCat();
            childList.clear();
            childList.addAll(CategoryManager.getInstance().getChildren(p1.getId()));
        } else {
            paymentRollbackValue = (Payment) obj;
        }
    }

    @FXML
    public void onDetailCat2EditCommit(TreeTableColumn.CellEditEvent<Object, Category> event) {
        TreeItem ti = event.getRowValue();
        Object obj = ti.getValue();
        if (obj instanceof com.webfront.model.Ledger) {
            Ledger l = (Ledger) obj;
            Category c = event.getNewValue();
            l.setSubCat(c);
            LedgerManager.getInstance().update(l);
        } else {
            Payment p = (Payment) obj;
            p.setSubCat(event.getNewValue());
            PaymentManager.getInstance().update(p);
        }
    }

    @FXML
    public void onDetailCat2EditCancel(TreeTableColumn.CellEditEvent<Payment, Category> event) {

    }

    @FXML
    public void onAdd() {
        SimpleBooleanProperty isNew = new SimpleBooleanProperty(false);

        if (table.getSelectionModel().getSelectedItem() == null) {
            selectedPaymentItem = new Payment();
        }

        ChangeListener listener = new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                System.out.println("DetailViewController.onAdd.changed()");
                if (isNew.get()) {
                    PaymentManager.getInstance().create(selectedPaymentItem);
                    Ledger l = selectedPaymentItem.getLedgerEntry();
                    if (l != null) {
                        for (TreeItem ti : root.getChildren()) {
                            if (ti.equals(l)) {
                                ti.getChildren().add(selectedPaymentItem);
                            }
                        }
                    }
                }
            }

        };

        PaymentForm paymentForm = new PaymentForm();
        paymentForm.getCreatedProperty().addListener(createListener);
        paymentForm.getUpdatedProperty().addListener(updateListener);
        paymentForm.getDeletedProperty().addListener(deleteListener);
        isNew.bind(paymentForm.getCreatedProperty());
        selectedPaymentProperty.bind(paymentForm.getSelectedPayment());

        paymentForm.getStage().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, new EventHandler() {
            @Override
            public void handle(Event event) {
                selectedPaymentProperty.unbind();
                paymentForm.getCreatedProperty().removeListener(createListener);
                paymentForm.getUpdatedProperty().removeListener(updateListener);
                paymentForm.getDeletedProperty().removeListener(deleteListener);
            }
        });
    }

    public void addListener(ListChangeListener<Payment> l) {

    }

    public void removeListener(ListChangeListener<Payment> l) {

    }

    private class DeleteListener implements InvalidationListener {

        @Override
        public void invalidated(Observable observable) {
            getPaymentManager().delete(selectedPaymentProperty.get());
            removeItem(selectedPaymentProperty.get());
        }
    }

    private class UpdateListener implements InvalidationListener {

        @Override
        public void invalidated(Observable observable) {
            updateItem(selectedPaymentProperty.get());
        }
    }

    private class CreateListener implements InvalidationListener {

        @Override
        public void invalidated(Observable observable) {
            getPaymentManager().create(selectedPaymentProperty.get());
            getList().add(selectedPaymentProperty.get());
        }
    }
}
