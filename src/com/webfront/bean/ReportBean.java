/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.bean;

import com.webfront.model.Category;
import com.webfront.model.Ledger;
import com.webfront.model.LedgerItem;
import com.webfront.model.Payment;
import com.webfront.model.SearchCriteria;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.ObservableList;
import javax.persistence.Query;

/**
 *
 * @author rlittle
 */
public class ReportBean {

    ArrayList<LedgerItem> itemList;
    ArrayList<Payment> paymentList;
    ArrayList<Ledger> ledgerList;
    SearchCriteria searchCriteria;
    HashMap<String, Float> totals;

    public ReportBean() {
        itemList = new ArrayList<>();
        paymentList = new ArrayList<>();
        ledgerList = new ArrayList<>();
        searchCriteria = new SearchCriteria();
        totals = new HashMap<>();
    }

    public ReportBean(SearchCriteria sc) {
        this();
        searchCriteria = sc;
    }

    public void setCriteria(SearchCriteria sc) {
        searchCriteria = sc;
    }

    public void doSearch() {
        String stmt;
        Query query;
        Map<String, Object> map = new HashMap<>();
        if (searchCriteria.getAccountProperty().get() != null) {
            stmt = "Payment.findRangeByAccountNum";
            map.put("accountNum", searchCriteria.getAccountProperty().get().getId());
        } else {
            stmt = "Payment.findAllByDate";
        }
        map.put("startDate", searchCriteria.asDate(searchCriteria.getStartDateProperty().get()));
        map.put("endDate", searchCriteria.asDate(searchCriteria.getEndDateProperty().get()));
        paymentList.clear();
        paymentList.addAll(PaymentManager.getInstance().doNamedQuery(stmt, map));

        if (searchCriteria.getAccountProperty().get() != null) {
            stmt = "Ledger.findRangeByAccountNum";
        } else {
            stmt = "Ledger.findRangeByDate";
        }
        ledgerList.clear();
        ledgerList.addAll(LedgerManager.getInstance().doNamedQuery(stmt, map));

        for (Payment p : paymentList) {
            if (p.getLedgerEntry() != null) {
                if (ledgerList.contains(p.getLedgerEntry())) {
                    ledgerList.remove(p.getLedgerEntry());
                }
            }
            float amt = Math.abs(p.getTransAmt());
            float bal = 0F;
            String cat1 = "Unassigned";
            String cat2 = "Unassigned";
            if (p.getPrimaryCat() != null) {
                cat1 = p.getPrimaryCat().getId().toString();
            }
            if (p.getSubCat() != null) {
                cat2 = p.getSubCat().getId().toString();
            }
            
            if (totals.containsKey(cat1)) {
                bal = totals.get(cat1);
            }
            bal += amt;
            totals.put(cat1, bal);
            
            bal = 0;
            if (totals.containsKey(cat2)) {
                bal = totals.get(cat2);
            }
            bal += amt;
            totals.put(cat2, bal);
        }

        for (Ledger l : ledgerList) {
            String cat1 = "Unassigned";
            String cat2 = "Unassigned";
            float amt = Math.abs(l.getTransAmt());
            float bal = 0F;
            if (l.getPrimaryCat() != null) {
                cat1 = l.getPrimaryCat().getId().toString();
            }
            if (l.getSubCat() != null) {
                cat2 = l.getSubCat().getId().toString();
            }
            if (totals.containsKey(cat1)) {
                bal = totals.get(cat1);
            }
            bal += amt;
            totals.put(cat1, bal);
            
            bal = 0;
            if (totals.containsKey(cat2)) {
                bal = totals.get(cat2);
            }
            bal += amt;
            totals.put(cat2, bal);
        }

        paymentList.clear();
        ledgerList.clear();
        ArrayList<Category> catList = new ArrayList<>();
        catList.addAll(CategoryManager.getInstance().getCategories());
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMinimumFractionDigits(2);
        for (String k : totals.keySet()) {
            String desc = k;
            Float amt = totals.get(k);
            for (Category c : catList) {
                String catId = c.getId().toString();
                if (k.equals(catId)) {
                    desc = c.getDescription();
                    break;
                }
            }
            System.out.print(k + " " + desc + " ");
            System.out.println(format.format(amt));
        }

    }

}
