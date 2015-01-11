/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.bean;

import com.webfront.model.Ledger;
import java.io.Serializable;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.persistence.Query;

public class LedgerManager extends DBManager implements Serializable {

    public LedgerManager() {
    }

    @Override
    public ObservableList<Ledger> getList(String q) {
        Query query = em.createNamedQuery("Ledger.findByAccountNum");
        query.setParameter("accountNum", Integer.parseInt(q));
        List<Ledger> list = query.getResultList();
        ObservableList olist = FXCollections.observableList(list);
        return olist;
    }
    
    public ObservableList<Ledger> doSqlQuery(String q) {
        Query query = em.createNativeQuery(q,Ledger.class);
        List<Ledger> list = query.getResultList();
        ObservableList olist = FXCollections.observableList(list);
        return olist;
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
     * @return most recent entry in the ledger
     */
    public int getLastId() {
        Query query=em.createNativeQuery("SELECT id FROM ledger ORDER BY id DESC LIMIT 0,1");
        List<Integer> result=query.getResultList();
        return result.get(0);
    }

}
