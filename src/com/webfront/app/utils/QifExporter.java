/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.app.utils;

import com.webfront.bean.XrefManager;
import com.webfront.model.Ledger;
import com.webfront.model.Payment;
import com.webfront.model.Xref;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.format.DateTimeFormatter;
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

    public String toTrans(String key, String value) {
        return "";
    }

    @Override
    protected Void call() throws Exception {
        doSelect();
        Double itemCount = (double) getList().size();
        Double progress = (double) 0;
        Double itemsCreated = (double) 0;
        BufferedWriter writer = new BufferedWriter(new FileWriter(super.outputFile));
        String text = "!Type:cash\n";
        writer.write(text);
        for (Ledger l : getList()) {
            String trans = createTransaction(l);
            writer.write(trans);
            itemsCreated += 1;
            progress = itemsCreated / itemCount;
            updateProgress(progress, 1);
        }
        writer.flush();
        writer.close();
        return null;
    }

    private void setMap() {
        map = new HashMap<>();
        map.put("transDate", "D");
        map.put("transAmt", "T");
        map.put("checkNum", "N");
        map.put("transDesc", "P");
        map.put("primaryCat", "L");
        map.put("subCat", "S");
        map.put("businessExpense", "F");
        map.put("endTrans", "^");
    }

    @Override
    public void doExport() {

    }

    private String createTransaction(Ledger l) {
        String transDate = DateConvertor.toLocalDate(l.getTransDate()).format(DateTimeFormatter.ISO_LOCAL_DATE);
        StringBuilder buffer = new StringBuilder();
        if (l.getPayment() != null && l.getPayment().size() > 0) {
            for (Payment p : l.getPayment()) {
                String c1 = catXref(p.getPrimaryCat().getId().toString());
                String c2 = catXref(p.getStore().getId().toString());
                String desc = p.getTransDesc();
                String amt = Float.toString(p.getTransAmt());
                buffer.append(map.get("transDate"));
                buffer.append(transDate);
                buffer.append("\n");
                buffer.append(map.get("transDesc"));
                buffer.append(desc);
                buffer.append("\n");
                buffer.append(map.get("primaryCat"));
                buffer.append(c1);
                buffer.append("\n");
                buffer.append(map.get("subCat"));
                buffer.append(c2);
                buffer.append("\n");
                buffer.append(map.get("transAmt"));
                buffer.append(amt);
                buffer.append("\n");
                buffer.append("^");
                buffer.append("\n");
            }
        } else {
            buffer.append(map.get("transDate"));
            buffer.append(transDate);
            buffer.append("\n");
            buffer.append(map.get("transDesc"));
            buffer.append(l.getTransDesc());
            buffer.append("\n");
            if (l.getCheckNum() != null) {
                buffer.append(map.get("checkNum"));
                buffer.append(l.getCheckNum());
                buffer.append("\n");
            }
            buffer.append(map.get("transAmt"));
            buffer.append(Float.toString(l.getTransAmt()));
            buffer.append("\n");
            if (l.getPrimaryCat() != null) {
                String c1 = catXref(l.getPrimaryCat().getId().toString());
                buffer.append(map.get("primaryCat"));
                buffer.append(c1);
                buffer.append("\n");
            }
            if (l.getSubCat() != null) {
                buffer.append(map.get("subCat"));
                String c2 = catXref(l.getSubCat().getId().toString());
                buffer.append(c2);
                buffer.append("\n");
            }
            buffer.append("^");
            buffer.append("\n");
        }

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
