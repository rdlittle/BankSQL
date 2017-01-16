/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.app.utils;

import com.webfront.bean.LedgerManager;
import com.webfront.bean.PaymentManager;
import com.webfront.model.Account;
import com.webfront.model.Ledger;
import com.webfront.model.LedgerItem;
import com.webfront.model.Payment;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

/**
 *
 * @author rlittle
 */
public abstract class Exporter extends Task<Void> {

    public final ObservableList<Ledger> list;
    public final ObservableList<LedgerItem> itemsList;
    public final File outputFile;
    public LocalDate startDate;
    public LocalDate endDate;
    public Account account;
    DoubleProperty progressProperty;
    public BooleanProperty isDoneProperty = new SimpleBooleanProperty(false);

    public Exporter(File f) {
        outputFile = f;
        progressProperty = new SimpleDoubleProperty();
        list = FXCollections.<Ledger>observableArrayList();
        itemsList = FXCollections.<LedgerItem>observableArrayList();
    }

    public abstract void doExport();

    public void doSelect() {
        getList().clear();
        getItemsList().clear();

        String sDate = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String eDate = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String stmt = "SELECT * FROM ledger ";
                stmt += "WHERE transDate BETWEEN '" + sDate + "' AND '" + eDate + "'";
                if (account != null) {
                    stmt += " AND accountNum = " + Integer.toString(account.getId());
                }
                stmt += " ORDER BY transDate";
                getList().setAll(LedgerManager.getInstance().doSqlQuery(stmt));
            }
        });
        t.start();
        while (t.isAlive()) {

        }
        String stmt = "SELECT * FROM ledger ";

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                String stmt = "SELECT * from payment WHERE transId IS NULL";
                stmt += " AND transDate BETWEEN '" + sDate + "' AND '" + eDate + "'";
                if (account != null) {
                    stmt += " AND accountNum = " + Integer.toString(account.getId());
                }
                stmt += " ORDER BY transDate";
                Map<String, Object> map = new HashMap<>();
                map.put("accountNum", account.getId());
                map.put("startDate", DateConvertor.fromLocalDate(startDate));
                map.put("endDate", DateConvertor.fromLocalDate(endDate));
                ObservableList<Payment> ilist = PaymentManager.getInstance().doNamedQuery("Payment.findOrphansRange", map);
                for (Payment p : ilist) {
                    getItemsList().add(new LedgerItem(p));
                }
            }
        });
        t2.start();
        while (t2.isAlive()) {

        }
        
    }

    public DoubleProperty getProgressProperty() {
        return progressProperty;
    }

    /**
     * @return the startDate
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * @param account the account to set
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * @return the list
     */
    public ObservableList<Ledger> getList() {
        return list;
    }

    /**
     * @return the itemsList
     */
    public ObservableList<LedgerItem> getItemsList() {
        return itemsList;
    }

}
