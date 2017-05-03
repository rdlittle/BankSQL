/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.app.utils;

import com.webfront.bean.LedgerManager;
import com.webfront.bean.XrefManager;
import com.webfront.model.Account;
import com.webfront.model.Ledger;
import com.webfront.model.LedgerItem;
import com.webfront.model.Xref;
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
    Account account;
    private XrefManager xrefManager;

    public CSVImporter(String fileName, Account acct) {
        super(fileName, acct);
        account = acct;
        getAccountConfig();
        buffer = new HashMap<>();
        entries = new ArrayList<>();
        lastBalance = new Float(0.0);
        LedgerManager mgr = LedgerManager.getInstance();
        int lastId = mgr.getLastId(account.getId());
        if (lastId > 0) {
            Ledger item = mgr.getItem(lastId);
            if (item != null) {
                lastBalance += item.getTransBal();
            }
        }
        beginningBalance = lastBalance;
        if (account.isXlateCat() || account.isXlateStore()) {
            xrefManager = XrefManager.getInstance();
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
        int fields;
        String line;

        Matcher lineMatcher;
        Matcher fieldMatcher;
        Element lineDefinition = section.getChild("line");
        Element dataDefinition = section.getChild("data");
        if (dataDefinition == null) {
            return;
        }
        if (dataDefinition.getAttribute("fields") == null) {
            fields = 0;
        } else {
            fields = Integer.parseInt(dataDefinition.getAttributeValue("fields"));
        }
        for (lineNum = 0; lineNum <= bufferSize; lineNum++) {
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
<<<<<<< HEAD
//                    for (int g = 1; g <= groups; g++) {
//                        System.out.println(g + ") " + lineMatcher.group(g));
//                    }
                    item.setDate(lineMatcher.group(2).replaceAll("\"", ""));
                    item.setDescription(lineMatcher.group(5).replaceAll("\"", ""));
                    if (lineMatcher.group(7) != null) {
                        String charge = lineMatcher.group(7).replaceAll("\"", "");
                        item.setAmount(charge.replaceAll(",", ""));
                    }
                    if (lineMatcher.group(9) != null) {
                        String payment = lineMatcher.group(9).replaceAll("\"", "");
                        amount = Float.parseFloat(payment.replaceAll(",", ""));
                        amount = amount * -1;
                        item.setAmount(amount.toString());
=======
                    for (Element dataElement : dataDefinition.getChildren()) {
                        int fieldNumber = Integer.parseInt(dataElement.getAttributeValue("number"));
                        String fieldName = dataElement.getAttributeValue("name");
                        String fieldPattern = dataElement.getText();
                        fieldMatcher = Pattern.compile(fieldPattern).matcher(line);
                        String fieldData = lineMatcher.group(fieldNumber);
                        
                        if (fieldName.equalsIgnoreCase("transDate")) {
                            item.setDate(fieldData);
                        } else if (fieldName.equalsIgnoreCase("checkNumber")) {
                            item.setCheckNumber(fieldData);
                        } else if (fieldName.equalsIgnoreCase("transDescription")) {
                            if (fieldData.isEmpty() && !item.getCheckNumber().isEmpty()) {
                                fieldData = "Check # " + item.getCheckNumber();
                            }
                            item.setDescription(fieldData);
                            if (account.isXlateStore()) {
                                Xref xref = xrefManager.lookup(fieldData, 'S');
                                if (xref!= null) {
                                    item.setStoreId(groups);
                                } else {
                                    xrefManager.addXref(fieldData, 'S');
                                }
                            }
                        } else if (fieldName.equalsIgnoreCase("category")) {
                            if (fieldData.startsWith("Payment")) {
                                item.setTransType('I');
                            } else {
                                item.setTransType('E');
                            }
                            if(account.isXlateCat()) {
                                Xref xref = xrefManager.lookup(fieldData,'C');
                                if (xref != null) {
                                    
                                } else {
                                    xrefManager.addXref(fieldData, 'C');
                                }
                            }
                        } else if (fieldName.equalsIgnoreCase("credit")) {
                            if (fieldData != null) {
                                item.setAmount(fieldData);
                            }
                        } else if (fieldName.equalsIgnoreCase("debit")) {
                            if (item.getTransType()=='D') {
                                fieldData = "-" + fieldData;
                            }
                            if (fieldData != null) {
                                item.setAmount(fieldData);
                            }
                        }
>>>>>>> revision1
                    }
                    entries.add(item);
                }
            }
        }

        doSort();

        for (LedgerItem item : entries) {
            java.util.Date date = new java.util.Date(DateConvertor.toLong(item.getDate(), "MM/dd/yyyy"));
            String amountString = item.getAmount();
            boolean isCredit = false;
            if (item.getAmount().startsWith("-") || item.getTransType()=='I') {
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
            Ledger ledger = new Ledger(null, date, amount, lastBalance, account);
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

    @Override
    public void doSort() {
        entries.sort(LedgerItem.LedgerComparator);
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
