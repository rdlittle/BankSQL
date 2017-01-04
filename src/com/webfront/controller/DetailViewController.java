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
import java.util.ArrayList;
import java.util.Comparator;
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
import javafx.collections.transformation.SortedList;
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
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;
import javafx.scene.control.cell.ComboBoxTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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
    
    @FXML
    Pane pane;
    @FXML
    VBox vbox;

    private final String dateFormat = "MM/dd/yyyy";
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);

    private final String decimalFormat = "###0.00";
    private final DecimalFormat decimalFormatter = new DecimalFormat(decimalFormat);

    public int accountNumber = 1;
    private final ObservableList<Ledger> list = FXCollections.<Ledger>observableArrayList();
    private final LedgerManager ledgerManager = LedgerManager.getInstance();

    private TreeItem<Ledger> root;
    private SortedList<Ledger> sortedList;
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
    SimpleBooleanProperty isNew = new SimpleBooleanProperty(false);

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
        return PaymentManager.getInstance();
    }

    @Override
    public ObservableList<Payment> getList() {
        return PaymentManager.getInstance().getList("");
    }
    
    public ObservableList<Ledger> getLedgerList() {
        return list;
    }

    @Override
    public void updateItem(Payment p) {
        PaymentManager.getInstance().update(p);
//        Ledger l = p.getLedgerEntry();
//        if (l != null) {
//            int idx = table.getRoot().getChildren().indexOf(l);
//            TreeItem<Ledger> parent = table.getRoot().getChildren().get(idx);
//            for (TreeItem child : parent.<Payment>getChildren()) {
//                if (child.equals(p)) {
//
//                }
//            }
//        }

    }

    @Override
    public void removeItem(Payment p) {
//        getPaymentManager().delete(p);
    }

    @Override
    public void updateTrans(int idx) {
        //getPaymentManager().update(idx);
    }

    @Override
    public Ledger getLedgerItem(String id) {
        return getLedgerManager().getItem(Integer.parseInt(id));
    }

    @Override
    public LedgerManager getLedgerManager() {
        return LedgerManager.getInstance();
    }

    /**
     * @return the root
     */
    public TreeItem<Ledger> getRoot() {
        return root;
    }

    /**
     * @param root the root to set
     */
    public void setRoot(TreeItem<Ledger> root) {
        this.root = root;
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
        pane = new Pane();
        vbox = new VBox();
        vbox.alignmentProperty().set(Pos.CENTER_RIGHT);
        table.minHeightProperty().bind(pane.heightProperty().subtract(200D));
        selectedLedgerItem = new Ledger();
        setRoot(new TreeItem<>());
        table.showRootProperty().set(false);
        parentList = FXCollections.<Category>observableArrayList(CategoryManager.getInstance().getList("Category.findAllParent"));
        sortedList = new SortedList(parentList);
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
//        table.minHeightProperty().bind();
        
        table.addEventHandler(MouseEvent.MOUSE_CLICKED, new DoubleClick());
        Platform.runLater(() -> loadData());
    }

    public void loadData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("accountType", Account.AccountType.CHECKING);
        map.put("accountStatus", Account.AccountStatus.ACTIVE);
        list.setAll(ledgerManager.doNamedQuery("Ledger.findAllByType", map));
        Ledger unAssignedPayments = new Ledger(0);
        unAssignedPayments.setTransDesc("Unassigned");
        unAssignedPayments.setTransAmt(0);
        TreeItem orphans = new TreeItem<>(unAssignedPayments);
        ObservableList<Payment> pList = PaymentManager.getInstance().doNamedQuery("Payment.findOrphans");
        Float amt = new Float(0.0);
        for (Payment p : pList) {
            unAssignedPayments.getPayment().add(p);
            amt += p.getTransAmt();
            unAssignedPayments.setTransAmt(accountNumber);
            orphans.getChildren().add(new TreeItem<>(p));
        }
        unAssignedPayments.setTransAmt(amt);

        getRoot().getChildren().add(orphans);

        for (Ledger l : list) {
            TreeItem ti = new TreeItem<>(l);
            for (Payment p : l.getPayment()) {
                ti.getChildren().add(new TreeItem<>(p));
            }
            getRoot().getChildren().add(ti);
        }
        table.setRoot(getRoot());
    }
    
    public void addLedgerEntries(ArrayList<Ledger> l) {
        for(Ledger le : l) {
            TreeItem<Ledger> ti = new TreeItem<>(le);
            root.getChildren().add(ti);
        }
    }

    public void doUpdate(ObservableList<Payment> paymentList) {
        for (Payment p : paymentList) {
            for (TreeItem branch : getRoot().getChildren()) {
                if (branch.getValue() instanceof Ledger) {
                    Ledger parent = (Ledger) branch.getValue();
                    if (parent.getId().equals(p.getLedgerEntry().getId())) {
                        TreeItem<Payment> child = new TreeItem<>(p);
                        if (!branch.getChildren().contains(child)) {
                            branch.getChildren().add(child);
                        } else {
                            int idx = branch.getChildren().indexOf(child);
                            branch.getChildren().set(idx, child);
                        }
                        break;
                    }
                }
            }
        }
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
                        for (TreeItem ti : getRoot().getChildren()) {
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

    private void doEdit(TreeItem ti) {
        TreeItem parent = ti.getParent();
        selectedRow = table.getSelectionModel().getSelectedIndex();
        boolean isPayment = (ti.getValue() instanceof Payment);
        if (isPayment) {
            Payment p = (Payment) ti.getValue();
            selectedPaymentProperty.set(p);
            selectedLedgerItem = p.getLedgerEntry();

            PaymentForm paymentForm = new PaymentForm();
            paymentForm.getStage().setOnCloseRequest(new EventHandler() {
                @Override
                public void handle(Event event) {
                    Payment p = selectedPaymentProperty.get();
                    if (p != null) {
                        Ledger l = p.getLedgerEntry();
                        if (l != null) {
                            p.getLedgerEntry().getPayment().add(p);
                        }
                    }
                    selectedPaymentProperty.unbindBidirectional(paymentForm.getSelectedPayment());
                    paymentForm.getCreatedProperty().removeListener(createListener);
                    paymentForm.getUpdatedProperty().addListener(updateListener);
                    paymentForm.getDeletedProperty().removeListener(deleteListener);
                }
            });

            paymentForm.getCreatedProperty().addListener(createListener);
            paymentForm.getUpdatedProperty().addListener(updateListener);
            paymentForm.getDeletedProperty().addListener(deleteListener);
            selectedPaymentProperty.bindBidirectional(paymentForm.getSelectedPayment());
            selectedPaymentProperty.set(p);
        } else {
            selectedLedgerItem = (Ledger) ti.getValue();
        }

    }

    private class DeleteListener implements InvalidationListener {

        @Override
        public void invalidated(Observable observable) {
            if (selectedPaymentProperty.get().getLedgerEntry() == null) {
                int si = table.getSelectionModel().getSelectedIndex();
                TreeItem ti = table.getSelectionModel().getModelItem(si);
                if (ti.getValue() instanceof com.webfront.model.Payment) {
                    getPaymentManager().delete(selectedPaymentProperty.get());
                    ti.getParent().getChildren().remove(ti);
                }
            }
        }
    }

    private class UpdateListener implements InvalidationListener {

        @Override
        public void invalidated(Observable observable) {
            Payment p = selectedPaymentProperty.get();
            TreeTableViewSelectionModel<Ledger> sm = table.getSelectionModel();
            updateItem(p);
            if (p.getLedgerEntry() != null) {
                int si = sm.getSelectedIndex();
                TreeItem ti = sm.getModelItem(si);
                if (ti.getValue() instanceof com.webfront.model.Payment) {
                    Ledger pLedger = p.getLedgerEntry();
                    for (TreeItem<Ledger> lti : root.getChildren()) {
                        if (lti.getValue().getId().equals(pLedger.getId())) {
                            ti.getParent().getChildren().remove(ti);
                            lti.getChildren().add(ti);
                            break;
                        }
                    }
                }
            }
        }
    }

    private class CreateListener implements InvalidationListener {

        @Override
        public void invalidated(Observable observable) {
            Payment p = selectedPaymentProperty.get();
            getPaymentManager().create(p);
            TreeItem pti = new TreeItem<>(p);
            if (p.getLedgerEntry() == null) {
                for(TreeItem<Ledger> lti : root.getChildren()) {
                    if(lti.getValue().getId()==0) {
                        lti.getChildren().add(pti);
                        lti.getChildren().sort(new Comparator<TreeItem>() {
                            @Override
                            public int compare(TreeItem o1, TreeItem o2) {
                                Payment p1 = (Payment) o1.getValue();
                                Payment p2 = (Payment) o2.getValue();
                                if(p1.getTransDate().equals(p2.getTransDate())) {
                                    return p2.getId().compareTo(p1.getId());
                                }
                                return p2.getTransDate().compareTo(p1.getTransDate());
                            }
                        });
                        table.refresh();
                        break;
                    }
                }
            } else {
                for (TreeItem<Ledger> lti : root.getChildren()) {
                    if (lti.getValue().getId().equals(p.getLedgerEntry().getId())) {
                        pti.getParent().getChildren().remove(pti);
                        lti.getChildren().add(pti);
                        break;
                    }
                }
            }
//            selectedPaymentProperty.set(p);
            selectedPaymentProperty.getValue().setId(null);
//            selectedPaymentProperty.getValue().setTransDate(p.getTransDate());
//            isNew.setValue(false);
        }
    }

    private class DoubleClick implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent event) {
            if (event.getClickCount() == 2) {
                TreeItem ti = table.getSelectionModel().getSelectedItem();
                if (ti.isLeaf()) {
                    doEdit(ti);
                }
            }
        }

    }

}
