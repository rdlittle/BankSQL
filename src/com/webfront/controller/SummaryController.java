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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.chart.PieChart;

/**
 *
 * @author rlittle
 */
public class SummaryController {

    ObservableMap<Integer, PieChart.Data> pieChartData;
    ObservableList dataList;
    private static SummaryController controller = null;
    private final CategoryManager catMgr;
    private final LedgerManager ledgerMgr;
    SummaryView view;

    private SummaryController() {
        pieChartData = FXCollections.observableHashMap();
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
        String stmt = "SELECT * FROM categories WHERE parent = 0 ORDER BY description";
        ObservableList<Category> catList = catMgr.getCategories(stmt);
        for (Category c : catList) {
            if (c != null) {
                pieChartData.put(c.getId(), new PieChart.Data(c.getDescription(), 0));
            }
        }
        stmt = "SELECT * FROM LEDGER ";
        ObservableList<Ledger> ledgerList = ledgerMgr.getList(stmt);
        for (Ledger l : ledgerList) {
            if (l.getPrimaryCat() != null) {
                int idx = l.getPrimaryCat().getId();
                PieChart.Data data;
                if (!pieChartData.containsKey(idx)) {
                    idx = 21;
                }
                data = pieChartData.get(idx);
                float amt = l.getTransAmt();
                if (data != null) {
                    if (amt != 0) {
                        amt += data.getPieValue();
                        data.setPieValue((double) amt);
                        pieChartData.put(idx, data);
                    }
                }

            }
        }
        dataList = FXCollections.observableArrayList();
        dataList.addAll(pieChartData.values());
        view.getChart().setData(dataList);
    }

    public SummaryView getView() {
        return view;
    }

}
