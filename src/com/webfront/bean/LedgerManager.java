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
import javax.annotation.PreDestroy;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

public class LedgerManager extends DBManager implements Serializable {

    public LedgerManager() {
    }

    @Override
    public ObservableList<Ledger> getList(String q) {
        Query query = em.createNamedQuery("Ledger.findAll");
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

    public Ledger getItem(int idx) {
        Query query = em.createNamedQuery("Ledger.findById");
        query.setParameter("id", idx);
        Ledger ledger = (Ledger) query.getSingleResult();
        return ledger;
    }

    public void save(Ledger ledger) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.persist(ledger);
        transaction.commit();
    }

    @PreDestroy
    public void close() {
        if (em.isOpen()) {
            em.close();
        }
    }

}
