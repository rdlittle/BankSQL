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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
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
    ObservableMap<Integer, String> index;

    public ReportBean() {
        itemList = new ArrayList<>();
        paymentList = new ArrayList<>();
        ledgerList = new ArrayList<>();
        searchCriteria = new SearchCriteria();
        totals = new HashMap<>();
        index = FXCollections.<Integer, String>observableHashMap();
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
        /*
        select 
        c2.type "Code" ,
        t.name "Type" ,
        x.cat2 "GL Group",
        x.name "Group Desc",
        x2.cat2 "GL Acct",
        c2.description "Acct Description", 
        lpad(format(sum(p.transAmt),2),9,' ') "Total"
        from payment p 
        join xref x on x.cat1 = p.primaryCat 
        join xref x2 on x2.cat1 = p.subCat 
        join categories c1 on c1.id = p.primaryCat 
        join categories c2 on c2.id = p.subCat 
        join types t on t.code = c2.type 
        where p.transDate between "2016-01-01" and "2016-12-31"  group by x2.cat2;
         */
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
            itemList.add(new LedgerItem(p));
            if (p.getLedgerEntry() != null) {
                if (ledgerList.contains(p.getLedgerEntry())) {
                    ledgerList.remove(p.getLedgerEntry());
                }
            }

        }

        for (Ledger l : ledgerList) {
            itemList.add(new LedgerItem(l));
        }

        paymentList.clear();
        ledgerList.clear();

        ArrayList<Comparator<LedgerItem>> compList = new ArrayList<>();
        compList.add(TypeComparator);
        compList.add(Cat1Comparator);
        compList.add(Cat2Comparator);
        MultiComparator<LedgerItem> comparator = new MultiComparator<>(compList);
        itemList.sort(comparator);
        Integer ptr = 0;

        for (LedgerItem li : itemList) {
            float amt = Math.abs(Float.parseFloat(li.getAmount()));
            float bal = 0F;

            String cat1 = Integer.toString(li.getPrimaryCat());
            String cat2 = Integer.toString(li.getSubCat());

            if (totals.containsKey(cat1)) {
                bal = totals.get(cat1);
            } else {
                ptr++;
            }
            bal += amt;
            totals.put(cat1, bal);

            bal = 0;
            if (totals.containsKey(cat2)) {
                bal = totals.get(cat2);
            } else {
                ptr++;
            }
            bal += amt;
            totals.put(cat2, bal);
            index.put(ptr, cat2);
        }

        ObservableList<Integer> idx = FXCollections.observableArrayList(index.keySet());
        idx.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return (o1.compareTo(o2));
            }
        });

        ArrayList<Category> catList = new ArrayList<>();
        catList.addAll(CategoryManager.getInstance().getCategories());
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMinimumFractionDigits(2);

        for (Integer seq : idx) {
            String k = index.get(seq);
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

    private static Comparator<LedgerItem> Cat1Comparator = new Comparator<LedgerItem>() {
        @Override
        public int compare(LedgerItem item1, LedgerItem item2) {
            Integer c1 = item1.getPrimaryCat();
            Integer c2 = item2.getPrimaryCat();
            return c1.compareTo(c2);
        }
    };

    private static Comparator<LedgerItem> Cat2Comparator = new Comparator<LedgerItem>() {
        @Override
        public int compare(LedgerItem item1, LedgerItem item2) {
            Integer c1 = item1.getSubCat();
            Integer c2 = item2.getSubCat();
            return c1.compareTo(c2);
        }
    };

    private static Comparator<LedgerItem> TypeComparator = new Comparator<LedgerItem>() {
        @Override
        public int compare(LedgerItem item1, LedgerItem item2) {
            return Character.compare(item1.getTransType(), item2.getTransType());
        }
    };

    public class MultiComparator<T> implements Comparator<T> {

        private final List<Comparator<T>> comparators;

        public MultiComparator(List<Comparator<T>> comparators) {
            this.comparators = comparators;
        }

        @Override
        public int compare(T o1, T o2) {
            for (Comparator<T> comparator : comparators) {
                int comparison = comparator.compare(o1, o2);
                if (comparison != 0) {
                    return comparison;
                }
            }
            return 0;
        }
    }

}
