/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.app.utils;

import com.webfront.bean.AccountManager;
import com.webfront.model.Account;
import com.webfront.model.Config;
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
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author rlittle
 */
public abstract class Importer implements Runnable {

    static BufferedReader in;
    ResourceBundle config;
    public static String configName;
    boolean headerDone;

    public String accountNumber;
    public String statementPeriod;
    public String startDate;
    public String endDate;

    public HashMap<String, String> summary;

    public Float beginningBalance;
    public Float endingBalance;
    public Float totalDeposits;
    public Float totalWithdrawals;
    public Float totalChecks;
    public Float totalFees;
    public Float totalTransfers;

    private ArrayList<Ledger> itemList;

    String fileName;
    public int accountId;

    SAXBuilder jdomBuilder;
    Document accountConfigXml;

    private final Config cfg = Config.getInstance();

    /**
    * @param fileName The full path to the file being imported
    * @param accountId The value of the id column from the bank.account table for this account
    */
   
    public Importer(String fileName, int accountId) {
        this.accountId = accountId;
        this.fileName = fileName;
        this.startDate = "";
        this.endDate = "";
        this.beginningBalance = new Float(0);
        this.endingBalance = new Float(0);
        this.totalDeposits = new Float(0);
        this.totalWithdrawals = new Float(0);
        this.totalChecks = new Float(0);
        this.totalFees = new Float(0);
        this.totalTransfers = new Float(0);
        this.itemList = new ArrayList<>();
        summary = new HashMap<>();
        summary.put("accountNumber", "");
        summary.put("startDate", "");
        summary.put("endDate", "");
        summary.put("beginningBalance", "");
        summary.put("endingBalance", "");
        summary.put("totalDeposits", "");
        summary.put("totalWithdrawals", "");
        summary.put("totalChecks", "");
        summary.put("totalFees", "");
        summary.put("totalTransfers", "");
        summary.put("totalInterest", "");
    }

    /**
    * @param fileName The full path of the data file being imported
    * @return BufferedReader for the opened file
    */
    public BufferedReader openFile(String fileName) {
        BufferedReader inFile = null;
        try {
            inFile = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Importer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return inFile;
    }

    public abstract void doImport(BufferedReader reader) throws IOException, ParseException;

    public void doSort() {
        if (!itemList.isEmpty()) {
            getItemList().sort(ledgerComparator);
        }
    }

    public void getAccountConfig() {
        AccountManager mgr = new AccountManager();
        for (Account acct : mgr.getAccounts()) {
            if (acct.getId() == accountId) {
                configName = acct.getConfigName() + ".xml";
                break;
            }
        }
        if (!configName.isEmpty()) {
            jdomBuilder = new SAXBuilder();
            String xmlSource = cfg.getInstallDir() + cfg.getFileSep() + configName;
            try {
                accountConfigXml = jdomBuilder.build(xmlSource);
            } catch (JDOMException ex) {
                Logger.getLogger(PDFImporter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PDFImporter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Date stringToDate(String str) throws ParseException {
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
            in = openFile(getFileName());
            doImport(in);
            in.close();
            doSort();
        } catch (MissingResourceException | NullPointerException e) {
            e.printStackTrace();
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
