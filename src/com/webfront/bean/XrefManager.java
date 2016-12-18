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

    }

    public static XrefManager getInstance() {
        if (instance == null) {
            instance = new XrefManager();
            instance.getList("Xref.findAll");
        }
        return instance;
    }

    public Xref lookup(String n, Character t) {
        for (Xref target : list) {
            if (target.getName().equalsIgnoreCase(n) && target.getType().compareTo(t) == 0) {
                if (target.getCat1() != 0) {
                    return target;
                }
            }
        }
        return null;
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
