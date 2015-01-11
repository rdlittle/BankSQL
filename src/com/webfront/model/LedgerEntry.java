/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.model;

import com.webfront.app.utils.DateConvertor;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rlittle
 */
public class LedgerEntry {

    private String date;
    private String description;
    private String refNumber;
    private String amount;
    private float balance;
    private String dateFormat;

    public LedgerEntry() {
        date = "";
        description = "";
        refNumber = "";
        amount = "";
        balance = 0;
        dateFormat = "MM/dd/yyyy";
    }

    public LedgerEntry(String d, String desc, String amt) {
        date = d;
        description = desc;
        amount = amt;
        dateFormat = "MM/dd/yyyy";
        balance = 0;
    }

    public static Comparator<LedgerEntry> LedgerComparator = new Comparator<LedgerEntry>() {
        @Override
        public int compare(LedgerEntry ledger1, LedgerEntry ledger2) {
            Long d1 = ledger1.getDateValue();
            Long d2 = ledger2.getDateValue();
            return d1.compareTo(d2);
        }
  
    };

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    public Long getDateValue() {
        try {
            return DateConvertor.toLong(date,dateFormat);
        } catch (ParseException ex) {
            Logger.getLogger(LedgerEntry.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        int currYear = cal.get(Calendar.YEAR);
        int currMonth = cal.get(Calendar.MONTH)+1;
        date = date.replaceAll("-", "/");
        if(date.matches("\\d{1,2}(/|-)\\d{1,2}")) {
            dateFormat = "MM/dd/yyyy";
            int dateMonth = Integer.parseInt(date.substring(0,date.indexOf("/")));
            if(dateMonth>currMonth) {
                date += date.indexOf("-") > 0 ? "-" : "/"+Integer.toString(currYear-1);
            }
        }
        this.date = date;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the refNumber
     */
    public String getRefNumber() {
        return refNumber;
    }

    /**
     * @param refNumber the refNumber to set
     */
    public void setRefNumber(String refNumber) {
        this.refNumber = refNumber;
    }

    /**
     * @return the amount
     */
    public String getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(String amount) {
        this.amount = amount.replaceAll(",", "");
    }

    /**
     * @return the balance
     */
    public float getBalance() {
        return balance;
    }

    /**
     * @param balance the balance to set
     */
    public void setBalance(float balance) {
        this.balance = balance;
    }
}
