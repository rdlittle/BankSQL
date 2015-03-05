/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.bean;

import com.webfront.model.Category;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.persistence.Query;

/**
 *
 * @author rlittle
 */
public class CategoryManager extends DBManager<Category> {

    private ObservableList<Category> categories;

    public CategoryManager() {
        super();
        categories = FXCollections.emptyObservableList();
    }

    public ObservableList<Category> getCategories() {
        if (categories.isEmpty()) {
            Query query = em.createNativeQuery("Select * from categories c order by c.description", Category.class);
            List<Category> list = query.getResultList();
            categories = (ObservableList<Category>) FXCollections.observableList(list);
        }
        return categories;
    }

    public ObservableList<Category> getCategories(String stmt) {
        Query query = em.createNativeQuery(stmt, Category.class);
        List<Category> list = query.getResultList();
        return FXCollections.observableList(list);
    }

    /**
     * @param categories the categories to set
     */
    public void setCategories(ObservableList<Category> categories) {
        this.categories = categories;
    }

    @Override
    public List<Category> getList(String s) {
        Query query = em.createNamedQuery(s);
        List<Category> list = query.getResultList();
        categories = (ObservableList<Category>) FXCollections.observableList(list);
        return categories;
    }

    @Override
    public ObservableList<Category> doSqlQuery(String q) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
