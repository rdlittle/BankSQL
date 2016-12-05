/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.app.utils.DateConvertor;
import com.webfront.bean.AccountManager;
import com.webfront.bean.CategoryManager;
import com.webfront.bean.StoresManager;
import com.webfront.model.Account;
import com.webfront.model.Category;
import com.webfront.model.Ledger;
import com.webfront.model.SearchCriteria;
import com.webfront.model.Stores;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author rlittle
 */
public final class SearchForm extends AnchorPane {

    SearchCriteria criteria;
    CategoryManager catManager;

    Stage stage;
    Scene scene;
    private LedgerView view;
    private FilteredList<Category> childCatList;

    URL location;
    ResourceBundle resources = ResourceBundle.getBundle("com.webfront.app.bank");

    @FXML
    Button btnOk;

    @FXML
    Button btnCancel;

    @FXML
    CheckBox chkChecks;

    @FXML
    ComboBox<Account> cbAccounts;

    @FXML
    ComboBox<Category> cbPrimary;

    @FXML
    ComboBox<Category> cbSecondary;

    @FXML
    ComboBox<Stores> cbStores;

    @FXML
    DatePicker startDate;

    @FXML
    DatePicker endDate;

    @FXML
    Label lblCheckStart;

    @FXML
    Label lblCheckEnd;

    @FXML
    TextField txtCheckStart;

    @FXML
    TextField txtCheckEnd;

    @FXML
    TextField txtPayee;

    @FXML
    TextField minAmount;

    @FXML
    TextField maxAmount;

