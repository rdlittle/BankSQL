/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.controller;

import com.webfront.bean.LedgerManager;
import com.webfront.model.Category;
import com.webfront.model.Ledger;
import com.webfront.model.Payment;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author rlittle
 */
public class LedgerViewController implements Initializable {

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
    TreeTableColumn<Payment, Category> detailCat1Column;
    @FXML
    TreeTableColumn<Payment, Category> detailCat2Column;

    private final String dateFormat = "MM/dd/yyyy";
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);
    
    private final String decimalFormat = "###0.00";
    private final DecimalFormat decimalFormatter = new DecimalFormat(decimalFormat);
    
    public int accountNumber = 1;
    private final ObservableList<Ledger> list = FXCollections.<Ledger>observableArrayList();
    private final LedgerManager ledgerManager = LedgerManager.getInstance();

    private TreeItem<Ledger> root;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        root = new TreeItem<>();
        table.showRootProperty().set(false);
        
        decimalFormatter.setMaximumFractionDigits(2);

        idColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("id"));
        dateColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("transDate"));
        descriptionColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("transDesc"));
        amountColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("transAmt"));
        detailItemColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("transDesc"));
        detailAmountColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("transAmt"));
        detailCat1Column.setCellValueFactory(new TreeItemPropertyValueFactory<>("primaryCat"));
        detailCat2Column.setCellValueFactory(new TreeItemPropertyValueFactory<>("subCat"));
        
        dateColumn.setCellFactory(new Callback<TreeTableColumn<Ledger,Date>,TreeTableCell<Ledger,Date>> () {
            @Override public TreeTableCell<Ledger, Date> call(TreeTableColumn<Ledger, Date> p) {
                return new TreeTableCell<Ledger, Date>() {
                    @Override protected void updateItem(Date item, boolean empty) {
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
        
        amountColumn.setCellFactory(new Callback<TreeTableColumn<Ledger,Float>,TreeTableCell<Ledger,Float>> () {
            @Override public TreeTableCell<Ledger, Float> call(TreeTableColumn<Ledger, Float> p) {
                return new TreeTableCell<Ledger, Float>() {
                    @Override protected void updateItem(Float item, boolean empty) {
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
        
        detailAmountColumn.setCellFactory(new Callback<TreeTableColumn<Payment,Float>,TreeTableCell<Payment,Float>> () {
            
            @Override public TreeTableCell<Payment, Float> call(TreeTableColumn<Payment, Float> p) {
                return new TreeTableCell<Payment, Float>() {
                    @Override protected void updateItem(Float item, boolean empty) {
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
        
        Platform.runLater(() -> loadData());
    }

    public void loadData() {
        list.setAll(ledgerManager.getList(Integer.toString(accountNumber)));
        for (Ledger l : list) {
            TreeItem ti = new TreeItem<>(l);
            for (Payment p : l.getPayment()) {
                ti.getChildren().add(new TreeItem<Payment>(p));
            }
            root.getChildren().add(ti);
        }
        table.setRoot(root);
    }

}
