/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.bean;

import com.webfront.model.Receipts;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.annotation.PreDestroy;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

/**
 *
 * @author rlittle
 */
public class ReceiptsManager extends DBManager {

    @Override
    public ObservableList<Receipts> getList(String q) {
        Query query = em.createNamedQuery("Receipts.findAll");
        List<Receipts> list = query.getResultList();
        ObservableList olist = FXCollections.observableList(list);
        return olist;
    }

    public void create(Receipts receipt) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.persist(receipt);
        transaction.commit();
    }

    public void update(Receipts receipt) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.merge(receipt);
        transaction.commit();
    }

    public void delete(Receipts receipt) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.remove(receipt);
        transaction.commit();
    }

    @PreDestroy
    public void close() {
        if (em.isOpen()) {
            em.close();
        }
    }

}
