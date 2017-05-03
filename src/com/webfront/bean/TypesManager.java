/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.bean;

import com.webfront.model.Types;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.persistence.Query;

/**
 *
 * @author rlittle
 */
public class TypesManager extends DBManager {
    
    private static TypesManager instance;
    private final ObservableList<Types> list = FXCollections.<Types>observableArrayList();

    protected TypesManager() {
        
    }
    
    public static TypesManager getInstance() {
        if (instance==null) {
            instance = new TypesManager();
        }
        return instance;
    }
    
    @Override
    public ObservableList<Types> getList(String s) {
        Query query = em.createNamedQuery("Types.findAll");
        list.clear();
        list.setAll(query.getResultList());
        return list;
    }

    @Override
    public ObservableList doSqlQuery(String q) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
