/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.app.utils;

import com.webfront.bean.LedgerManager;
import com.webfront.model.Account;
import com.webfront.model.Ledger;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author rlittle
 */
public abstract class Exporter implements Runnable {
    private final ObservableList<Ledger> list;
    public final File outputFile;
    private LocalDate startDate;
    private LocalDate endDate;
    private Account account;
    DoubleProperty progressProperty;
    
    public Exporter(File f) {
        outputFile = f;
        progressProperty = new SimpleDoubleProperty();
        list = FXCollections.<Ledger>observableArrayList();
    }
    
    public abstract void doExport();
    
    @Override
    public void run() {
//        try {
//            doSelect();
//            double itemsDone = 0D;
//            Double itemCount = (double) getList().size();
//            progressProperty.set(itemsDone);
//            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
//            for (Ledger item : getList()) {
//                progressProperty.set(itemsDone / itemCount);
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(Exporter.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
    
    public void doSelect() {
        getList().clear();
        String stmt = "SELECT * FROM ledger ";
        String sDate = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String eDate = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        stmt += "WHERE transDate BETWEEN '"+sDate+"' AND '"+eDate+"'";
        if(account != null) {
            stmt += " AND accountNum = "+Integer.toString(account.getId());
        }
        stmt += " ORDER BY transDate";
        getList().setAll(LedgerManager.getInstance().doSqlQuery(stmt));
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
    
}
