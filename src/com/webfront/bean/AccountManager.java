/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.bean;

import com.webfront.model.Account;

import java.io.Serializable;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.persistence.Query;

/**
 *
 * @author rlittle
 */
public class AccountManager extends DBManager<Account> implements Serializable {

    private ObservableList<Account> list;
    private static AccountManager manager=null;
    
    public AccountManager() {
        super();
        list = FXCollections.emptyObservableList();
    }
    @Override
    public List<Account> getList(String s) {
        Query query = em.createNamedQuery(s);
        list = FXCollections.observableList(query.getResultList());
        return list;
    }
    
    private void setList(String stmt) {
        Query query = em.createNamedQuery(stmt);
        list = FXCollections.observableList(query.getResultList());
    }
    
    public ObservableList<Account> getAccounts() {
        setList("Account.findAll");
        return list;
    }
    
    public Account getAccount(int id) {
        Query query = em.createNamedQuery("Account.findById");
        query.setParameter("id", id);
        Account acct = (Account) query.getSingleResult();
        return acct;
    }

    @Override
    public ObservableList<Account> doSqlQuery(String q) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static AccountManager getInstance() {
        if(manager == null) {
            manager = new AccountManager();
        }
        return manager;
    }

}
