/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.bean;

import com.webfront.model.Ledger;
import com.webfront.model.LedgerItem;
import com.webfront.model.Payment;
import com.webfront.model.SearchCriteria;
import java.util.ArrayList;

/**
 *
 * @author rlittle
 */
public class ReportBean {

    ArrayList<LedgerItem> itemList;
    ArrayList<Payment> paymentList;
    ArrayList<Ledger> ledgerList;
    SearchCriteria searchCriteria;

    public ReportBean() {
        itemList = new ArrayList<>();
        paymentList = new ArrayList<>();
        ledgerList = new ArrayList<>();
        searchCriteria = new SearchCriteria();
    }

}
