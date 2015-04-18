/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.controller;

import com.webfront.bean.CategoryManager;
import com.webfront.bean.LedgerManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

/**
 *
 * @author rlittle
 */
public class SummaryController {

    private ObservableList<PieChart.Data> dataList;
    private HashMap<String,Integer> catMap;
    private static SummaryController controller = null;
    private final LedgerManager ledgerMgr;

    public SummaryController() {
        ledgerMgr = new LedgerManager();
        dataList = FXCollections.observableArrayList();
        catMap = CategoryManager.getInstance().getMapByDescription();
    }

    public void buildSummary(int category) {
        String startDate = LocalDate.now().minusDays(365).format(DateTimeFormatter.ISO_DATE);
        //String stmt = "SELECT * FROM categories WHERE parent = 0 ORDER BY description";
        String stmt = "SELECT FORMAT(ABS(l.transAmt),2), c.description FROM ledger l ";
        stmt += "inner join categories c on l.primaryCat = c.id ";
        stmt += "where l.transDate > \"" + startDate + "\" group by l.primaryCat";
        List<Object[]> list = ledgerMgr.getResults(stmt);
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
            getDataList().add(data);
        }
    }
    
    public ObservableList<PieChart.Data> getSubCat(int category) {
        ObservableList<PieChart.Data> list = FXCollections.observableArrayList();
        String startDate = LocalDate.now().minusDays(365).format(DateTimeFormatter.ISO_DATE);
        String stmt;
        stmt = "SELECT c.description Category,format(ABS(l.transAmt),2) Amount FROM ledger l ";
        stmt += "INNER JOIN distribution d ON d.transId = l.id ";
        stmt += "INNER JOIN categories c ON d.categoryId = c.id ";
        stmt += "WHERE l.transDate > \"" + startDate + "\" " ;
        stmt += "AND c.parent = "+category+" ";
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
            item.amt = Float.parseFloat(s);
            item.label = (String) obj[0];
            PieChart.Data data = new PieChart.Data(item.label, item.amt);
            list.add(data);
        }
        return list;
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
        this.dataList = dataList;
    }

    /**
     * @return the catMap
     */
    public HashMap<String,Integer> getCatMap() {
        return catMap;
    }

    /**
     * @param catMap the catMap to set
     */
    public void setCatMap(HashMap<String,Integer> catMap) {
        this.catMap = catMap;
    }

}
