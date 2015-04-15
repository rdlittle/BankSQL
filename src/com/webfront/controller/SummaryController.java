/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.controller;

import com.webfront.bean.LedgerManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private static SummaryController controller = null;
    private final LedgerManager ledgerMgr;

    public SummaryController() {
        ledgerMgr = new LedgerManager();
        dataList = FXCollections.observableArrayList();
    }

    public void buildSummary() {
        String startDate = LocalDate.now().minusDays(365).format(DateTimeFormatter.ISO_DATE);
        String stmt = "SELECT * FROM categories WHERE parent = 0 ORDER BY description";
        stmt = "SELECT FORMAT(ABS(l.transAmt),2), c.description FROM ledger l ";
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

}