    public SearchForm() {
        location = getClass().getResource("/com/webfront/app/fxml/SearchForm.fxml");
        FXMLLoader loader = new FXMLLoader(location, resources);
        loader.setRoot(this);
        loader.setController(this);
        catManager = CategoryManager.getInstance();

        stage = new Stage();
        scene = new Scene(this);
        cbPrimary = new ComboBox<>();
        cbSecondary = new ComboBox<>();
        cbStores = new ComboBox<>();
        cbAccounts = new ComboBox<>();
        btnOk = new Button();
        btnCancel = new Button();

        endDate = new DatePicker();
        startDate = new DatePicker();
        minAmount = new TextField("0");
        maxAmount = new TextField("999999");
        txtCheckStart = new TextField();
        txtCheckEnd = new TextField();
        txtPayee = new TextField();
        chkChecks = new CheckBox();
        chkChecks.selectedProperty().set(false);
        lblCheckStart = new Label();
        lblCheckEnd = new Label();

        try {
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(SearchForm.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public SearchForm(LedgerView view, SearchCriteria sc) {
        this();
        criteria = sc;
        //view = view;
        catManager = view.getCategoryManager();
        // Set the upper range to the latest available transaction date
        Ledger ledgerItem = (Ledger) view.getTable().getItems().get(0);
        Date date = ledgerItem.getTransDate();
        endDate.valueProperty().set(DateConvertor.toLocalDate(date));
        // Set the lower range to 60 days before latest date   
        startDate.valueProperty().set(endDate.getValue().minusYears(1));

        setForm();
    }

    public void setForm() {
        buildForm();
        setHandlers();
    }

    public void buildForm() {
        if (criteria == null) {
            criteria = new SearchCriteria();
        }

        criteria.getStartDateProperty().bind(startDate.valueProperty());
        criteria.getEndDateProperty().bind(endDate.valueProperty());

        criteria.getMinAmountProperty().bind(minAmount.textProperty());
        criteria.getMaxAmountProperty().bind(maxAmount.textProperty());

        criteria.getPrimaryCatProperty().bind(cbPrimary.valueProperty());
        criteria.getSecondaryCatProperty().bind(cbSecondary.valueProperty());

        criteria.getChkStartProperty().bind(txtCheckStart.textProperty());
        criteria.getChkEndProperty().bind(txtCheckEnd.textProperty());

        criteria.getStoreProperty().bind(cbStores.valueProperty());

        criteria.getAccountProperty().bind(cbAccounts.valueProperty());
        
        criteria.getPayeeProperty().bind(txtPayee.textProperty());

        if (startDate.getValue() == null) {
            startDate.setValue(LocalDate.now());
        }
        if ("".equals(criteria.getStartDate())) {
            criteria.setStartDate(startDate.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        startDate.setValue(LocalDate.parse(criteria.getStartDate()));

        if (endDate.getValue() == null) {
            endDate.setValue(startDate.getValue().minusYears(10));
        }
        if ("".equals(criteria.getEndDate())) {
            criteria.setEndDate(endDate.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        endDate.setValue(LocalDate.parse(criteria.getEndDate()));

        cbAccounts.setItems(AccountManager.getInstance().getAccounts());
        cbAccounts.converterProperty().set(new AccountManager.AccountConverter());

        cbPrimary.converterProperty().set(new CategoryManager.CategoryConverter());
        cbPrimary.getItems().addAll(CategoryManager.getInstance().getCategories("SELECT * FROM categories WHERE parent = 0"));

        childCatList = new FilteredList<>(CategoryManager.getInstance().getCategories("SELECT * FROM categories WHERE parent > 0"));
        childCatList.setPredicate((e) -> true);
        cbSecondary.converterProperty().set(new CategoryManager.CategoryConverter());
        cbSecondary.setItems(childCatList);

        cbStores.setItems(StoresManager.getInstance().getStoreList());
        cbStores.converterProperty().set(new StoresManager.StoreConverter());

        txtCheckStart.disableProperty().bind(chkChecks.selectedProperty().not());
        txtCheckEnd.disableProperty().bind(chkChecks.selectedProperty().not());
        lblCheckStart.disableProperty().bind(chkChecks.selectedProperty().not());
        lblCheckEnd.disableProperty().bind(chkChecks.selectedProperty().not());

        setFormData();
        stage.setScene(scene);
        stage.setTitle("Search Form");

    }

    @FXML
    public void cbPrimaryOnAction() {
        Category p = cbPrimary.getValue();
        childCatList.setPredicate((c) -> (p.getId() == c.getParent()));
    }

    public void showForm() {
        stage.showAndWait();
    }

    private void setHandlers() {

        startDate.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                try {
                    LocalDate ld = (LocalDate) newValue;
                    criteria.validateRange(ld, endDate.getValue());
                    criteria.setStartDate(ld.format(DateTimeFormatter.ISO_LOCAL_DATE));
                } catch (Exception ex) {
                    Logger.getLogger(SearchForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        endDate.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                try {
                    LocalDate ld = (LocalDate) newValue;
                    criteria.validateRange(startDate.getValue(), ld);
                    criteria.setEndDate(ld.format(DateTimeFormatter.ISO_LOCAL_DATE));
                } catch (Exception ex) {
                    Logger.getLogger(SearchForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        minAmount.textProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                criteria.setMinAmount(newValue.toString());
            }
        });

        maxAmount.textProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                criteria.setMaxAmount(newValue.toString());
            }
        });

    }

    @FXML
    public void doSearch() {
        // Want to construct an SQL statement similar to:
        // SELECT * FROM ledger l 
        // INNER JOIN distribution d ON d.transId = l.id 
        // WHERE l.primaryCat = 1 AND d.categoryId = 23 
        // AND l.transDate >= "2014-04-07" AND l.transDate <= "2014-04-21" 
        // AND l.transDesc like "%SPEED%";
        StringBuilder sql = new StringBuilder();
        float min, max;
        int startCheck = 0;
        int endCheck = 0;
        boolean hasPrimaryCat = false;
        boolean hasSecondaryCat = false;
        boolean hasStartDate = false;
        boolean hasEndDate = false;
        boolean hasMinAmt = false;
        boolean hasMaxAmt = false;
        boolean hasStore = false;
        boolean hasDescription = false;
        boolean hasAccount = false;
        boolean hasStartCheck = false;
        boolean hasEndCheck = false;
        boolean hasPayee = false;
        int args = 0;

        Account a = cbAccounts.getValue();
        if (a != null) {
            hasAccount = true;
            args++;
        }

        String chkNum = txtCheckStart.getText();
        if (chkNum != null && !chkNum.isEmpty()) {
            try {
                Integer chk = Integer.parseInt(chkNum);
                hasStartCheck = true;
                args++;
            } catch (NumberFormatException nfe) {

            }
        }

        chkNum = txtCheckStart.getText();
        if (chkNum != null && !chkNum.isEmpty()) {
            try {
                Integer chk = Integer.parseInt(chkNum);
                hasEndCheck = true;
                args++;
            } catch (NumberFormatException nfe) {

            }
        }

        if (criteria.getSecondaryCat() != null && criteria.getSecondaryCat().getId() != null) {
            hasSecondaryCat = true;
            args++;
        }

        if (criteria.getStartDate() != null && !criteria.getStartDate().isEmpty()) {
            hasStartDate = true;
            args++;
        }

        if (criteria.getEndDate() != null && !criteria.getEndDate().isEmpty()) {
            hasEndDate = true;
        }

        if (criteria.getPrimaryCat() != null) {
            if (criteria.getPrimaryCat().getId() != null) {
                args++;
                hasPrimaryCat = true;
            }
        }

        min = 0;
        max = 0;

        if (criteria.getMinAmount() != null && !criteria.getMinAmount().isEmpty()) {
            hasMinAmt = true;
            args++;
            min = Float.parseFloat(criteria.getMinAmount());
        }

        if (criteria.getMaxAmount() != null && !criteria.getMaxAmount().isEmpty()) {
            max = Float.parseFloat(criteria.getMaxAmount());
            if (max >= min) {
                hasMaxAmt = true;
                args++;
            }
        }

        if (!criteria.getSearchTarget().isEmpty()) {
            args++;
            hasDescription = true;
        }

        if (criteria.getStoreId() != null) {
            args++;
            hasStore = true;
        }

        /*
        SELECT c.description "Category", c2.description "Sub Category" 
            s.storeName Store,
            l.transDate "Post Date", left(l.transDesc,30) "Description", 
            p.transDesc "Detail", 
            lpad(format(abs(l.transAmt),2),9,' ') Amount, 
            p.transDate "Receipt Date",
            lpad(format(abs(p.transAmt),2),7,' ') "Item Cost" 
            FROM ledger l 
            JOIN categories c on c.id = l.primaryCat 
            JOIN payment p on p.transId = l.id 
            JOIN categories c2 on c2.id = p.subCat
            JOIN stores s on s.id = p.storeId
            WHERE l.transDate BETWEEN "2015-01-01" AND "2015-12-30" 
            AND l.primaryCat = 11 
            AND p.storeId = 46
            AND p.subCat = 47;
         */
        Map<Integer, String> map = new HashMap<>();
        int ptr = 0;
        if (args > 0) {
            String[] argv = new String[args + 1];
            sql = sql.append("SELECT * from ledger l ");

            if (hasSecondaryCat) {
                sql = sql.append(", categories c2 ");
            }
            if (hasStore) {
                sql = sql.append(", stores s ");
            }
            if (hasSecondaryCat || hasStore) {
                sql = sql.append(", payment p ");
            }

            if (hasDescription) {
                argv[ptr] = "l.transDesc like \"%" + criteria.getSearchTarget() + "%\"";
                map.put(ptr++, "ledger");
            }

            if (hasStartDate && hasEndDate) {
                argv[ptr] = "l.transDate BETWEEN \"" + criteria.getStartDate() + "\" AND \"" + criteria.getEndDate() + "\"";
                map.put(ptr++, "ledger");
            } else if (hasStartDate) {
                argv[ptr] = "l.transDate >= \"" + criteria.getStartDate() + "\"";
                map.put(ptr++, "ledger");
            } else if (hasEndDate) {
                argv[ptr] = "l.transDate <= \"" + criteria.getEndDate() + "\"";
                map.put(ptr++, "ledger");
            }

            if (hasMinAmt && hasMaxAmt) {
                argv[ptr] = "abs(l.transAmt) BETWEEN " + criteria.getMinAmount() + " AND " + criteria.getMaxAmount();
                map.put(ptr++, "ledger");
            } else if (hasMinAmt) {
                argv[ptr] = "abs(l.transAmt) => " + criteria.getMinAmount();
                map.put(ptr++, "ledger");
            } else if (hasMaxAmt) {
                argv[ptr] = "abs(l.transAmt) <= " + criteria.getMaxAmount();
                map.put(ptr++, "ledger");
            }

            if (hasPrimaryCat) {
                argv[ptr] = "l.primaryCat = " + criteria.getPrimaryCat().getId();
                map.put(ptr++, "ledger");
            }

            if (hasSecondaryCat) {
                argv[ptr] = "p.subCat = " + criteria.getSecondaryCat().getId();
                map.put(ptr++, "payment");
            }

            if (hasStore) {
                argv[ptr] = "AND s.storeId = " + criteria.getStoreId();
                map.put(ptr++, "stores");
            }

            for (Integer i : map.keySet()) {
                if (map.get(i).equals("ledger")) {
                    if (i == 0) {
                        sql = sql.append(" WHERE ");
                    } else {
                        sql = sql.append(" AND ");
                    }
                }
                sql = sql.append(argv[i]);
            }
        }
        System.out.println(
                "SELECT statement: " + sql);
        criteria.setSqlStmt(sql.toString());

        criteria.getSqlProperty()
                .set(sql.toString());
        closeForm();
    }

    private void setFormData() {

    }

    @FXML
    public void closeForm() {
        stage.close();
    }

}
