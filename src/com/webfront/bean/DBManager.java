/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.webfront.bean;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author rlittle
 */
public abstract class DBManager {
    public final EntityManagerFactory emf;

    /**
     *
     */
    public final EntityManager em;
    
    /**
     *
     */
    public DBManager() {
        emf=Persistence.createEntityManagerFactory("BankPU");
        em=emf.createEntityManager();
    }
    
    public abstract List<?> getList(String s);

}
