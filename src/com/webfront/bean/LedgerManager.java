/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.bean;

import com.webfront.model.Account;
import com.webfront.model.Ledger;
import com.webfront.model.SearchCriteria;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.persistence.Query;

public class LedgerManager extends DBManager implements Serializable {

    private List<Ledger> selectedItems;

    /**
     *
     */
    public SimpleBooleanProperty isChanged;

    public LedgerManager() {
        selectedItems = new ArrayList<>();
        isChanged = new SimpleBooleanProperty(false);
    }

    @Override
    public ObservableList<Ledger> getList(String q) {
        Query query = em.createNamedQuery("Ledger.findByAccountNum");
        query.setParameter("accountNum", Integer.parseInt(q));
        List<Ledger> list = query.getResultList();
        ObservableList olist = FXCollections.observableList(list);
        return olist;
    }

    /**
     *
     * @param q
     * @return
     */
    @Override
    public ObservableList<Ledger> doSqlQuery(String q) {
        Query query = em.createNativeQuery(q, Ledger.class);
        List<Ledger> list = query.getResultList();
        ObservableList olist = FXCollections.observableList(list);
        return olist;
    }

    public List getResults(String q) {
        Query query = em.createNativeQuery(q);
        List list = query.getResultList();
        return list;
    }

    public Ledger getItem(int idx) throws javax.persistence.NoResultException {
        Query query = em.createNamedQuery("Ledger.findById");
        query.setParameter("id", idx);
        Ledger ledger;
        ledger = (Ledger) query.getSingleResult();
        return ledger;
    }

    /**
     *
     * @param accountNum
     * @return most recent entry in the ledger
     */
    public int getLastId(int accountNum) {
        String queryString = "SELECT id FROM ledger WHERE accountNum = ";
        queryString += Integer.toString(accountNum);
        queryString += " ORDER BY id DESC LIMIT 0,1";
        Query query = em.createNativeQuery(queryString);
        List<Integer> result = query.getResultList();
        if (result.isEmpty()) {
            return 0;
        }
        return result.get(0);
    }

    public int getLastId() {
        return 1;
    }

    public void rebalance(int acct, SearchCriteria criteria) {
        Query query = em.createNamedQuery("Account.findById");
        query.setParameter("id", acct);
        Account account = (Account) query.getSingleResult();
        query = em.createNamedQuery("Ledger.findRangeByDate");
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        
        LocalDate ld1 = criteria.getDateRange()[0];
        LocalDate ld2 = criteria.getDateRange()[1];
        
        Instant instant = ld1.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        Date d1 = Date.from(instant);
        instant = ld2.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        Date d2 = Date.from(instant);
        
        query.setParameter("accountNum", acct);
        query.setParameter("startDate", d1);
        query.setParameter("endDate", d2);
        selectedItems = query.getResultList();
        if (!selectedItems.isEmpty()) {
            float balance = criteria.getBeginningBalance();
            for (Ledger l : selectedItems) {
                float amt = l.getTransAmt();
                if (account.getAccountType() == Account.AccountType.CREDIT) {
                    balance += amt;
                } else {
                    balance -= amt;
                }
                l.setTransBal(balance);
                update(l);
            }
            isChanged.set(true);
        }        
    }

    /**
     * @return the selectedItems
     */
    public List<Ledger> getSelectedItems() {
        return selectedItems;
    }

    /**
     * @param selectedItems the selectedItems to set
     */
    public void setSelectedItems(ArrayList<Ledger> selectedItems) {
        this.selectedItems = selectedItems;
    }

}
