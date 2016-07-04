/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.bean;

import com.webfront.model.Account;
import com.webfront.model.Ledger;
import com.webfront.model.Payment;
import com.webfront.model.SearchCriteria;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class LedgerManager extends DBManager {

    private List<Ledger> selectedItems;
    private final ObservableList<Ledger> ledgerList;
    private static LedgerManager instance = null;
    private final Account pettyCashAccount;

    public static enum EntryType {
        CREDIT, DEBIT;
    }

    /**
     *
     */
    protected LedgerManager() {
        this.ledgerList = FXCollections.<Ledger>observableArrayList();
        selectedItems = new ArrayList<>();
        pettyCashAccount = AccountManager.getInstance().getAccount("Petty Cash");
    }

    public synchronized static LedgerManager getInstance() {
        if (instance == null) {
            instance = new LedgerManager();
        }
        return instance;
    }

    @Override
    public synchronized ObservableList<Ledger> getList(String q) {
        Query query = em.createNamedQuery("Ledger.findByAccountNum");
        query.setParameter("accountNum", Integer.parseInt(q));
        List<Ledger> list = query.getResultList();
        ledgerList.setAll(list);
        return this.ledgerList;
    }

    /**
     *
     * @param q
     * @return
     */
    @Override
    public synchronized ObservableList<Ledger> doSqlQuery(String q) {
        Query query = em.createNativeQuery(q, Ledger.class);
        List<Ledger> list = query.getResultList();
        ObservableList olist = FXCollections.observableList(list);
        return olist;
    }

    /**
     *
     * @param qName
     * @return
     */
    public synchronized ObservableList<Ledger> doNamedQuery(String qName) {
        Query query = em.createNamedQuery(qName, Ledger.class);
        List<Ledger> list = query.getResultList();
        ObservableList olist = FXCollections.observableList(list);
        return olist;
    }

    /**
     *
     * @param qName
     * @return
     */
    public synchronized ObservableList<Ledger> doNamedQuery(String qName, HashMap<String, Object> args) {
        Query query = em.createNamedQuery(qName, Ledger.class);
        for (String key : args.keySet()) {
            query.setParameter(key, args.get(key));
        }
        List<Ledger> list = query.getResultList();
        ObservableList olist = FXCollections.observableList(list);
        return olist;
    }

    /**
     *
     * @param q
     * @return
     */
    public List getResults(String q) {
        Query query = em.createNativeQuery(q);
        List list = query.getResultList();
        return list;
    }

    /**
     *
     * @param idx
     * @return
     * @throws NoResultException
     */
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
    
    public Float getBalance(int accountNum) {
        return (getItem(getLastId(accountNum))).getTransBal();
    }

    public synchronized void rebalance(int acct, SearchCriteria criteria) {
        Query query = em.createNamedQuery("Account.findById");
        query.setParameter("id", acct);
        Account account = (Account) query.getSingleResult();
        query = em.createNamedQuery("Ledger.findRangeByDate");
        Calendar cal = Calendar.getInstance(Locale.getDefault());

        LocalDate ld1 = criteria.getDateRange()[0];
        LocalDate ld2 = criteria.getDateRange()[1];

        Date d1 = criteria.asDate(ld1);
        Date d2 = criteria.asDate(ld2);

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
                }
                if (account.getAccountType() == Account.AccountType.CHECKING) {
                    if (amt > 0) {
                        balance += Math.abs(amt);
                    } else {
                        balance -= Math.abs(amt);
                    }
                }
                l.setTransBal(balance);
                update(l);
            }
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
