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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.SwingWorker;

public class LedgerManager extends DBManager implements Serializable {

    private List<Ledger> selectedItems;

    /**
     *
     */
    public LedgerManager() {
        selectedItems = new ArrayList<>();
    }

    @Override
    public ObservableList<Ledger> getList(String q) {
        ObservableList ledgerList=FXCollections.emptyObservableList();
        SwingWorker<List<Ledger>, Void> worker;
        worker = new SwingWorker<List<Ledger>, Void>() {
            @Override
            protected List<Ledger> doInBackground() {
                Query query = em.createNamedQuery("Ledger.findByAccountNum");
                query.setParameter("accountNum", Integer.parseInt(q));
                final List<Ledger> list = query.getResultList();
                return list;
            }

            @Override
            protected void done() {
            }
        };
        worker.execute();
        try {
            ledgerList = FXCollections.observableList(worker.get());
        } catch (InterruptedException ex) {
            Logger.getLogger(LedgerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(LedgerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ledgerList;
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

    /**
     *
     * @param qName
     * @return
     */
    public ObservableList<Ledger> doNamedQuery(String qName) {
        Query query = em.createNamedQuery(qName, Ledger.class);
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

    public void rebalance(int acct, SearchCriteria criteria) {
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
                } else {
                    balance -= amt;
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
