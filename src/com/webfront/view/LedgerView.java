/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.bean.CategoryManager;
import com.webfront.bean.LedgerManager;
import com.webfront.model.Category;
import com.webfront.model.Ledger;
import com.webfront.model.SearchCriteria;
import java.util.Comparator;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

/**
 *
 * @author rlittle
 */
public class LedgerView extends AnchorPane {

    public int accountNumber;
    private static LedgerView ledgerView;
    private TableView<Ledger> table;
    private ObservableList<Ledger> list;
    private final LedgerManager ledgerManager;
    private CategoryManager categoryManager;
    Button btnSearch;
    Button btnReset;

    TableColumn dateColumn;
    TableColumn descColumn;
    TableColumn primaryCatColumn;
    TableColumn<Ledger, String> subCatColumn;
    TableColumn<Ledger, Float> transAmtColumn;
    TableColumn<Ledger, Float> transBalColumn;

    public SimpleBooleanProperty isRebalance;
    public Ledger selectedItem;

    public LedgerView(int acctNum) {
        super();
        this.setStyle("-fx-background-color: #336699;");
        isRebalance = new SimpleBooleanProperty(false);

        HBox buttonBox = new HBox();
        buttonBox.getStyleClass().add("panel");
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        buttonBox.setPadding(new Insets(8));
        buttonBox.setSpacing(10);

        accountNumber = acctNum;
        ledgerManager = new LedgerManager();
        categoryManager = new CategoryManager();

        list = ledgerManager.getList(Integer.toString(acctNum));

        table = new TableView<>();

        dateColumn = new TableColumn("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory("transDate"));
        dateColumn.setCellFactory(new CellFormatter<>());

        descColumn = new TableColumn("Description");
        descColumn.setCellValueFactory(new PropertyValueFactory("transDesc"));
        descColumn.setMinWidth(620.00);

        primaryCatColumn = new TableColumn("Cat 1");
        primaryCatColumn.setCellValueFactory(new PropertyValueFactory("primaryCat"));
        primaryCatColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Ledger, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Ledger, String> param) {
                if (param.getValue().getPrimaryCat() != null) {
                    return new SimpleStringProperty(param.getValue().getPrimaryCat().getDescription());
                }
                return null;
            }
        });
        primaryCatColumn.setMinWidth(150.0);

        subCatColumn = new TableColumn("Cat 2");
        subCatColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Ledger, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Ledger, String> param) {
                if (param.getValue().getDistribution() != null) {
                    if (!param.getValue().getDistribution().isEmpty()) {
                        Category c = param.getValue().getDistribution().get(0).getCategory();
                        if (c != null) {
                            String desc = c.getDescription();
                            return new SimpleStringProperty(desc);
                        }
                    }
                }
                return null;
            }
        });
        subCatColumn.setMinWidth(215.0);

        transAmtColumn = new TableColumn("Amount");
        transAmtColumn.setCellValueFactory(new PropertyValueFactory<>("transAmt"));
        transAmtColumn.setMinWidth(100.0);
        transAmtColumn.setCellFactory(new CellFormatter<>(TextAlignment.RIGHT));

        transBalColumn = new TableColumn("Balance");
        transBalColumn.setCellValueFactory(new PropertyValueFactory<>("transBal"));
        transBalColumn.setCellFactory(new CellFormatter<>(TextAlignment.RIGHT));
        transBalColumn.setMinWidth(100.0);

        EventHandler<MouseEvent> click = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    selectedItem = (Ledger) table.getSelectionModel().getSelectedItem();
                    if (selectedItem != null) {
                        getLedgerManager().refresh(selectedItem);
                        LedgerForm form = new LedgerForm(LedgerView.this, selectedItem);
                    }
                } else {
                    selectedItem = (Ledger) table.getSelectionModel().getSelectedItem();
                }
            }
        };

        table.addEventHandler(MouseEvent.MOUSE_CLICKED, click);
        table.getItems().addAll(list);
        table.getColumns().addAll(dateColumn, descColumn, primaryCatColumn, subCatColumn, transAmtColumn, transBalColumn);

        btnSearch = new Button("Search");
        btnSearch.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                doSearch("");
            }
        });

        btnReset = new Button("Reset");
        btnReset.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                list = ledgerManager.getList("Ledger.findAll");
                table.getItems().clear();
                table.setItems(list);
            }
        });

        isRebalance.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                RebalanceForm rebal = RebalanceForm.getInstance(LedgerView.this);
                rebal.hasChanged.addListener(new InvalidationListener() {
                    @Override
                    public void invalidated(Observable observable) {
                        ledgerView.getChildren().remove(rebal);
                        table.removeEventHandler(MouseEvent.MOUSE_CLICKED, rebal.click);
                        //ledgerManager.rebalance(accountNumber, rebal.startTrans, rebal.endTrans, rebal.balance);
                        ledgerManager.rebalance(accountNumber, rebal.getCriteria());
                    }
                });
                table.addEventHandler(MouseEvent.MOUSE_CLICKED, rebal.click);
                rebal.showForm();                
            }
        });

        ledgerManager.isChanged.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                list=ledgerManager.getList(Integer.toString(acctNum));
            }
        });
        
        buttonBox.getChildren().addAll(btnSearch, btnReset);

        AnchorPane.setTopAnchor(table, 0.0);
        AnchorPane.setLeftAnchor(table, 0.0);
        AnchorPane.setRightAnchor(table, 0.0);
        AnchorPane.setBottomAnchor(table, 0.0);

        AnchorPane.setBottomAnchor(buttonBox, 0.0);
        AnchorPane.setLeftAnchor(buttonBox, 0.0);
        AnchorPane.setRightAnchor(buttonBox, 0.0);

        getChildren().addAll(table, buttonBox);

        ledgerView = LedgerView.this;
    }

    public LedgerView getInstance(int acctNum) {
        if (ledgerView == null) {
            ledgerView = new LedgerView(acctNum);
        }
        return ledgerView;
    }

    public LedgerView newInstance(int acctNum) {
        LedgerView view = new LedgerView(acctNum);
        view.accountNumber = acctNum;
        ledgerView = view;
        return view;
    }

    public void doSearch(String sql) {
        SearchForm form = new SearchForm(this, new SearchCriteria());
        form.showForm();
        if (form.criteria.getSqlStmt() != null) {
            list = ledgerManager.doSqlQuery(form.criteria.getSqlStmt());
            table.getItems().clear();
            table.setItems(list);
        }
    }

    /**
     * @return the table
     */
    public TableView<Ledger> getTable() {
        return table;
    }

    /**
     * @param table the table to set
     */
    public void setTable(TableView<Ledger> table) {
        this.table = table;
    }

    /**
     * @return the list
     */
    public ObservableList<Ledger> getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(ObservableList<Ledger> list) {
        this.list = list;
    }

    /**
     * @return the categoryManager
     */
    public CategoryManager getCategoryManager() {
        return categoryManager;
    }

    /**
     * @param categoryManager the categoryManager to set
     */
    public void setCategoryManager(CategoryManager categoryManager) {
        this.categoryManager = categoryManager;
    }

    /**
     * @return the ledgerManager
     */
    public LedgerManager getLedgerManager() {
        return ledgerManager;
    }

    public static Comparator<Ledger> LedgerComparator = new Comparator<Ledger>() {
        @Override
        public int compare(Ledger ledger1, Ledger ledger2) {
            Integer id1 = ledger1.getId();
            Integer id2 = ledger2.getId();
            return id2.compareTo(id1);
        }
    };

}
