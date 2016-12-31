/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.app.utils;

import com.webfront.model.Ledger;
import com.webfront.model.Payment;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rlittle
 */
public class QifExporter extends Exporter {

    private HashMap<String, String> map;

    public QifExporter(File f) {
        super(f);
        setMap();
    }

    public String toTrans(String key, String value) {
        return "";
    }

    @Override
    public void run() {
        try {
            doSelect();
            double itemsDone = 0D;
            Double itemCount = (double) getList().size();
            progressProperty.set(itemsDone);
            BufferedWriter writer = new BufferedWriter(new FileWriter(super.outputFile));
            String text = "";
            text = "!Type:cash\n";
            writer.write(text);
            for (Ledger item : getList()) {
                String trans = createTransaction(item);
                writer.write(trans);
                progressProperty.set(itemsDone / itemCount);
            }
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Exporter.class.getName()).log(Level.SEVERE, null, ex);
        }
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
                String c1 = p.getPrimaryCat().getId().toString();
                String c2 = p.getStore().getId().toString();
                String desc = p.getTransDesc();
                String amt = Float.toString(p.getTransAmt());
                buffer.append(map.get("transDate"));
                buffer.append(transDate);
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
                buffer.append("\n");
            }
            buffer.append(map.get("transAmt"));
            buffer.append(Float.toString(l.getTransAmt()));
            buffer.append("\n");
            if (l.getPrimaryCat() != null) {
                buffer.append(map.get("primaryCat"));
                buffer.append(l.getPrimaryCat().getId());
                buffer.append("\n");
            }
            if (l.getSubCat() != null) {
                buffer.append(map.get("subCat"));
                buffer.append(l.getSubCat().getId());
                buffer.append("\n");
            }
            buffer.append("^");
            buffer.append("\n");
        }

        return buffer.toString();
    }

}
