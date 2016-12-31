/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.bean;

import com.webfront.model.Account;
import com.webfront.model.ExportFormat;
import java.io.Serializable;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;
import javax.persistence.Query;

/**
 *
 * @author rlittle
 */
public class BankManager extends DBManager<Account> implements Serializable {
    
    private static BankManager instance = null;
    
    private final ObservableList<Account> accountList;
    
    protected BankManager() {
        super();
        accountList = FXCollections.<Account>observableArrayList();
    }
    
    public static BankManager getInstance() {
        if(instance==null) {
            instance = new BankManager();
        }
        return instance;
    }
    
    public void addListener(ListChangeListener lcl) {
        accountList.addListener(lcl);
    }
    
    public void removeListener(ListChangeListener lcl) {
        accountList.removeListener(lcl);
    }    
    
    public void addListener(InvalidationListener il) {
        accountList.addListener(il);
    }
    
    public void removeListener(InvalidationListener il) {
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
    
    
    public static class AccountConverter extends StringConverter {

        @Override
        public String toString(Object object) {
            Account acct = (Account) object;
            if(object==null) {
                acct = new Account();
            }
            return acct.getAccountName();
        }

        @Override
        public Object fromString(String string) {
            return string;
        }
        
    }
}
