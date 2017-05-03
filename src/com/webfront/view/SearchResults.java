/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.bean.LedgerManager;
import com.webfront.model.Category;
import com.webfront.model.Ledger;
import com.webfront.model.SearchCriteria;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 *
 * @author rlittle
 */
public final class SearchResults extends Pane {

    private ObservableList<?> resultsList;
    private TableView table;
    Class clzz = null;
    public String result;
    public Stage stage;

    private Button btnOK;
    private Button btnCancel;
    private Button btnSearch;
    SimpleStringProperty resultProperty;
    SearchCriteria searchCriteria;

    /**
     *
     */
    public SearchResults() {
        resultProperty = new SimpleStringProperty();
        resultProperty.set("");
        table = new TableView();
        resultsList = FXCollections.observableArrayList();
    }

    /**
     *
     * @param list
     */
    public SearchResults(ObservableList<?> list) {
        this();
        this.resultsList = list;
        clzz = this.resultsList.get(0).getClass();
        createView();
    }

    /**
     *
     * @param list
     */
    public void setResultsList(ObservableList<?> list) {
        this.resultsList = list;
        clzz = this.resultsList.get(0).getClass();
        createView();
    }

    /**
     *
     * @return
     */
    public ObservableList<?> getResultsList() {
        return this.resultsList;
    }

    /**
     * @return the table
     */
    public TableView getTable() {
        return table;
    }

    /**
     * @param table the table to set
     */
    public void setTable(TableView table) {
        this.table = table;
    }

    public void createView() {
        VBox vbox = new VBox();
        btnOK = new Button("OK");
        btnCancel = new Button("Cancel");
        btnSearch = new Button("Change Search");

        TilePane tileButtons = new TilePane(Orientation.HORIZONTAL);
        tileButtons.setPadding(new Insets(20, 10, 20, 0));
        tileButtons.setHgap(10.0);
        tileButtons.setVgap(8.0);
        tileButtons.setAlignment(Pos.CENTER_RIGHT);
        tileButtons.setStyle("fx-background: blue;");

        Label label = new Label("Target date: " + searchCriteria.getDate() + " Amount: " + searchCriteria.getAmount());
        label.setAlignment(Pos.CENTER_LEFT);
        label.setStyle("fx-background: white;");
        label.setPadding(new Insets(0, 0, 0, 10));

        btnOK.setMaxWidth(Double.MAX_VALUE);
        btnCancel.setMaxWidth(Double.MAX_VALUE);
        btnSearch.setMaxWidth(Double.MAX_VALUE);

        btnOK.setOnAction(new EventHandler() {

            @Override
            public void handle(Event event) {
                int rowNum = getTable().getSelectionModel().getSelectedIndex();
                if (rowNum != -1) {
                    if (clzz.getSimpleName().equals("Ledger")) {
                        Ledger ledgerItem = (Ledger) getTable().getSelectionModel().getSelectedItem();
                        result = ledgerItem.getId().toString();
                    }
                } else {
                    result = "-1";
                }
                resultProperty.set(result);
                stage.close();
            }
        });

        btnCancel.setOnAction(new EventHandler() {

            @Override
            public void handle(Event event) {
                resultProperty.set("-1");
                stage.close();
            }
        });

        btnSearch.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                SearchForm searchForm = new SearchForm();
                searchForm.criteria = searchCriteria;
                searchCriteria.getSqlProperty().addListener(new ChangeListener() {
                    @Override
                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                        resultsList.clear();
                        LedgerManager ledgerManager = LedgerManager.getInstance();
                        resultsList = ledgerManager.doSqlQuery(searchCriteria.getSqlStmt());
                        getTable().getItems().clear();
                        getTable().setItems(resultsList);
                    }
                });

                searchForm.setForm();
                searchForm.showForm();

            }
        });

        tileButtons.getChildren().addAll(btnOK, btnCancel, btnSearch);

        if (clzz.getSimpleName().equals("Ledger")) {
            // Build results for a ledger list
            TableColumn<Ledger, String> id = new TableColumn<>("ID");
            TableColumn<Ledger, String> date = new TableColumn<>("Date");
            TableColumn<Ledger, String> description = new TableColumn<>("Description");
            TableColumn<Ledger, String> primaryCat = new TableColumn<>("Cat 1");
            TableColumn<Ledger, String> secondaryCat = new TableColumn<>("Cat 2");
            TableColumn<Ledger, String> amount = new TableColumn<>("Amount");

            id.setCellValueFactory(new PropertyValueFactory("id"));

            date.setCellValueFactory(new PropertyValueFactory("transDate"));
            date.setCellFactory(new CellFormatter<>());

            description.setCellValueFactory(new PropertyValueFactory("transDesc"));
            description.setMinWidth(520.00);

            primaryCat.setCellValueFactory(new PropertyValueFactory("primaryCat"));
            primaryCat.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Ledger, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Ledger, String> param) {
                    if (param.getValue().getPrimaryCat() != null) {
                        return new SimpleStringProperty(param.getValue().getPrimaryCat().getDescription());
                    }
                    return null;
                }
            });
            primaryCat.setMinWidth(150.0);

            secondaryCat.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Ledger, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Ledger, String> param) {
                    if (param.getValue().getSubCat() != null) {
                        Category c = param.getValue().getSubCat();
                        if (c != null) {
                            String desc = c.getDescription();
                            return new SimpleStringProperty(desc);
                        }
                    }
                    return null;
                }
            });
            secondaryCat.setMinWidth(215.0);

            amount.setCellValueFactory(new PropertyValueFactory<>("transAmt"));
            amount.setMinWidth(100.0);
            amount.setCellFactory(new CellFormatter<>(TextAlignment.RIGHT));

            table.getColumns().addAll(id, date, description, primaryCat, secondaryCat, amount);
            table.setItems(resultsList);

            Scene scene = new Scene(this);
            GridPane bottomPane = new GridPane();

            ColumnConstraints col0 = new ColumnConstraints();
            ColumnConstraints col1 = new ColumnConstraints();

            col0.setHalignment(HPos.LEFT);
            col0.setHgrow(Priority.ALWAYS);
            col1.setHalignment(HPos.RIGHT);
            col1.setHgrow(Priority.NEVER);

            bottomPane.getColumnConstraints().addAll(col0, col1);
            bottomPane.add(label, 0, 0);
            bottomPane.add(tileButtons, 1, 0);

            vbox.getChildren().addAll(table, bottomPane);
            this.getChildren().add(vbox);

            stage = new Stage();
            stage.setTitle("Search Results");
            stage.setScene(scene);
            stage.show();
        } else if (clzz.getSimpleName().equals("Payment")) {
            // Build results for a receipts list;
        }
    }

    /**
     * @return the btnOK
     */
    public Button getBtnOK() {
        return btnOK;
    }

    /**
     * @return the btnCancel
     */
    public Button getBtnCancel() {
        return btnCancel;
    }

    /**
     * @return the btnSearch
     */
    public Button getBtnSearch() {
        return btnSearch;
    }

}
