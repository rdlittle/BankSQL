/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.app.utils;

import com.webfront.model.Ledger;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rlittle
 */
public final class Importer implements Runnable {

    static BufferedReader in;
    ResourceBundle config;
    static final String configName = "com.webfront.app.bank";
    boolean headerDone;
    boolean depositsDone;
    boolean withdrawalsDone;
    boolean checksDone;
    boolean feesDone;
    public String startDate;
    public String endDate;
    public Float beginningBalance;
    public Float endingBalance;
    public Float totalDeposits;
    public Float totalWithdrawals;
    public Float totalChecks;
    public Float totalFees;
    private ArrayList<Ledger> itemList;

    private String fileName;

    public Importer(String fileName) {
        this.fileName = fileName;
        this.startDate = "";
        this.endDate = "";
        this.beginningBalance = new Float(0);
        this.endingBalance = new Float(0);
        this.totalDeposits = new Float(0);
        this.totalWithdrawals = new Float(0);
        this.totalChecks = new Float(0);
        this.totalFees = new Float(0);
        this.itemList = new ArrayList<>();
    }

    public BufferedReader openFile(String fileName) {
        BufferedReader inFile = null;
        try {
            inFile = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Importer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return inFile;
    }

    public void doImport(BufferedReader reader) throws IOException, ParseException {
        String text;
        String line;
        String section = "";

        headerDone = false;
        depositsDone = false;
        withdrawalsDone = false;
        checksDone = false;
        feesDone = false;

        while ((text = reader.readLine()) != null) {
            line = text.trim();
            if (!headerDone) {
                if (line.startsWith((String) config.getString("beginningSummary"))) {
                    section = (String) config.getString("beginningSummary");
                    text = (line.substring(section.length(), line.length())).trim();
                    int amtPtr = text.indexOf('$');
                    String amt = text.substring(amtPtr + 1, text.length());
                    amt = amt.replaceAll("\\,", "");
                    String date = text.substring(0, amtPtr - 1);
                    text = date.trim();
                    try {
                        startDate = DateConvertor.convert(text);
                    } catch (ParseException ex) {
                        Logger.getLogger(Importer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    beginningBalance = Float.parseFloat(amt);
                }
                if (line.startsWith((String) config.getString("depositsSummary"))) {
                    section = (String) config.getString("depositsSummary");
                    text = (line.substring(section.length(), line.length())).trim();
                    text = text.replaceAll("\\,", "");
                    totalDeposits = Float.valueOf(text);
                }
                if (line.startsWith((String) config.getString("withdrawalsSummary"))) {
                    section = (String) config.getString("withdrawalsSummary");
                    text = (line.substring(section.length(), line.length())).trim();
                    text = text.replaceAll("\\,", "");
                    totalWithdrawals = Float.valueOf(text);
                }
                if (line.startsWith((String) config.getString("checksSummary"))) {
                    section = (String) config.getString("checksSummary");
                    text = (line.substring(section.length(), line.length())).trim();
                    text = text.replaceAll("\\,", "");
                    totalChecks = Float.valueOf(text);
                }
                if (line.startsWith((String) config.getString("feesSummary"))) {
                    section = (String) config.getString("feesSummary");
                    text = (line.substring(section.length(), line.length())).trim();
                    text = text.replaceAll("\\,", "");
                    totalFees = Float.valueOf(text);
                }
                if (line.startsWith((String) config.getString("endingSummary"))) {
                    section = (String) config.getString("endingSummary");
                    text = (line.substring(section.length(), line.length())).trim();
                    int amtPtr = text.indexOf('$');
                    String amt = text.substring(amtPtr + 1, text.length());
                    amt = amt.replaceAll("\\,", "");
                    String date = text.substring(0, amtPtr - 1);
                    text = date.trim();
                    try {
                        endDate = DateConvertor.convert(text);
                    } catch (ParseException ex) {
                        Logger.getLogger(Importer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    endingBalance = Float.parseFloat(amt);
                    headerDone = true;
                }
            } else {
                if (line.startsWith((String) config.getString("depositsSection"))) {
                    section = "deposits";
                    continue;
                }
                if (line.startsWith((String) config.getString("withdrawalsSection"))) {
                    section = "withdrawals";
                    depositsDone = true;
                    continue;
                }
                if (line.startsWith((String) config.getString("checksSection"))) {
                    section = "checks";
                    depositsDone = true;
                    withdrawalsDone = true;
                    continue;
                }
                if (line.startsWith((String) config.getString("feesSection"))) {
                    section = "fees";
                    depositsDone = true;
                    withdrawalsDone = true;
                    continue;
                }
                if (section.equals("checks")) {
                    int idx;
                    String lineLeft = line.trim();
                    
                    idx = lineLeft.indexOf(" ");
                    String date = lineLeft.substring(0, idx).trim();
                    lineLeft = lineLeft.substring(idx).trim();

                    idx = lineLeft.indexOf(" ");
                    String checkNum = lineLeft.substring(0, lineLeft.indexOf(" ")).trim();
                    lineLeft = lineLeft.substring(idx).trim();

                    idx = lineLeft.indexOf(" ");
                    String amt;
                    if(idx == -1) {
                        amt = lineLeft.substring(0).trim();
                        lineLeft = "";
                    } else {
                        amt = lineLeft.substring(0, idx).trim();
                        lineLeft = lineLeft.substring(idx).trim();
                    }
                    
                    Ledger item = new Ledger();

                    item.setTransDate(stringToDate(date));
                    item.setCheckNum(checkNum);
                    item.setTransDesc("Check# " + checkNum);
                    item.setTransAmt(Float.valueOf(amt));
                    getItemList().add(item);
//                    System.out.println(item.getTransDate().toString() + " " + item.getTransDesc() + " " + item.getTransAmt());

                    if (lineLeft.length() > 0) {
                        idx = lineLeft.indexOf(" ");
                        date = lineLeft.substring(0, idx).trim();
                        lineLeft = lineLeft.substring(idx).trim();
                        
                        idx = lineLeft.indexOf(" ");
                        checkNum = lineLeft.substring(0, idx).trim();
                        lineLeft = lineLeft.substring(idx).trim();

                        idx = lineLeft.indexOf(" ");
                        if(idx == -1) {
                            amt = lineLeft.substring(0).trim();
                        } else {
                            amt = lineLeft.substring(0,idx).trim();
                        }

                        item = new Ledger();
                        item.setTransDate(stringToDate(date));
                        item.setTransDesc("Check# " + checkNum);
                        item.setCheckNum(checkNum);
                        item.setTransAmt(Float.valueOf(amt));
//                        System.out.println(item.getTransDate().toString() + " " + item.getTransDesc() + " " + item.getTransAmt());
                        getItemList().add(item);
                    }
                } else {
                    text = line.trim();
                    int idx = text.indexOf(" ");
                    String date = text.substring(0, idx);
                    text = text.substring(date.length(), text.length());
                    text = StringUtil.fTrim(text);
                    idx = text.lastIndexOf(' ');
                    String amt = text.substring(idx + 1);
                    amt = amt.replace(",", "");
                    text = text.substring(0, idx).trim();

                    Ledger item = new Ledger();
                    item.setTransDate(stringToDate(date));
                    item.setTransDesc(text);
                    item.setTransAmt(Float.valueOf(amt));
//                    System.out.println(item.getTransDate().toString() + " " + item.getTransDesc() + " " + item.getTransAmt());
                    getItemList().add(item);
                }
            }
        }
    }

    private void doSort() {
        if (!itemList.isEmpty()) {
            getItemList().sort(ledgerComparator);
        }
    }

    private Date stringToDate(String str) throws ParseException {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("MM/dd/yy");
        Date inDate = inputDateFormat.parse(str);
        return inDate;
    }

    public static Comparator<Ledger> ledgerComparator = new Comparator<Ledger>() {
        @Override
        public int compare(Ledger ledger1, Ledger ledger2) {
            Date date1 = ledger1.getTransDate();
            Date date2 = ledger2.getTransDate();
            return date1.compareTo(date2);
        }
    };

    @Override
    public void run() {
        setItemList(new ArrayList<>());
        Float balance;
        DecimalFormat f = new DecimalFormat("#####.00");
        if (f instanceof DecimalFormat) {
            ((DecimalFormat) f).setDecimalSeparatorAlwaysShown(true);
        }
        try {
            config = ResourceBundle.getBundle(configName);
            in = openFile(getFileName());
            doImport(in);
            in.close();
            doSort();
            balance = beginningBalance;
            for (Ledger l : getItemList()) {
                balance += l.getTransAmt();
                l.setTransBal(balance);
            }
        } catch (MissingResourceException | NullPointerException e) {
            System.out.println(e.toString());
        } catch (IOException ex) {
            System.out.println(ex.toString());
            Logger.getLogger(Importer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Importer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * @return the itemList
     */
    public ArrayList<Ledger> getItemList() {
        return itemList;
    }

    /**
     * @param itemList the itemList to set
     */
    public void setItemList(ArrayList<Ledger> itemList) {
        this.itemList = itemList;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
