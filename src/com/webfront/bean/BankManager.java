/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.bean;

import com.webfront.model.Account;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javax.persistence.Query;

/**
 *
 * @author rlittle
 */
public class BankManager extends DBManager {
    
    private static BankManager instance;
    
    private final ObservableList<Account> accountList;
    
    protected BankManager() {
        BankManager.instance = null;
        this.accountList = FXCollections.<Account>observableArrayList();
    }
    
    public static synchronized BankManager getInstance() {
        if(instance==null) {
            instance = new BankManager();
        }
        return instance;
    }
    
    public synchronized void addListener(ListChangeListener lcl) {
        accountList.addListener(lcl);
    }
    
    public synchronized void removeListener(ListChangeListener lcl) {
        accountList.removeListener(lcl);
    }    
    
    public synchronized void addListener(InvalidationListener il) {
        accountList.addListener(il);
    }
    
    public synchronized void removeListener(InvalidationListener il) {
        accountList.removeListener(il);
    }    

    @Override
    public List getList(String s) {
        Query query = em.createNamedQuery("Account.findAll");
        List<Account> list = query.getResultList();
        accountList.setAll(list);
        return accountList;
    }

    @Override
    public ObservableList doSqlQuery(String q) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
