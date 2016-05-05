/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.bean;

import java.util.List;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 *
 * @author rlittle
 * @param <T> Entity Object
 */
public abstract class DBManager<T> {

    public SimpleBooleanProperty isChanged;
    public final EntityManagerFactory emf;
    //T entity;
    /**
     *
     */
    public final EntityManager em;

    /**
     *
     */
    public DBManager() {
        isChanged = new SimpleBooleanProperty(false);
        emf = Persistence.createEntityManagerFactory("BankPU");
        em = emf.createEntityManager();
    }

    public abstract List<T> getList(String s);
    public abstract ObservableList<T> doSqlQuery(String q);

    /**
     *
     * @param obj an Entity to create
     */
    public synchronized void create(T obj) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.persist(obj);
        transaction.commit();
        isChanged.set(true);
    }

    public synchronized void update(T obj) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.merge(obj);
        transaction.commit();
        isChanged.set(true);
    }

    public synchronized void delete(T obj) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.remove(obj);
        transaction.commit();
        isChanged.set(true);
    }

    public synchronized void refresh(T obj) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.refresh(obj);
        transaction.commit();
    }
    
    @PreDestroy
    public void close() {
        if (em.isOpen()) {
            em.close();
        }
    }

}
