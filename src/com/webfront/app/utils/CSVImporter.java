/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.app.utils;

import com.webfront.model.Ledger;
import com.webfront.view.LedgerView;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author rlittle
 */
public final class CSVImporter extends Importer {

    ArrayList<Ledger> itemList;

    private String fileName;
    String currencyString = "^.*\\,\\\"\\-{0,1}\\d{0,3}\\,{0,1}\\d{3}\\.{1}\\d{2}\\\".*";
    //String currencyString = "^.*\\,\\\"\\-{0,1}\\d{0,3}\\,{0,1}\\d{3}\\.{1}\\d{2}\\\"";
    Pattern currencyPattern;
    Matcher currencyMatcher;

    public CSVImporter(String fileName, String cfgName, int accountId) {
        super(fileName,accountId);
        configName = cfgName;
        currencyPattern = Pattern.compile(currencyString);
    }

    public BufferedReader openFile(String fileName) {
        BufferedReader inFile = null;
        try {
            inFile = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CSVImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return inFile;
    }

    public void doImport(BufferedReader reader) throws IOException, ParseException {
        String text;
        String line;

        String date;
        String reference;
        String payee;
        String memo;
        String amt;
        String category;

        Float amount;
        Float balance;

        LedgerView view = new LedgerView(accountId);
        int lastId = view.getLedgerManager().getLastId();
        Ledger item = view.getLedgerManager().getItem(lastId);
        this.beginningBalance = item.getTransBal();
        this.endingBalance = this.beginningBalance;
        String[] fields;
        headerDone = false;
        boolean isCheck;
        boolean isDeposit;
        boolean isWithdrawal;
        boolean isTransfer;
        boolean isFee;
        int lineNum = 0;

        while ((text = reader.readLine()) != null) {
            line = text.trim();
            if (!headerDone) {
                if (line.startsWith((String) config.getString("header"))) {
                    headerDone = true;
                }
            } else {
                lineNum += 1;

                text = line.trim();
                currencyMatcher = currencyPattern.matcher(text);

                fields = text.split(",", 5);
                date = fields[0];
                reference = fields[1];
                payee = fields[2].replaceAll("\"", "");
                memo = fields[3].replaceAll("\"", "");;
                amt = fields[4].replaceAll("\\\"", "");
                amt = amt.replaceAll("\\,", "");
                amount = Float.valueOf(amt);
                isCheck = memo.equals("CHECK");
                isDeposit = memo.contains("DEPOSIT");
                isTransfer = (memo.contains("TRANSFER") || memo.startsWith("\"INTERNET TFR\""));
                isWithdrawal = (amount < 0);

                this.endDate=date;
                if (lineNum == 1) {
                    this.startDate = date;
                }
                if (isCheck) {
                    this.totalChecks += (-1 * amount);
                }
                if (isDeposit) {
                    this.totalDeposits += amount;
                }
                if (isTransfer) {
                    this.totalTransfers += amount;
                }
                if (isWithdrawal) {
                    this.totalWithdrawals -= amount;
                }

                this.endingBalance += amount;

                item = new Ledger();
                item.setTransDate(stringToDate(date));
                item.setTransDesc(payee);
                item.setTransAmt(amount);
                item.setTransBal(this.endingBalance);
//                    System.out.println(item.getTransDate().toString() + " " + item.getTransDesc() + " " + item.getTransAmt());
                getItemList().add(item);
            }
        }
    }

}
