/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.app.utils;

import com.webfront.bean.LedgerManager;
import com.webfront.model.Ledger;
import com.webfront.model.LedgerItem;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom2.Element;

/**
 *
 * @author rlittle
 */
public class CSVImporter extends Importer {

    HashMap<Integer, String> buffer;
    ArrayList<LedgerItem> entries;
    Float lastBalance;

    public CSVImporter(String fileName, int accountId) {
        super(fileName, accountId);
        getAccountConfig();
        buffer = new HashMap<>();
        entries = new ArrayList<>();
        lastBalance = new Float(0.0);
        LedgerManager mgr = new LedgerManager();
        int lastId = mgr.getLastId(accountId);
        if (lastId > 0) {
            Ledger item = mgr.getItem(lastId);
            if (item != null) {
                lastBalance += item.getTransBal();
            }
        }
    }

    @Override
    public void doImport(BufferedReader reader) throws IOException, ParseException {
        loadImportFile(reader);
        Element root = accountConfigXml.getRootElement(); // <statement>
        for (Element el : root.getChildren()) { //    <detail>
            for (Element section : el.getChildren()) { // <section> 
                String sectionName = section.getAttributeValue("content");
                if (sectionName == null) {
                    continue;
                }
                switch (sectionName) {
                    case "transactions":
                        processSection(section);
                        break;
                }
            }

        }
    }

    public void processSection(Element section) throws ParseException {
        int bufferSize = buffer.size();
        int lineNum;
        String line;

        Matcher lineMatcher = null;
        Element dataDefinition = section.getChild("data");
        if (dataDefinition == null) {
            return;
        }
        Element lineDefinition = section.getChild("line");

        for (lineNum = 1; lineNum <= bufferSize; lineNum++) {
            line = buffer.get(lineNum);
            if (line == null || line.isEmpty()) {
                continue;
            }

            Float amount;
            
            if (lineDefinition != null) {
                lineMatcher = Pattern.compile(lineDefinition.getText()).matcher(line);
                if (lineMatcher.matches()) {
                    LedgerItem item = new LedgerItem();
                    int groups = lineMatcher.groupCount();
                    for (int g = 1; g <= groups; g++) {
                        System.out.println(g + ") " + lineMatcher.group(g));
                    }
                    item.setDate(lineMatcher.group(1).replaceAll("\"", ""));
                    item.setDescription(lineMatcher.group(3).replaceAll("\"", ""));
                    if (lineMatcher.group(4) != null) {
                        String charge = lineMatcher.group(4).replaceAll("\"", "");
                        item.setAmount(charge.replaceAll(",", ""));
                    }
                    if (lineMatcher.group(8) != null) {
                        String payment = lineMatcher.group(8).replaceAll("\"", "");
                        amount = Float.parseFloat(payment.replaceAll(",", ""));
                        amount = amount * -1;
                        item.setAmount(amount.toString());
                    }
                    entries.add(item);
                }
            }
        }
        
        entries.sort(LedgerItem.LedgerComparator);
        for (LedgerItem item : entries) {
            java.util.Date date = new java.util.Date(DateConvertor.toLong(item.getDate(), "MM/dd/yyyy"));
            String amountString = item.getAmount();
            boolean isCredit = false;
            if (item.getAmount().startsWith("-")) {
                isCredit = true;
                String amt = item.getAmount().replaceFirst("-", "");
                item.setAmount(amt);
            }
            float amount = Float.parseFloat(amountString);
            if (isCredit) {
                lastBalance += amount;
                totalDeposits -= amount;
            } else {
                lastBalance += amount;
                totalWithdrawals += amount;
            }
            Ledger ledger = new Ledger(null, date, amount, lastBalance, accountId);
            if (item.getDescription().length() > 120) {
                item.setDescription(item.getDescription().substring(0, 119));
            }
            if (item.getCheckNumber() != null && !item.getCheckNumber().isEmpty()) {
                ledger.setCheckNum(item.getCheckNumber());
                totalChecks -= amount;
            }
            ledger.setTransDesc(item.getDescription());
            getItemList().add(ledger);
        }
    }

    public void loadImportFile(BufferedReader reader) throws IOException {
        int lineNum = 0;
        String line;
        while (true) {
            try {
                line = reader.readLine();
                if (line == null) {
                    break;
                }
                buffer.put(lineNum, line);
                lineNum++;
            } catch (IOException ex) {
                break;
            }
        }
        reader.close();
    }

}
