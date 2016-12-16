/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.bean;

import com.webfront.model.Xref;
import java.io.Serializable;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.persistence.Query;

/**
 *
 * @author rlittle
 */
public class XrefManager extends DBManager<Xref> implements Serializable {

    ObservableList<Xref> list;
    private static XrefManager instance = null;

    protected XrefManager() {
        instance = new XrefManager();
        instance.getList("");
    }

    public XrefManager getInstance() {
        if(instance == null) {
            instance = new XrefManager();
        }
        return instance;
    }
            
            public List<Xref> lookup(String n, Character t) {
        Query query = em.createNamedQuery("Xref.findXrefByName");
        query.setParameter("name", n);
        query.setParameter("type", t);
        List<Xref> xref = query.getResultList();
        return xref;
    }

    @Override
    public List<Xref> getList(String s) {
        Query query = em.createNamedQuery(s);
        list = FXCollections.observableList(query.getResultList());
        return list;
    }

    @Override
    public ObservableList<Xref> doSqlQuery(String q) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
