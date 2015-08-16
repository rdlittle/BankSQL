/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.app.Bank;
import com.webfront.bean.AccountManager;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    Stage stage;

    static PaymentView view;
    private HashMap<String, Stores> storeMap;
    HashMap<String, Category> categoryMap, subCatMap;
    Payment oldPayment;
    Payment newPayment;
    private SearchCriteria searchCriteria;

    public PaymentForm(PaymentView parent, Payment prevPayment) {
        view = parent;
        oldPayment = prevPayment;
        newPayment = new Payment();
        transDate = new DatePicker();
        cbAccount = new ComboBox<>();
        primaryCat = new ComboBox<>();
        subCat = new ComboBox<>();
        storeMap = new HashMap<>();
        categoryMap = new HashMap<>();
        subCatMap = new HashMap<>();
        btnOk = new Button();
        btnDelete = new Button();
        cbStores = new ComboBox<>();
        statusMessage = new Label();
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
            for (Stores s : view.getStoreList()) {
                storeMap.put(s.getStoreName(), s);
                cbStores.getItems().add(s.getStoreName());
            }

            for (Account acct : Bank.accountList) {
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
            ObservableList<Category> subList = (ObservableList<Category>) view.getCategoryList();
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
                        ObservableList<Category> subCatList = view.categoryManager.getCategories(sqlStmt);
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

            transDescription.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if (!transDescription.getText().equals(oldPayment.getTransDesc())) {
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
        // Set default values depending on whether you're editing an existing receipt or creating a new one
        if (oldPayment.getId() != null) {
            btnOk.setDisable(true);
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
        Payment receipt = newPayment;
        String newStoreName = cbStores.getValue();
        String storeKey;
        Stores store = new Stores();

        if (!storeMap.containsKey(newStoreName)) {
            StoresManager storeManager = new StoresManager();
            store.setStoreName(newStoreName);
            storeManager.create(store);
            storeKey = store.getStoreName();
            getStoreMap().put(storeKey, store);
            view.getStoreList().add(store);
            view.getStoreAdded().set(true);
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
            view.getPaymentManager().update(oldPayment);
            int idx = view.getTable().getSelectionModel().getSelectedIndex();
            view.getTable().getItems().set(idx, oldPayment);
            view.getPaymentManager().refresh(oldPayment);
            closeForm();
        } else {
            LocalDate localDate = transDate.getValue();
            String dateStr = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
            receipt.setTransDate(Date.valueOf(dateStr));
            receipt.setTransDesc(transDescription.getText());
            receipt.setAccountNum((Integer) cbAccount.getSelectionModel().getSelectedItem().getKey());
            receipt.setTransAmt(Float.parseFloat(transAmt.getText()));
            storeKey = cbStores.getValue();
            store = getStoreMap().get(storeKey);
            Category cat1 = categoryMap.get(primaryCat.getValue());
            Category cat2 = subCatMap.get(subCat.getValue());
            receipt.setPrimaryCat(cat1);
            receipt.setSubCat(cat2);
            receipt.setStore(store);
            view.getPaymentManager().create(receipt);
            view.getTable().getItems().add(receipt);
            view.getTable().getItems().sort(Payment.PaymentComparator);
            view.getTable().getSelectionModel().selectFirst();
            newPayment = new Payment();
        }
        transDescription.clear();
        transId.clear();
        transAmt.setText("0");
    }

    @FXML
    public void deleteItem() {
        if (oldPayment != null) {
            if (oldPayment.getId() != null) {
                view.getPaymentManager().delete(oldPayment);
                view.getTable().getItems().remove(oldPayment);
                closeForm();
            }
        }
    }

    @FXML
    void closeForm() {
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
        sql += "AND accountNum = "+oldPayment.getAccountNum()+" ";
        sql += "ORDER BY transDate";
        ObservableList<Ledger> results;
        results = view.getLedgerManager().doSqlQuery(sql);

        SearchResults searchResults = new SearchResults();
        searchCriteria.setStartDate(startDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        searchCriteria.setEndDate(endDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        searchCriteria.setAmount(Float.toString(oldPayment.getTransAmt()));
        if (oldPayment.getStore() != null) {
            searchCriteria.setStoreId(oldPayment.getStore().getStoreName());
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
                ledger = view.getLedgerManager().getItem(Integer.parseInt(id));
                oldPayment.setLedgerEntry(ledger);
                btnOk.setDisable(false);
                statusMessage.setText("");
            } catch (javax.persistence.NoResultException ex) {
                statusMessage.setText("Ledger ID (" + id + ") not found.");
                transId.setText("");
            }
        }
    }
}
