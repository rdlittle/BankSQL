/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.bean;

import com.webfront.model.Stores;
import java.util.HashMap;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;
import javax.persistence.Query;

/**
 *
 * @author rlittle
 */
public class StoresManager extends DBManager {

    private static StoresManager instance;

    private final ObservableList<Stores> list;
    private final SimpleBooleanProperty changed;
    private final HashMap<String, Stores> storesMap;

    protected StoresManager() {
        list = FXCollections.<Stores>observableArrayList();
        changed = new SimpleBooleanProperty(false);
        changed.bind(super.isChanged);
        changed.addListener(new ListChange());
        storesMap = new HashMap<>();
    }

    public static StoresManager getInstance() {
        if (instance == null) {
            instance = new StoresManager();
        }
        return instance;
    }

    /**
     *
     * @param q The NamedQuery to execute
     * @return ObservableList of Store objects
     */
    @Override
    public synchronized ObservableList<Stores> getList(String q) {
        Query query = em.createNamedQuery("Stores.findAll");
        List<Stores> storeList = query.getResultList();
        list.setAll(storeList);
        list.sort(Stores.storeComparator);
        storeList.stream().forEach((s) -> {
            storesMap.put(s.getStoreName(), s);
        });
        ObservableList olist = FXCollections.observableList(storeList);
        return olist;
    }
    
    public synchronized void create(Stores s) {
        super.create(s);
        Query query = em.createNamedQuery("Stores.findByStoreName");
        query.setParameter("storeName", s.getStoreName());
        Stores store = (Stores) query.getSingleResult();
        list.add(store);
    }
    
    public synchronized ObservableList<Stores> getStoreList() {
        if(list.isEmpty()) {
            getList("");
        }
        return list;
    }

    public Stores getItem(Stores s) {
        String sql = "SELECT * FROM stores WHERE ";
        if (s != null) {
            if (s.getId() == null) {
                if (s.getStoreName() != null) {
                    sql += "storeName = \"" + s.getStoreName() + "\"";
                }
            } else {
                sql += "id = " + s.getId();
            }
            Query query = em.createNativeQuery(sql, Stores.class);
            Stores store = (Stores) query.getSingleResult();
            return store;
        }
        return null;
    }
    
    public HashMap<String,Stores> getStoresMap() {
        return storesMap;
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
    
    public static class StoreConverter extends StringConverter {

        @Override
        public String toString(Object object) {
            Stores s = (Stores) object;
            return s.getStoreName();
        }

        @Override
        public Object fromString(String string) {
            return getInstance().getStoresMap().get(string);
        }
        
    }
    
}
