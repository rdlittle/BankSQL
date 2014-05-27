/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.bean;

import com.webfront.model.Category;
import java.util.List;
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
        emf = Persistence.createEntityManagerFactory("BankPU");
        em = emf.createEntityManager();
    }

    public abstract List<T> getList(String s);

    /**
     *
     * @param obj an Entity to create
     */
    public void create(T obj) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.persist(obj);
        transaction.commit();
    }

    public void update(T obj) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.merge(obj);
        transaction.commit();
    }

    public void delete(T obj) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.remove(obj);
        transaction.commit();
    }

    @PreDestroy
    public void close() {
        if (em.isOpen()) {
            em.close();
        }
    }

}
