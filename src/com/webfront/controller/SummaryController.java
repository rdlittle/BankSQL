/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.controller;

import com.webfront.bean.CategoryManager;
import com.webfront.bean.LedgerManager;
import com.webfront.model.Category;
import com.webfront.model.Ledger;
import com.webfront.view.SummaryView;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.chart.PieChart;

/**
 *
 * @author rlittle
 */
public class SummaryController {

    ObservableMap<Integer, PieChart.Data> pieChartHashMap;
    ObservableList dataList;
    private static SummaryController controller = null;
    private final CategoryManager catMgr;
    private final LedgerManager ledgerMgr;
    SummaryView view;

    private SummaryController() {
        pieChartHashMap = FXCollections.observableHashMap();
        view = new SummaryView();
        catMgr = new CategoryManager();
        ledgerMgr = new LedgerManager();
    }

    /**
     *
     * @return
     */
    public static SummaryController getInstance() {
        if (controller == null) {
            controller = new SummaryController();
        }
        return controller;
    }

    public void buildSummary() {
        String startDate = LocalDate.now().minusDays(365).format(DateTimeFormatter.ISO_DATE);
        String stmt = "SELECT * FROM categories WHERE parent = 0 ORDER BY description";
        ObservableList<Category> catList = catMgr.getCategories(stmt);
        
        stmt = "SELECT * FROM ledger where transDate >= \""+startDate+"\"";
        ObservableList<Ledger> ledgerList = ledgerMgr.doSqlQuery(stmt);
        for (Ledger l : ledgerList) {
            if (l.getPrimaryCat() != null) {
                Category cat = l.getPrimaryCat();
                Integer catId = cat.getId();
                String catDesc = cat.getDescription();
                float amt = l.getTransAmt();
                PieChart.Data data;
                if(!pieChartHashMap.containsKey(catId)) {
                    data = new PieChart.Data(cat.getDescription(),0);
                } else {
                    data = pieChartHashMap.get(catId);
                }
                if (data != null) {
                    if (amt != 0) {
                        amt += data.getPieValue();
                        data.setPieValue((double) amt);
                        pieChartHashMap.put(catId, data);
                    }
                }

            }
        }
        dataList = FXCollections.observableArrayList();
        dataList.addAll(pieChartHashMap.values());
        view.getChart().setData(dataList);
    }

    public SummaryView getView() {
        return view;
    }

}
