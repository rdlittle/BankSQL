/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.bean.AccountManager;
import com.webfront.bean.CategoryManager;
import com.webfront.bean.LedgerManager;
import com.webfront.bean.StoresManager;
import com.webfront.model.Account;
import com.webfront.model.Category;
import com.webfront.model.Ledger;
import com.webfront.model.Payment;
import com.webfront.model.SearchCriteria;
import com.webfront.model.SelectItem;
import com.webfront.model.Stores;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author rlittle
 */
public final class PaymentForm extends AnchorPane {

    @FXML
    DatePicker transDate;
    @FXML
    ComboBox<SelectItem> cbAccount;
    @FXML
    ComboBox<String> primaryCat;
    @FXML
    ComboBox<String> subCat;

    @FXML
    TextField transId;
    @FXML
    TextField transAmt;
    @FXML
    TextField transDescription;
    @FXML
    Button btnOk;
    @FXML
    Button btnCancel;
    @FXML
    Button btnDelete;

    @FXML
    ComboBox<String> cbStores;

    @FXML
    Hyperlink searchLink;

    @FXML
    Label statusMessage;

    @FXML
    CheckBox cbCash;

    Stage stage;

    static ViewInterface view;
    private final LinkedHashMap<String, Stores> storeMap;
    HashMap<String, Category> categoryMap, subCatMap;
    Payment oldPayment;
    Payment newPayment;
    private SearchCriteria searchCriteria;
    private SimpleBooleanProperty updatedProperty;
    private SimpleBooleanProperty createdProperty;
    private SimpleBooleanProperty deletedProperty;
    private SimpleBooleanProperty storeAddedProperty;
    private final ArrayList<String> storeList;
    private SimpleObjectProperty<Payment> selectedPayment;
    private final ItemListener itemListener = new ItemListener();
    private Account cashAccount;

    public PaymentForm() {
        newPayment = new Payment();
        oldPayment = new Payment();
        selectedPayment = new SimpleObjectProperty<>();
        selectedPayment.addListener(itemListener);

        transDate = new DatePicker();
        cbAccount = new ComboBox<>();
        primaryCat = new ComboBox<>();
        subCat = new ComboBox<>();
        storeMap = new LinkedHashMap<>();
        storeMap.putAll(StoresManager.getInstance().getStoresMap());
        storeList = new ArrayList<>(storeMap.keySet());
        storeList.sort(comparator);
        categoryMap = new HashMap<>();
        subCatMap = new HashMap<>();
        btnOk = new Button();
        btnDelete = new Button();
        cbStores = new ComboBox<>();
        statusMessage = new Label();
        cbCash = new CheckBox();
        cbCash.setSelected(false);
        updatedProperty = new SimpleBooleanProperty(false);
        deletedProperty = new SimpleBooleanProperty(false);
        createdProperty = new SimpleBooleanProperty(false);
        buildForm();
        setFormData();
    }

