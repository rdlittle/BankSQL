/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.app.utils;

import com.webfront.model.Account;
import com.webfront.model.LedgerItem;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
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
    }

    @Override
    public void doImport(BufferedReader reader) throws IOException, ParseException {
        loadImportFile(reader);
        int bufferSize = buffer.size();
        StringBuilder sb = new StringBuilder();
        for (int lineNum = 0; lineNum <= bufferSize; lineNum++) {
            String text = buffer.get(lineNum);
            if (text.startsWith("!") || text.isEmpty()) {
            } else if (text.startsWith("^")) {
                buffer.clear();
                lineNum++;
                LedgerItem item = createItem(text);
                if(item != null) {
                    itemsList.add(item);
                }
            } else {
                sb.append(text);
                sb.append("\n");
            }
        }
    }

    private LedgerItem createItem(String text) {
        String data[] = text.split("\n");
        int sz = data.length;
        for(int i=0;i<=sz;i++) {
            LedgerItem item = new LedgerItem();
            String firstChar = data[i].substring(0, 1).toUpperCase();
            String fieldData = data[i].substring(1,999);
            switch(firstChar) {
                case "D":
                    item.setDate(fieldData);
                    break;
                case "P":
                    item.setDescription(fieldData);
                    break;
                case "M":
                    item.setDescription(fieldData);
                    break;
                case "N":
                    item.setCheckNumber(fieldData);
                    break;
                case "T":
                    item.setAmount(fieldData);
                    break;
            }
            return item;
        }
        return null;
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
