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
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

/**
 *
 * @author rlittle
 */
public class LedgerView extends AnchorPane {

    public int accountNumber;
    private static LedgerView ledgerView;
    private TableView<Ledger> table;
    private final ObservableList<Ledger> list = FXCollections.<Ledger>observableArrayList();
    private final LedgerManager ledgerManager;
    private CategoryManager categoryManager;
    Button btnSearch;
    Button btnReset;

    ContextMenu contextMenu;

    TableColumn dateColumn;
    TableColumn descColumn;
    TableColumn primaryCatColumn;
    TableColumn<Ledger, String> subCatColumn;
    TableColumn<Ledger, Float> transAmtColumn;
    TableColumn<Ledger, Float> transBalColumn;

    public SimpleBooleanProperty isRebalance;
    public Ledger selectedItem;
    public SimpleBooleanProperty isLoading = new SimpleBooleanProperty(true);

    /**
     *
     * @param acctNum
     */
    protected LedgerView(int acctNum) {
        super();
        this.setStyle("-fx-background-color: #336699;");
        isRebalance = new SimpleBooleanProperty(false);

//        HBox buttonBox = new HBox();
//        buttonBox.getStyleClass().add("panel");
//        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
//        buttonBox.setPadding(new Insets(8));
//        buttonBox.setSpacing(10);

        accountNumber = acctNum;
        ledgerManager = LedgerManager.getInstance();
        categoryManager = CategoryManager.getInstance();

        table = new TableView<>();

        dateColumn = new TableColumn("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory("transDate"));
        dateColumn.setCellFactory(new CellFormatter<>());
        dateColumn.setMinWidth(85.0);

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

        contextMenu = new ContextMenu();
        contextMenu.setOnShown(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
//                System.out.println("shown");
            }
        });

        MenuItem ctxItem1 = new MenuItem("Edit");
        ctxItem1.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                System.out.println("About");
            }
        });
        MenuItem ctxItem2 = new MenuItem("Delete");
        ctxItem2.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                System.out.println("About");
            }
        });
        MenuItem ctxItem3 = new MenuItem("Refresh");
        ctxItem3.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                System.out.println("About");
            }
        });
        contextMenu.getItems().addAll(ctxItem1, ctxItem2, ctxItem3);

        EventHandler<MouseEvent> click = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                selectedItem = (Ledger) table.getSelectionModel().getSelectedItem();
                if (event.getButton() == MouseButton.SECONDARY) {
                    contextMenu.show(LedgerView.this, Side.TOP, event.getSceneX(), event.getSceneY());
                }
                if (event.getClickCount() == 2) {
                    if (selectedItem != null) {
                        getLedgerManager().refresh(selectedItem);
                        LedgerForm form = new LedgerForm(LedgerView.this, selectedItem);
                    }
                }
            }
        };

        Platform.runLater(() -> loadData());
        
        table.addEventHandler(MouseEvent.MOUSE_CLICKED, click);

        list.addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change c) {
                table.getItems().addAll(list);
                isLoading.set(false);
            }
        });
        
        
        table.getItems().addAll(list);

        table.getColumns().addAll(dateColumn, descColumn, primaryCatColumn, subCatColumn, transAmtColumn, transBalColumn);

//        btnSearch = new Button("Search");
//        btnSearch.setOnAction(new EventHandler() {
//            @Override
//            public void handle(Event event) {
//                doSearch("");
//            }
//        });
//
//        btnReset = new Button("Reset");
//        btnReset.setOnAction(new EventHandler() {
//            @Override
//            public void handle(Event event) {
//                ObservableList<Ledger> copyOfList;
//                copyOfList = ledgerManager.getList(Integer.toString(accountNumber));
//                list.clear();
//                list.addAll(copyOfList);
//                table.getItems().clear();
//                table.setItems(list);
//            }
//        });
//
//        buttonBox.getChildren().addAll(btnSearch, btnReset);

        AnchorPane.setTopAnchor(table, 0.0);
        AnchorPane.setLeftAnchor(table, 0.0);
        AnchorPane.setRightAnchor(table, 0.0);
        AnchorPane.setBottomAnchor(table, 0.0);

//        AnchorPane.setBottomAnchor(buttonBox, 0.0);
//        AnchorPane.setLeftAnchor(buttonBox, 0.0);
//        AnchorPane.setRightAnchor(buttonBox, 0.0);
//
//        getChildren().addAll(table, buttonBox);
        getChildren().add(table);
    }

    public void loadData() {
        list.setAll(ledgerManager.getList(Integer.toString(accountNumber)));
    }

    public synchronized LedgerView getInstance(int acctNum) {
        if (ledgerView == null) {
            ledgerView = new LedgerView(acctNum);
        }
        return ledgerView;
    }

    public static LedgerView newInstance(int acctNum) {
        LedgerView view = new LedgerView(acctNum);
        view.accountNumber = acctNum;
        ledgerView = view;
        return view;
    }

    public void doSearch(String sql) {
        SearchForm form = new SearchForm(this, new SearchCriteria());
        form.showForm();
        if (form.criteria.getSqlStmt() != null) {
            ObservableList<Ledger> copyOfList;
            copyOfList = ledgerManager.doSqlQuery(form.criteria.getSqlStmt());
            list.clear();
            list.addAll(copyOfList);
            table.getItems().clear();
            table.setItems(list);
        }
    }

    public void doRebalance() {
        RebalanceForm rebal = RebalanceForm.getInstance(LedgerView.this);
        rebal.showForm();
        if (rebal.hasChanged.get()) {
            ledgerManager.rebalance(accountNumber, rebal.getCriteria());
            ObservableList<Ledger> copyOfList;
            copyOfList = ledgerManager.getList(Integer.toString(accountNumber));
            list.clear();
            list.addAll(copyOfList);
            table.getItems().clear();
            table.setItems(list);
        }
    }

    public void doRefresh() {
        ObservableList<Ledger> copyOfList;
        copyOfList = ledgerManager.getList(Integer.toString(accountNumber));
        list.clear();
        list.addAll(copyOfList);
        table.getItems().clear();
        table.setItems(list);
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
        this.list.clear();
        this.list.addAll(list);
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
