/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.app.utils;

import com.webfront.bean.XrefManager;
import com.webfront.model.LedgerItem;
import com.webfront.model.Xref;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author rlittle
 */
public class QifExporter extends Exporter {

    private HashMap<String, String> map;
    ObservableList<Xref> xrefList;
    HashMap<String, String> argMap;

    public QifExporter(File f) {
        super(f);
        setMap();
        argMap = new HashMap<>();
        argMap.put("type", "X");
        xrefList = FXCollections.observableArrayList(XrefManager.getInstance().getList(argMap));
    }

    @Override
    protected Void call() throws Exception {
        doSelect();
        Double itemCount = (double) getItemsList().size();
        Double progress = (double) 0;
        Double itemsCreated = (double) 0;
        BufferedWriter writer = new BufferedWriter(new FileWriter(super.outputFile));
        String text = "!Type:Cash\n\n";
        writer.write(text);
        for (LedgerItem l : getItemsList()) {
            String trans = createTransaction(l);
            writer.write(trans);
            itemsCreated += 1;
            progress = itemsCreated / itemCount;
            updateProgress(progress, 1);
        }
        writer.flush();
        writer.close();
        isDoneProperty.set(true);
        return null;
    }

    private void setMap() {
        map = new HashMap<>();
        map.put("transDate", "D");
        map.put("transAmt", "T");
        map.put("checkNum", "N");
        map.put("transDesc", "P");
        map.put("payee","P");
        map.put("primaryCat", "L");
        map.put("subCat", "S");
        map.put("businessExpense", "F");
        map.put("endTrans", "^");
    }

    @Override
    public void doExport() {

    }

    private String createTransaction(LedgerItem l) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(map.get("transDate"));
        buffer.append(l.getDate());
        buffer.append("\n");
        buffer.append(map.get("transDesc"));
        buffer.append(l.getDescription());
        buffer.append("\n");
        if (l.getCheckNumber() != null && !l.getCheckNumber().isEmpty()) {
            buffer.append(map.get("checkNum"));
            buffer.append(l.getCheckNumber());
            buffer.append("\n");
        }
        buffer.append(map.get("transAmt"));
        buffer.append(l.getAmount());
        buffer.append("\n");
        String c1 = Integer.toString(l.getPrimaryCat());
        String c2 = Integer.toString(l.getSubCat());
        c1 = catXref(c1);
        buffer.append(map.get("primaryCat"));
        buffer.append(c1);
        buffer.append("\n");
        buffer.append(map.get("subCat"));
        c2 = catXref(c2);
        buffer.append(c2);
        buffer.append("\n");
        buffer.append("^");
        buffer.append("\n");
        buffer.append("\n");
        return buffer.toString();
    }

    private String catXref(String catNum) {
        for (Xref xref : xrefList) {
            if (catNum.equals(xref.getCat1().toString())) {
                return xref.getCat2().toString();
            }
        }
        return "";
    }

}