    public void buildForm() {
        try {
            URL location = getClass().getResource("/com/webfront/app/fxml/PaymentForm.fxml");
            ResourceBundle resources = ResourceBundle.getBundle("com.webfront.app.bank");
            FXMLLoader loader = new FXMLLoader(location, resources);

            loader.setRoot(this);
            loader.setController(this);
            stage = new Stage();
            Scene scene = new Scene(this);
            loader.load();

            stage.setScene(scene);
            stage.setTitle("Payment Form");

            // Populate store list
            cbStores.getItems().addAll(storeList);

            for (Account acct : AccountManager.getInstance().getList("Account.findAll")) {
                if (acct.getAccountName().equalsIgnoreCase("Petty Cash")) {
                    cashAccount = acct;
                }
                SelectItem<Integer, String> se = new SelectItem<>(acct.getId(), acct.getAccountName());
                cbAccount.getItems().add(se);
            }
            cbStores.setEditable(true);
            cbStores.setPromptText("Enter store name...");
            cbStores.setOnAction(new EventHandler() {
                @Override
                public void handle(Event event) {
                    ComboBox cb = (ComboBox) event.getSource();
                    if (cb.getValue() != null && !cb.getValue().toString().isEmpty()) {
                        String sName = cb.getValue().toString();
                    }
                }
            });

            // Populate category 1 and category 2 lists
            ObservableList<Category> subList = (ObservableList<Category>) CategoryManager.getInstance().getCategories();
            subList.stream().forEach((c) -> {
                categoryMap.put(c.getDescription(), c);
                if (c.getParent() == null || c.getParent() == 0) {
                    primaryCat.getItems().add(c.getDescription());
                } else {
                    subCat.getItems().add(c.getDescription());
                }
            });

            transDate.setOnAction(new EventHandler() {
                @Override
                public void handle(Event event) {
                    if (oldPayment.getId() != null) {
                        Date d = new Date(oldPayment.getTransDate().getTime());
                        LocalDate oDate = d.toLocalDate();
                        String oldDate = oDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
                        String newDate = transDate.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE);
                        if (!oldDate.equals(newDate)) {
                            btnOk.setDisable(false);
                        }
                    }
                }
            });

            cbStores.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    if (oldPayment.getId() != null) {
                        if (!oldPayment.getStore().getStoreName().equals(cbStores.getValue())) {
                            btnOk.setDisable(false);
                        }
                    }
                }
            });

            primaryCat.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    String newCat = newValue.toString();
                    if (categoryMap.containsKey(newCat)) {
                        Category c = categoryMap.get(newValue);
                        String sqlStmt = "SELECT * FROM categories WHERE parent = " + Integer.toString(c.getId());
                        sqlStmt += " order by description";
                        ObservableList<Category> subCatList = CategoryManager.getInstance().getCategories(sqlStmt);
                        subCat.getItems().clear();
                        subCatMap.clear();
                        for (Category cat2 : subCatList) {
                            subCatMap.put(cat2.getDescription(), cat2);
                        }
                        subCat.getItems().addAll(subCatMap.keySet());
                        subCat.getSelectionModel().selectFirst();
                        if (oldPayment.getId() != null) {
                            if (oldPayment.getPrimaryCat().getDescription() != primaryCat.getValue()) {
                                btnOk.setDisable(false);
                            }
                        }
                    }
                }
            });

            subCat.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    if (oldPayment.getId() != null) {
                        if (oldPayment.getSubCat() != null) {
                            if (oldPayment.getSubCat().getDescription() != subCat.getValue()) {
                                btnOk.setDisable(false);
                            }
                        }
                    }
                }
            });

            transDescription.setOnKeyReleased(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    String newDesc = transDescription.getText();
                    String oldDesc = oldPayment.getTransDesc();
                    if (!newDesc.equals(oldDesc)) {
                        btnOk.setDisable(false);
                    }
                }
            });

            transId.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if (event.getCode() == KeyCode.TAB) {
                        String tid = transId.getText();
                        if (tid != null && !tid.isEmpty()) {
                            updateTrans(tid);
                        }
                    }
                }
            });

            transId.textProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {

                    btnOk.setDisable(false);
                }
            });

            transAmt.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if (event.getCode() == KeyCode.TAB) {
                        if (!transAmt.getText().equals(oldPayment.getTransAmt())) {
                            btnOk.setDisable(false);
                        }
                    }
                }
            });

            searchLink = new Hyperlink();
            searchLink.setOnAction((ActionEvent e) -> {
                System.out.println("This link is clicked");
            });
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(PaymentForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setFormData() {
        // Set default values depending on whether you're editing an existing payment or creating a new one
        if (oldPayment.getId() != null) {
            btnOk.setDisable(true);
            btnDelete.setDisable(false);
            long ldate = oldPayment.getTransDate().getTime();
            Date d2 = new Date(ldate);
            LocalDate localDate = d2.toLocalDate();
            transDate.setValue(localDate);
            transDescription.setText(oldPayment.getTransDesc());
            transAmt.setText(Float.toString(oldPayment.getTransAmt()));
            Ledger l = oldPayment.getLedgerEntry();
            if (l != null) {
                transId.setText(l.getId().toString());
                Integer aid = l.getAccountNum();
                Account acct = AccountManager.getInstance().getAccount(aid);
                SelectItem<Integer, String> se = new SelectItem<>(aid, acct.getAccountName());
                cbAccount.setValue(se);
            } else {
                cbAccount.getSelectionModel().selectFirst();
                for (SelectItem se : cbAccount.getItems()) {
                    if (se.getKey() == oldPayment.getAccountNum()) {
                        cbAccount.getSelectionModel().select(se);
                        break;
                    }
                }
            }
            primaryCat.setValue(oldPayment.getPrimaryCat().getDescription());
            if (oldPayment.getSubCat() != null) {
                subCat.setValue(oldPayment.getSubCat().getDescription());
            }
            if (oldPayment.getStore().getStoreName() != null) {
                cbStores.setValue(oldPayment.getStore().getStoreName());
            }
            cbCash.setSelected(oldPayment.isCashPayment());
        } else {
            transDate.setValue(LocalDate.now());
            cbAccount.getSelectionModel().selectFirst();
            transId.setText("0");
            transAmt.setText("0");
            btnDelete.setDisable(true);
        }
    }

    @FXML
    void addItem() {
        Payment payment = newPayment;
        String newStoreName = cbStores.getValue();
        String storeKey;
        Stores store = new Stores();
        getCreatedProperty().set(false);

        if (!storeMap.containsKey(newStoreName)) {
            StoresManager storeManager = StoresManager.getInstance();
            store.setStoreName(newStoreName);
            storeManager.create(store);
            storeKey = store.getStoreName();
            storeManager.getStoresMap().put(storeKey, store);
//            view.getStoreList().add(store);
//            view.getStoreAdded().set(true);
            storeAddedProperty.set(true);
            cbStores.getItems().sort(comparator);
        }

        if (oldPayment.getId() != null) {
            LocalDate localDate = transDate.getValue();
            String dateStr = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
            oldPayment.setTransDate(Date.valueOf(dateStr));
            oldPayment.setTransDesc(transDescription.getText());
            oldPayment.setAccountNum((Integer) cbAccount.getSelectionModel().getSelectedItem().getKey());
            oldPayment.setTransAmt(Float.parseFloat(transAmt.getText()));
            storeKey = cbStores.getValue();
            store = getStoreMap().get(storeKey);
            Category cat1 = categoryMap.get(primaryCat.getValue());
            Category cat2 = subCatMap.get(subCat.getValue());
            oldPayment.setPrimaryCat(cat1);
            oldPayment.setSubCat(cat2);
            oldPayment.setStore(store);
            oldPayment.setCashPayment(cbCash.isSelected());
            getUpdatedProperty().set(true);
            selectedPayment.setValue(oldPayment);
            closeForm();
        } else {
            LocalDate localDate = transDate.getValue();
            String dateStr = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
            payment.setTransDate(Date.valueOf(dateStr));
            payment.setTransDesc(transDescription.getText());
            payment.setAccountNum((Integer) cbAccount.getSelectionModel().getSelectedItem().getKey());
            payment.setTransAmt(Float.parseFloat(transAmt.getText()));
            storeKey = cbStores.getValue();
            store = getStoreMap().get(storeKey);
            Category cat1 = categoryMap.get(primaryCat.getValue());
            Category cat2 = subCatMap.get(subCat.getValue());
            payment.setPrimaryCat(cat1);
            payment.setSubCat(cat2);
            payment.setStore(store);
            payment.setCashPayment(false);
            selectedPayment.set(payment);
            newPayment = new Payment();
            getCreatedProperty().set(true);
        }
        transDescription.clear();
        transId.clear();
        transAmt.setText("0");
    }

    @FXML
    public void deleteItem() {
        if (oldPayment != null) {
            if (oldPayment.getId() != null) {
                deletedProperty.set(true);
                closeForm();
            }
        }
    }

    @FXML
    void closeForm() {
        selectedPayment.removeListener(itemListener);
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        stage.close();
    }

    /**
     * @return the storeMap
     */
    public HashMap<String, Stores> getStoreMap() {
        return storeMap;
    }

    public static Comparator<String> comparator = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    };

    @FXML
    public void doSearch() {
        searchCriteria = new SearchCriteria();
        String sql = "SELECT * from ledger where transDate >= \"";
        LocalDate localDate = transDate.getValue();
        searchCriteria.setDate(localDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        LocalDate startDate = localDate.minusDays(5);
        LocalDate endDate = localDate.plusDays(5);
        String dateStr = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        sql += startDate.format(DateTimeFormatter.ISO_LOCAL_DATE) + "\" ";
        sql += "and transDate <= \"";
        sql += endDate.format(DateTimeFormatter.ISO_LOCAL_DATE) + "\" ";
        sql += "AND accountNum = ";
        if (cbCash.isSelected()) {
            sql += cashAccount.getId();
        } else {
            sql += oldPayment.getAccountNum();
        }
        sql += " ORDER BY transDate";
        ObservableList<Ledger> results;
        results = LedgerManager.getInstance().doSqlQuery(sql);

        SearchResults searchResults = new SearchResults();
        searchCriteria.setStartDate(startDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        searchCriteria.setEndDate(endDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        searchCriteria.setAmount(Float.toString(oldPayment.getTransAmt()));
        if (oldPayment.getStore() != null) {
            searchCriteria.setStoreId(oldPayment.getStore().getId());
        }
        searchResults.searchCriteria = this.searchCriteria;
        searchResults.resultProperty.addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                String id = newValue.toString();
                if (id != null && !id.isEmpty() && !id.equals("-1")) {
                    transId.setText(id);
                    updateTrans(id);
                }
            }
        });

        EventHandler<MouseEvent> click = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    Ledger item = (Ledger) searchResults.getTable().getSelectionModel().getSelectedItem();
                    if (item != null) {
                        transId.setText(item.getId().toString());
                        updateTrans(item.getId().toString());
                    }
                    searchResults.stage.close();
                }
            }
        };

        searchResults.getTable().addEventHandler(MouseEvent.MOUSE_CLICKED, click);
        if (results.size() > 0) {
            searchResults.setResultsList(results);
            statusMessage.setText(null);
        } else {
            statusMessage.setText("No items found");
        }
    }

    private void updateTrans(String id) {
        if (id != null && !id.isEmpty()) {
            try {
                Ledger ledger;
                ledger = LedgerManager.getInstance().getItem(Integer.parseInt(id));
                if (ledger != null) {
                    oldPayment.setLedgerEntry(ledger);
                    if (ledger.getPayment().contains(oldPayment)) {
                        int idx = ledger.getPayment().indexOf(oldPayment);
                        ledger.getPayment().set(idx, oldPayment);
                    } else {
                        ledger.getPayment().add(oldPayment);
                    }
                    btnOk.setDisable(false);
                    statusMessage.setText("");
                }
            } catch (javax.persistence.NoResultException ex) {
                statusMessage.setText("Ledger ID (" + id + ") not found.");
                transId.setText("");
            }
        }
    }

    public void removeEventHandler(EventHandler eventHandler) {
        stage.removeEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, eventHandler);
    }

    /**
     * @return the updatedProperty
     */
    public SimpleBooleanProperty getUpdatedProperty() {
        return updatedProperty;
    }

    /**
     * @param updatedProperty the updatedProperty to set
     */
    public void setUpdatedProperty(SimpleBooleanProperty updatedProperty) {
        this.updatedProperty = updatedProperty;
    }

    public Stage getStage() {
        return stage;
    }

    /**
     * @return the selectedPayment
     */
    public SimpleObjectProperty getSelectedPayment() {
        return selectedPayment;
    }

    /**
     * @param selectedPayment the selectedPayment to set
     */
    public void setSelectedPayment(SimpleObjectProperty selectedPayment) {
        this.selectedPayment = selectedPayment;
    }

    /**
     * @return the createProperty
     */
    public SimpleBooleanProperty getCreatedProperty() {
        return createdProperty;
    }

    /**
     * @param createProperty the createProperty to set
     */
    public void setNewProperty(SimpleBooleanProperty createProperty) {
        this.createdProperty = createProperty;
    }

    /**
     * @return the deleted
     */
    public SimpleBooleanProperty getDeletedProperty() {
        return deletedProperty;
    }

    /**
     * @param deleted the deleted to set
     */
    public void setDeleted(SimpleBooleanProperty deleted) {
        this.deletedProperty = deleted;
    }

    private class ItemListener implements ChangeListener<Payment> {

        @Override
        public void changed(ObservableValue<? extends Payment> observable, Payment oldValue, Payment newValue) {
            if (newValue != null) {
                oldPayment = (Payment) newValue;
                setFormData();
            }
        }

    }
}
