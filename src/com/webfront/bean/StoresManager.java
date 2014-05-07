/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.webfront.bean;

import com.webfront.model.Stores;
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
public class StoresManager extends DBManager {
    
    public ObservableList<Stores> getList(String q) {
        Query query=em.createNativeQuery(q,Stores.class);
        List<Stores> list=query.getResultList();
        ObservableList olist=FXCollections.observableList(list);
        return olist;
    }
    
    public Stores getItem(Stores s) {
        String sql = "SELECT * FROM stores WHERE ";
        if(s != null) {
            if(s.getId()==null) {
                if(s.getStoreName()!=null) {
                    sql += "storeName = \""+s.getStoreName()+"\"";
                }
            } else {
                sql += "id = "+s.getId();
            }
            Query query = em.createNativeQuery(sql, Stores.class);
            Stores store=(Stores)query.getSingleResult();
            return store;
        }
        return null;
    }
    
    public void create(Stores store) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.persist(store);
        transaction.commit();
    } 
    
    public void update(Stores store) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.merge(store);
        transaction.commit();
    }
    
    public void delete(Stores store) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.remove(store);
        transaction.commit();        
    }
    
    @PreDestroy
    public void close() {
        if(em.isOpen()) {
            em.close();
        }
    }
}
