/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.app.utils.DateConvertor;
import com.webfront.bean.CategoryManager;
import com.webfront.bean.StoresManager;
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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
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
    Map<String, Category> primeCatMap;
    Map<String, Category> subCatMap;

    private ObservableList<Stores> stores;
    private ObservableList<String> primaryCats;
    private ObservableList<String> subCats;

    URL location = new StoreForm().getClass().getResource("/com/webfront/app/fxml/SearchForm.fxml");
    ResourceBundle resources = ResourceBundle.getBundle("com.webfront.app.bank");

    @FXML
    TextField txtSubject;

    @FXML
    DatePicker startDate;

    @FXML
    DatePicker endDate;

    @FXML
    TextField minAmount;

    @FXML
    TextField maxAmount;

    @FXML
    ComboBox<String> cbPrimary;

    @FXML
    ComboBox<String> cbSecondary;

    @FXML
    ComboBox<String> cbStores;

    @FXML
    Button btnOk;

    @FXML
    Button btnCancel;

    public SearchForm() {
        FXMLLoader loader = new FXMLLoader(location, resources);
        loader.setRoot(this);
        loader.setController(this);
        catManager = new CategoryManager();

        stage = new Stage();
        scene = new Scene(this);
        txtSubject = new TextField();
        cbPrimary = new ComboBox<>();
        cbSecondary = new ComboBox<>();
        cbStores = new ComboBox<>();
        btnOk = new Button();
        btnCancel = new Button();

        primaryCats = FXCollections.observableArrayList();
        subCats = FXCollections.observableArrayList();
        stores = FXCollections.observableArrayList();

        primeCatMap = new HashMap<>();
        subCatMap = new HashMap<>();
        endDate = new DatePicker();
        startDate = new DatePicker();
        minAmount = new TextField();
        maxAmount = new TextField();

        try {
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(StoreForm.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public SearchForm(LedgerView view, SearchCriteria sc) {
        this();
        this.criteria = sc;
        this.view = view;
        this.catManager = view.getCategoryManager();
        // Set the upper range to the latest available transaction date
        Ledger ledgerItem = (Ledger) view.getTable().getItems().get(0);
        Date date = ledgerItem.getTransDate();
        endDate.valueProperty().set(DateConvertor.toLocalDate(date));
        // Set the lower range to 60 days before latest date   
        startDate.valueProperty().set(endDate.getValue().minusDays(60));
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

        if (startDate.getValue() == null) {
            startDate.setValue(LocalDate.now());
        }
        if ("".equals(criteria.getStartDate())) {
            criteria.setStartDate(startDate.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        startDate.setValue(LocalDate.parse(criteria.getStartDate()));

        if (endDate.getValue() == null) {
            endDate.setValue(startDate.getValue().minusDays(30));
        }
        if ("".equals(criteria.getEndDate())) {
            criteria.setEndDate(endDate.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        endDate.setValue(LocalDate.parse(criteria.getEndDate()));

        ObservableList<Category> list = catManager.getCategories();
        for (Category c : list) {
            if (null != c.getParent() && c.getParent() == 0) {
                primeCatMap.put(c.getDescription(), c);
                getPrimaryCats().add(c.getDescription());
            }
        }

        cbPrimary.getItems().add("--Select--");
        cbPrimary.getItems().addAll(getPrimaryCats());
        cbPrimary.getSelectionModel().selectFirst();

        cbSecondary.getItems().add("--Select--");
        cbSecondary.getSelectionModel().selectFirst();

        StoresManager storeManager = new StoresManager();
        stores = storeManager.getList("SELECT * FROM stores ORDER BY storeName");
        for (Stores store : stores) {
            cbStores.getItems().add(store.getStoreName());
        }

        setFormData();
        stage.setScene(scene);
        stage.setTitle("Search Form");

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

        cbPrimary.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                String newCat = newValue.toString();
                if (primeCatMap.containsKey(newCat)) {
                    Category c = primeCatMap.get(newCat);
                    criteria.setPrimaryCat(c);
                    String sqlStmt = "SELECT * FROM categories WHERE parent = " + Integer.toString(c.getId());
                    sqlStmt += " order by description";
                    ObservableList<Category> subCatList = catManager.getCategories(sqlStmt);
                    cbSecondary.getItems().clear();
                    subCatMap.clear();
                    for (Category cat2 : subCatList) {
                        subCatMap.put(cat2.getDescription(), cat2);
                    }
                    cbSecondary.getItems().addAll(subCatMap.keySet());
                }
            }
        });

        cbSecondary.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue != null) {
                    String newCat = newValue.toString();
                    if (subCatMap.containsKey(newCat)) {
                        Category c = subCatMap.get(newCat);
                        criteria.setSecondaryCat(c);
                    }
                }
            }
        });

        txtSubject.textProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                criteria.setSearchTarget(newValue.toString());
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
        String sql = "";
        float min, max;

        if (criteria.getSecondaryCat() != null && criteria.getSecondaryCat().getId() != null) {
            sql += " INNER join distribution d ON d.transId = l.id ";
        }

        if (criteria.getStartDate() != null && !criteria.getStartDate().isEmpty()) {
            sql += "WHERE l.transDate >= \"";
            sql += criteria.getStartDate() + "\"";
        }

        if (criteria.getEndDate() != null) {
            if (criteria.getStartDate() != null && !criteria.getStartDate().isEmpty()) {
                sql += " AND ";
            } else {
                sql += "WHERE ";
            }
            sql += "l.transDate <= \"";
            sql += criteria.getEndDate() + "\"";
        }

        if (criteria.getPrimaryCat() != null) {
            if (criteria.getPrimaryCat().getId() != null) {
                if (sql.isEmpty()) {
                    sql += "WHERE ";
                } else {
                    sql += " AND ";
                }
                sql += "l.primaryCat = ";
                sql += criteria.getPrimaryCat().getId();
            }
        }

        min = 0;
        max = 0;

        if (criteria.getMinAmount() != null && !criteria.getMinAmount().isEmpty()) {
            min = Float.parseFloat(criteria.getMinAmount());
            if (sql.isEmpty()) {
                sql += "WHERE ";
            } else {
                sql += " AND ";
            }
            sql += "l.transAmt >= ";
            sql += criteria.getMinAmount();
        }

        if (criteria.getMaxAmount() != null && !criteria.getMaxAmount().isEmpty()) {
            max = Float.parseFloat(criteria.getMaxAmount());
            if (max >= min) {
                if (sql.isEmpty()) {
                    sql += "WHERE ";
                } else {
                    sql += " AND ";
                }
                sql += "l.transAmt <= ";
                sql += criteria.getMaxAmount();
            }
        }

        if (!sql.isEmpty()) {
            if (criteria.getSearchTarget() != null && !criteria.getSearchTarget().isEmpty()) {
                sql += " AND l.transDesc like \"%" + criteria.getSearchTarget() + "%\"";
            }
            if (criteria.getSecondaryCat() != null && criteria.getSecondaryCat().getId() != null) {
                sql += " AND d.categoryId = " + criteria.getSecondaryCat().getId().toString();
            }
            sql = "SELECT * FROM ledger l " + sql;
        }

        System.out.println(
                "SELECT statement: " + sql);
        criteria.setSqlStmt(sql);

        criteria.getSqlProperty()
                .set(sql);
        closeForm();
    }

    private void setFormData() {

    }

    @FXML
    public void searchStringChanged() {
        if (txtSubject.getText() != null) {

            criteria.setSearchTarget(txtSubject.getText());
        }
    }

    @FXML
    public void closeForm() {
        stage.close();
    }

    /**
     * @return the primaryCats
     */
    public ObservableList<String> getPrimaryCats() {
        return primaryCats;
    }

    /**
     * @param primaryCats the primaryCats to set
     */
    public void setPrimaryCats(ObservableList<String> primaryCats) {
        this.primaryCats = primaryCats;
    }

    /**
     * @return the subCats
     */
    public ObservableList<String> getSubCats() {
        return subCats;
    }

    /**
     * @param subCats the subCats to set
     */
    public void setSubCats(ObservableList<String> subCats) {
        this.subCats = subCats;
    }
}
