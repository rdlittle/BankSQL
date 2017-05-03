/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.bean;

import com.webfront.model.Category;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.util.StringConverter;
import javax.persistence.Query;
import javax.swing.SwingWorker;

/**
 *
 * @author rlittle
 */
public class CategoryManager extends DBManager<Category> {

    /**
     * @return the filteredCategoryList
     */
    public FilteredList<Category> getFilteredCategoryList() {
        return filteredCategoryList;
    }

    private ObservableList<Category> categories;
    private static CategoryManager instance = null;
    private final SortedList<Category> sortedCategories;
    private FilteredList<Category> filteredCategoryList;

    protected CategoryManager() {
        super();
        categories = FXCollections.emptyObservableList();
        sortedCategories = new SortedList<>(categories);
    }

    public synchronized static CategoryManager getInstance() {
        if (instance == null) {
            instance = new CategoryManager();
            instance.filteredCategoryList = new FilteredList<>(instance.getCategories("select * from categories where parent > 0"));
            instance.filteredCategoryList.setPredicate((e)->true);
        }
        return instance;
    }

    public List<Category> getTree() {
        Query query = em.createNamedQuery("Category.tree");
        List<Category> l = query.getResultList();
        return l;
    }

    public ObservableList<Category> getCategories() {
        if (categories.isEmpty()) {
//            Query query = em.createNativeQuery("Select * from categories c order by c.description", Category.class);
            Query query = em.createNamedQuery("Category.findAll");
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

    public HashMap<String, Integer> getMapByDescription() {
        HashMap<String, Integer> map = new HashMap<>();
        for (Category c : getCategories()) {
            map.put(c.getDescription(), c.getId());
        }
        return map;
    }

    public HashMap<String, Integer> getMapById() {
        HashMap<String, Integer> map = new HashMap<>();
        for (Category c : getCategories()) {
            map.put(c.getDescription(), c.getId());
        }
        return map;
    }

    public Category getCategory(int id) {
        Query query = em.createNamedQuery("Category.findById");
        query.setParameter("id", id);
        Category cat = (Category) query.getSingleResult();
        return cat;
    }

    public ObservableList<Category> getChildren(Integer parentId) {
        Query query = em.createNamedQuery("Category.findByParent");
        query.setParameter("parent", parentId);
        final List<Category> list = query.getResultList();
        return FXCollections.observableArrayList(list);
    }

    public boolean hasChildren(int id) {
        Query query = em.createNamedQuery("Category.findByParent");
        query.setParameter("parent", id);
        return (!query.getResultList().isEmpty());
    }

    /**
     * @param categories the categories to set
     */
    public void setCategories(ObservableList<Category> categories) {
        this.categories = categories;
    }

    @Override
    public List<Category> getList(String s) {
        SwingWorker<List<Category>, Void> worker;
        worker = new SwingWorker<List<Category>, Void>() {
            @Override
            protected List<Category> doInBackground() {
                Query query = em.createNamedQuery(s);
                final List<Category> list = query.getResultList();
                return list;
            }

            @Override
            protected void done() {
            }
        };
        worker.execute();
        try {
            categories = FXCollections.observableList(worker.get());
        } catch (InterruptedException ex) {
            Logger.getLogger(LedgerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(LedgerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return categories;
    }

    @Override
    public ObservableList<Category> doSqlQuery(String q) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void addListener(ListChangeListener<? super Category> listListener) {
        categories.addListener(listListener);
    }

    public void removeListener(ListChangeListener<? super Category> listListener) {
        categories.removeListener(listListener);
    }

    public void addListener(InvalidationListener listener) {
        categories.addListener(listener);
    }

    public void removeListener(InvalidationListener listener) {
        categories.removeListener(listener);
    }

    public static class CategoryConverter extends StringConverter {

        @Override
        public String toString(Object object) {
            Category target = (Category) object;
            for (Category c : CategoryManager.getInstance().categories) {
                if (target.getId().equals(c.getId())) {
                    return c.getDescription();
                }
            }
            return object.toString();
        }

        @Override
        public Object fromString(String string) {
            for (Category c : CategoryManager.getInstance().categories) {
                if (c.getDescription().equalsIgnoreCase(string)) {
                    return c;
                }
            }
            return null;
        }

    }

}
