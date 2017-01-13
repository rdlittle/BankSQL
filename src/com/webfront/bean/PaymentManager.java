/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.bean;

import com.webfront.model.Payment;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

/**
 *
 * @author rlittle
 */
public class PaymentManager extends DBManager<Payment> implements Serializable {

    private static PaymentManager instance = null;
    private final ObservableList<Payment> list;
    private Payment selectedPayment;

    protected PaymentManager() {
        this.list = FXCollections.<Payment>observableArrayList();
    }

    public static synchronized PaymentManager getInstance() {
        if (instance == null) {
            instance = new PaymentManager();
        }
        return instance;
    }

    @Override
    public ObservableList<Payment> getList(String q) {
        if (list.isEmpty()) {
            refresh();
        }
        return list;
    }

    public void refresh() {
        Query query = em.createNamedQuery("Payment.findAll");
        List<Payment> l = query.getResultList();
        list.setAll(l);
    }

    @Override
    public void create(Payment p) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.persist(p);
        transaction.commit();
        selectedPayment = p;
        list.add(p);
    }

    @Override
    public synchronized ObservableList<Payment> doSqlQuery(String q) {
        if (q.isEmpty()) {
            return FXCollections.observableArrayList();
        }
        Query query = em.createNativeQuery(q);
        List<Payment> l = query.getResultList();
        return FXCollections.observableArrayList(l);
    }

    public synchronized List<Payment> getPayments(String s) {
        Query query = em.createNativeQuery(s);
        List<Payment> l = query.getResultList();
        return l;
    }

    public synchronized ObservableList<Payment> doNamedQuery(String nq) {
        Query query = em.createNamedQuery(nq);
        List<Payment> l = query.getResultList();
        return FXCollections.observableArrayList(l);
    }

    public synchronized ObservableList<Payment> doNamedQuery(String nq, Map<String, Object> map) {
        Query query = em.createNamedQuery(nq);
        query.setParameter("acctNum", map.get("accountNum"));
        query.setParameter("startDate", map.get("startDate"));
        query.setParameter("endDate", map.get("endDate"));
        List<Payment> l = query.getResultList();
        return FXCollections.observableArrayList(l);
    }

    public synchronized void addListener(ListChangeListener lcl) {
        list.addListener(lcl);
    }

    public synchronized void removeListener(ListChangeListener lcl) {
        list.removeListener(lcl);
    }

    public synchronized void addListener(InvalidationListener il) {
        list.addListener(il);
    }

    public synchronized void removeListener(InvalidationListener il) {
        list.removeListener(il);
    }

    /**
     * @return the selectedPayment
     */
    public Payment getSelectedPayment() {
        return selectedPayment;
    }

    /**
     * @param selectedPayment the selectedPayment to set
     */
    public void setSelectedPayment(Payment selectedPayment) {
        this.selectedPayment = selectedPayment;
    }

}
