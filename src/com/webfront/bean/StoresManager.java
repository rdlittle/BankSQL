/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.webfront.bean;

import com.webfront.model.Stores;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.persistence.Query;

/**
 *
 * @author rlittle
 */
public class StoresManager extends DBManager {
    
    private final ObservableList<Stores> list;
    private SimpleBooleanProperty changed;
    
    public StoresManager() {
        list = FXCollections.<Stores>observableArrayList();
        changed = new SimpleBooleanProperty(false);
        changed.bind(super.isChanged);
        changed.addListener(new ListChange());
    }
    
    /**
     *
     * @param q The NamedQuery to execute
     * @return ObservableList of Store objects
     */
    @Override
    public synchronized ObservableList<Stores> getList(String q) {
        Query query=em.createNativeQuery(q,Stores.class);
        List<Stores> storeList=query.getResultList();
        list.setAll(storeList);
        ObservableList olist=FXCollections.observableList(storeList);
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
    
    @Override
    public ObservableList doSqlQuery(String q) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private class ListChange implements InvalidationListener {

        @Override
        public void invalidated(Observable observable) {
            getList("SELECT * FROM stores ORDER BY storeName");
        }
        
    }

}
