/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.controller;

import com.webfront.bean.CategoryManager;
import com.webfront.bean.LedgerManager;
import com.webfront.model.Category;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

/**
 *
 * @author rlittle
 */
public class SummaryController {

    private final ObservableList<PieChart.Data> dataList;
    private HashMap<String, Integer> catMap;
    private static SummaryController instance = null;
    private final LedgerManager ledgerMgr;
    private String startDate;
    private String endDate;
    private final ArrayList<Category> selectedCategory;

    protected SummaryController() {
        ledgerMgr = LedgerManager.getInstance();
        dataList = FXCollections.<PieChart.Data>observableArrayList();
        catMap = CategoryManager.getInstance().getMapByDescription();
        startDate = LocalDate.now().minusDays(365).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        endDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        selectedCategory = new ArrayList<>();
    }
    
    public synchronized static SummaryController getInstance() {
        if(instance==null) {
            instance = new SummaryController();
        }
        return instance;
    }

    public void buildSummary(int category) {
        String sDate = LocalDate.now().minusDays(365).format(DateTimeFormatter.ISO_DATE);
        String stmt = "SELECT FORMAT(ABS(SUM(l.transAmt)),2), c.description FROM ledger l ";
        stmt += "join categories c on l.primaryCat = c.id ";
        stmt += "where l.transDate > \"" + sDate + "\" group by l.primaryCat WITH ROLLUP";
        List<Object[]> list = ledgerMgr.getResults(stmt);
        dataList.clear();
        class Item {
            Float amt;
            String label;
        }
        Iterator<Object[]> li = list.iterator();
        while (li.hasNext()) {
            Object[] obj = li.next();
            Item item = new Item();
            String s = (String) obj[0];
            item.amt = Float.parseFloat(s.replaceAll(",", ""));
            item.label = (String) obj[1];
            PieChart.Data data = new PieChart.Data(item.label, item.amt);
            dataList.add(data);
        }
    }

    public ObservableList<PieChart.Data> getSubCat(int category) {
        final ObservableList<PieChart.Data> list = FXCollections.<PieChart.Data>observableArrayList();
        String sDate = LocalDate.now().minusDays(365).format(DateTimeFormatter.ISO_DATE);
        String stmt;
        Category cat = CategoryManager.getInstance().getCategory(category);
        stmt = "SELECT c.description Category,format(ABS(SUM(l.transAmt)),2) Amount FROM ledger l ";
        stmt += "INNER JOIN distribution d ON d.transId = l.id ";
        stmt += "INNER JOIN categories c ON d.categoryId = c.id ";
        stmt += "WHERE l.transDate > \"" + sDate + "\" ";
        stmt += "AND c.parent = " + category + " ";
        stmt += "GROUP BY c.id ";
        stmt += "ORDER BY c.description";
        List<Object[]> objList = ledgerMgr.getResults(stmt);
        class Item {
            Float amt;
            String label;
        }
        Iterator<Object[]> li = objList.iterator();
        while (li.hasNext()) {
            Object[] obj = li.next();
            Item item = new Item();
            String s = (String) obj[1];
            item.amt = Float.parseFloat(s.replaceAll(",", ""));
            item.label = (String) obj[0];
            PieChart.Data data = new PieChart.Data(item.label, item.amt);
            list.add(data);
        }
        return list;
    }

    public ObservableList<PieChart.Data> getPayments(int subCategory) {
        ObservableList<PieChart.Data> list = FXCollections.observableArrayList();
        String sDate = LocalDate.now().minusDays(365).format(DateTimeFormatter.ISO_DATE);
        ObservableList<Category> catList = CategoryManager.getInstance().getCategories();
        for(Category c : catList) {
            if(c.getId()==subCategory) {
                if(c.getParent()>0) {
                    return getDetail(subCategory);
                }
            }
        }
        String stmt;
        stmt = "SELECT c.description Category,format(ABS(p.transAmt),2) Amount FROM categories c ";
        stmt += "JOIN payment p ON p.subCat = c.id ";
        stmt += "WHERE p.transDate > \"" + sDate + "\" ";
        stmt += "AND p.primaryCat = " + subCategory + " ";
        stmt += "GROUP BY c.description ";
        stmt += "ORDER BY c.description";
        List<Object[]> objList = ledgerMgr.getResults(stmt);
        class Item {
            Float amt;
            String label;
        }
        Iterator<Object[]> li = objList.iterator();
        while (li.hasNext()) {
            Object[] obj = li.next();
            Item item = new Item();
            String s = (String) obj[1];
            item.amt = Float.parseFloat(s.replaceAll(",", ""));
            item.label = (String) obj[0];
            PieChart.Data data = new PieChart.Data(item.label, item.amt);
            list.add(data);
        }
        return list;
    }
    
    public ObservableList<PieChart.Data> getDetail(int subCategory) {
        ObservableList<PieChart.Data> list = FXCollections.observableArrayList();
        String sDate = LocalDate.now().minusDays(365).format(DateTimeFormatter.ISO_DATE);
        String stmt;
        stmt = "SELECT p.transDesc, format(ABS(p.transAmt),2) ";
        stmt += "FROM payment p ";
        stmt += "WHERE p.primaryCat = "+subCategory+" ";
        stmt += "AND p.transDate > \"" + sDate + "\" ";
        stmt += "GROUP BY p.transDesc";
        List<Object[]> objList = ledgerMgr.getResults(stmt);
        class Item {
            Float amt;
            String label;
        }
        Iterator<Object[]> li = objList.iterator();
        while (li.hasNext()) {
            Object[] obj = li.next();
            Item item = new Item();
            String s = (String) obj[1];
            item.amt = Float.parseFloat(s.replaceAll(",", ""));
            item.label = (String) obj[0];
            PieChart.Data data = new PieChart.Data(item.label, item.amt);
            list.add(data);
        }
        return list;
    }
    
    public synchronized void addListener(ListChangeListener<PieChart.Data> listener) {
        dataList.addListener(listener);
    }
    
    public synchronized void removeListener(ListChangeListener<PieChart.Data> listener) {
        dataList.removeListener(listener);
    }
    
    public synchronized void addListener(InvalidationListener listener) {
        dataList.addListener(listener);
    }
    
    public synchronized void removeListener(InvalidationListener listener) {
        dataList.removeListener(listener);
    }    
    
    public boolean hasChildren(int id) {
        return CategoryManager.getInstance().hasChildren(id);
    }

    /**
     * @return the dataList
     */
    public ObservableList<PieChart.Data> getDataList() {
        return dataList;
    }

    /**
     * @param dataList the dataList to set
     */
    public void setDataList(ObservableList<PieChart.Data> dataList) {
        this.dataList.setAll(dataList);
    }

    /**
     * @return the catMap
     */
    public HashMap<String, Integer> getCatMap() {
        return catMap;
    }

    /**
     * @param catMap the catMap to set
     */
    public void setCatMap(HashMap<String, Integer> catMap) {
        this.catMap = catMap;
    }

    /**
     * @return the startDate
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

}
