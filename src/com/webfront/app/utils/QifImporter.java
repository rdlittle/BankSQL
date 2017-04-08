/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.app.utils;

import com.webfront.bean.LedgerManager;
import com.webfront.model.Account;
import com.webfront.model.Ledger;
import com.webfront.model.LedgerItem;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 *
 * @author rlittle
 */
public class QifImporter extends Importer {

    ResourceBundle map;
    HashMap<Integer, String> buffer;
    ArrayList<LedgerItem> itemsList;

    public QifImporter(String fileName, Account acct) {
        super(fileName, acct);
        map = ResourceBundle.getBundle("com.webfront.app.bank");
        buffer = new HashMap<>();
        itemsList = new ArrayList<>();
        ledgerItemList = new ArrayList<>();
    }

    @Override
    public void doImport(BufferedReader reader) throws IOException, ParseException {
        loadImportFile(reader);
        int bufferSize = buffer.size();
        StringBuilder sb = new StringBuilder();
        for (int lineNum = 0; lineNum <= bufferSize; lineNum++) {
            String text = buffer.get(lineNum);
            if (text.startsWith("!") || text.isEmpty() || text == null) {
            } else if (text.startsWith("^")) {
                lineNum++;
                text = sb.toString();
                LedgerItem item = createItem(text);
                if (item != null) {
                    ledgerItemList.add(item);
                }
                sb = new StringBuilder();
            } else {
                sb.append(text);
                sb.append("\n");
            }
        }
        buffer.clear();
        Float lastBalance = new Float(0.0);
        if (!ledgerItemList.isEmpty()) {
            ledgerItemList.sort(LedgerItem.LedgerComparator);
            LedgerManager mgr = LedgerManager.getInstance();
            lastBalance = mgr.getOpeningBalance(account);
            for (LedgerItem li : ledgerItemList) {
                Ledger l = new Ledger();
                boolean isCredit = true;
                if (li.getAmount().startsWith("-")) {
                    isCredit = false;
                }
                float amount = Float.parseFloat(li.getAmount());
                if (isCredit) {
                    lastBalance += amount;
                } else {
                    lastBalance += amount;
                }
                li.setAmount(li.getAmount().replaceFirst("-", ""));
                l.setAccount(account);
                l.setTransDate(new Date(li.getDateValue()));
                String desc = li.getDescription().toUpperCase();
                if (desc.contains("SUBSTITUTE CHECK")) {
                    l.setCheckNum(li.getCheckNumber());
                }                
                l.setTransDesc(li.getDescription());
                l.setTransAmt(Float.parseFloat(li.getAmount()));
                l.setTransBal(lastBalance);
                itemList.add(l);
            }
        }
    }

    private LedgerItem createItem(String text) {
        String data[] = text.split("\n");
        LedgerItem item = new LedgerItem();
        int sz = data.length;
        for (int i = 0; i < sz; i++) {
            String firstChar = data[i].substring(0, 1).toUpperCase();
            int dataSize = data[i].length();
            String fieldData = data[i].substring(1, dataSize);
            switch (firstChar) {
                case "D":
                    item.setDate(fieldData);
                    break;
                case "P":
                    item.setDescription(fieldData);
                    break;
                case "M":
                    item.setDescription(fieldData.trim());
                    break;
                case "N":
                    item.setCheckNumber(fieldData);
                    break;
                case "T":
                    item.setAmount(fieldData);
                    break;
            }
        }
        return item;
    }

    private void loadImportFile(BufferedReader reader) throws IOException {
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
