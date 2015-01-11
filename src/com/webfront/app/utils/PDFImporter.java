/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.app.utils;

import com.webfront.bean.LedgerManager;
import com.webfront.model.Config;
import com.webfront.model.Ledger;
import com.webfront.model.LedgerEntry;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.jdom2.Content;
import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author rlittle
 */
public class PDFImporter extends Importer {

    protected String fileName;
    SAXBuilder jdomBuilder;
    Document xmlDoc;
    private String txtOutput;
    private final Config cfg = Config.getInstance();
    private BufferedReader txtReader;
    Float lastBalance;

    ArrayList<LedgerEntry> entries;

    public PDFImporter(String fileName, int accountId) {
        super(fileName,accountId);
        this.fileName = fileName;
        entries = new ArrayList<>();
        LedgerManager mgr = new LedgerManager();
        Ledger item = mgr.getItem(mgr.getLastId());
        lastBalance = item.getTransBal();
    }

    @Override
    public void doImport(BufferedReader reader) throws IOException, ParseException {
        PDFTextStripper pdfStripper = new PDFTextStripper();
        BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(fileName));
        try (PDDocument document = PDDocument.load(inStream)) {
            txtOutput = pdfStripper.getText(document);
            try (FileWriter writer = new FileWriter(cfg.getTmpDir() + cfg.getFileSep() + "pdfOut.txt")) {
                writer.write(txtOutput);
                writer.close();
                txtReader = new BufferedReader(new FileReader(cfg.getTmpDir() + cfg.getFileSep() + "pdfOut.txt"));
            }
            getConfig();
            Element root = xmlDoc.getRootElement();
            for (Element element : root.getChildren()) {
                int lines = 1;
                try {
                    if (element.getAttribute("lines") != null) {
                        lines = element.getAttribute("lines").getIntValue();
                    }
                    switch (element.getName()) {
                        case ("ignore"):
                            String str;
                            for (int l = 1; l <= lines; l++) {
                                str = txtReader.readLine();
                            }
                            break;
                        case ("header"):
                            for (Element section : element.getChildren()) {
                                processHeader(section);
                            }
                            break;
                        case ("summary"):
                            processElement(element, lines);
                            break;
                        case ("detail"):
                            for (Element section : element.getChildren()) {
                                if (section.getName().equals("ignore")) {
                                    if (section.getAttribute("lines") != null) {
                                        lines = Integer.parseInt(section.getAttributeValue("lines"));
                                        for (int l = 1; l <= lines; l++) {
                                            str = txtReader.readLine();
                                        }
                                    }
                                } else {
                                    processSection(section);
                                }
                            }
                            break;
                        case ("footer"):
                            break;
                    }
                } catch (DataConversionException ex) {
                    Logger.getLogger(PDFImporter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        entries.sort(LedgerEntry.LedgerComparator);
        for(LedgerEntry item : entries) {
            java.util.Date date = new java.util.Date(DateConvertor.toLong(item.getDate(), "MM/dd/yyyy"));
            String amountString = item.getAmount();
            boolean isCredit = true;
            if(item.getAmount().startsWith("-")) {
                isCredit = false;
                String amt = item.getAmount().replaceFirst("-", "");
                item.setAmount(amt);
            }
            float amount = Float.parseFloat(amountString);
            if (isCredit) {
                lastBalance += amount;
                totalDeposits+=amount;
            } else {
                lastBalance -= amount;
                totalWithdrawals+=amount;
            }
            Ledger ledger = new Ledger(null,date,amount,lastBalance,accountId);
            if(item.getDescription().length()>120) {
                item.setDescription(item.getDescription().substring(0,119));
            }
            ledger.setTransDesc(item.getDescription());
            getItemList().add(ledger);
        }
    }

    public void processElement(Element e, int lines) {
        List<Element> children = e.getChildren();
        int currLine = 0;
        for (Element child : children) {
            boolean hasFormat = false;
            String content = child.getAttributeValue("content");
            String format = child.getAttributeValue("format");
            String text = child.getText();
            if (format != null && format.equals("regex")) {
                hasFormat = true;
            }
            try {
                String str = txtReader.readLine().trim();
                if (hasFormat) {
                    Pattern p = Pattern.compile(text);
                    Matcher m = p.matcher(str);
                    if (m.matches()) {
                        System.out.println(str);
                    }
                }
                if (content.equals("data")) {
                    int fields = Integer.parseInt(child.getAttributeValue("fields"));
                    String delimiter = child.getAttributeValue("delimiter");
                    String[] data = str.split(delimiter);
                    for (Element dataLine : child.getChildren()) {
                        if (dataLine.getName().equals("field")) {
                            int fieldNum = Integer.parseInt(dataLine.getAttributeValue("number"));
                            String tag = dataLine.getAttributeValue("content");
                            summary.put(tag, data[fieldNum]);
                            System.out.println(tag + " " + data[fieldNum]);
                            data[fieldNum]=data[fieldNum].replaceAll(",", "");
                            switch(tag) {
                                case "beginningBalance":
                                    beginningBalance=Float.parseFloat(data[fieldNum]);
                                    break;
                                case "totalDeposits":
                                    totalDeposits=Float.parseFloat(data[fieldNum]);
                                    break;
                                case "totalWithdrawals":
                                    totalWithdrawals=Float.parseFloat(data[fieldNum]);
                                    break;
                                case "endingBalance":
                                    endingBalance=Float.parseFloat(data[fieldNum]);
                                    break;                                    
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(PDFImporter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void processHeader(Element section) {
        String contentDesc = (section.getAttribute("content") == null ? "" : section.getAttributeValue("content"));
        Element startElement = section.getChild("start");
        Element endElement = section.getChild("end");
        Element lineDefinition = section.getChild("line");
        Pattern linePattern = Pattern.compile(lineDefinition.getText());
        Pattern endPattern = Pattern.compile(endElement.getText());

        int lines = 1;
        String text = "";

        if (startElement.getAttributeValue("lines") != null) {
            lines = Integer.parseInt(startElement.getAttributeValue("lines"));
            for (int l = 1; l <= lines; l++) {
                try {
                    text = txtReader.readLine().trim();
                } catch (IOException ex) {
                    Logger.getLogger(PDFImporter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        Matcher endMatcher = endPattern.matcher(text);
        Matcher lineMatcher = linePattern.matcher(text);

        if (endMatcher.matches()) {
            return;
        }

        if (lineMatcher != null) {
            while (lineMatcher.find()) {
                text = lineMatcher.group();
                if (contentDesc.equals("accountInfo")) {
                    summary.put("accountNumber", text);
                } else if (contentDesc.equals("statementInfo")) {
                    if (summary.get("startDate").isEmpty() || summary.get("startDate") == "") {
                        summary.put("startDate", text);
                        this.startDate=text;
                    } else {
                        summary.put("endDate", text);
                        this.endDate=text;
                    }
                }
                System.out.println(text);
            }
        }
    }

    public void processSection(Element section) {
        String name = section.getName();
        String contentDesc = (section.getAttribute("content") == null ? "" : section.getAttributeValue("content"));
        int lines = 1;
        Element startElement = section.getChild("start");
        Element endElement = section.getChild("end");
        Element dataDefinition = section.getChild("data");
        Element lineDefinition = section.getChild("line");
        Pattern dataPattern = null;
        Pattern linePattern = null;
        String sign = (section.getAttribute("sign") == null ? "+" : section.getAttributeValue("sign"));

        if (startElement.getAttributeValue("lines") != null) {
            lines = Integer.parseInt(startElement.getAttributeValue("lines"));
            String text;
            for (int l = 1; l <= lines; l++) {
                try {
                    text = txtReader.readLine().trim();
                } catch (IOException ex) {
                    Logger.getLogger(PDFImporter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        if (lineDefinition != null) {
            linePattern = Pattern.compile(lineDefinition.getText());
        }
        Pattern endPattern = Pattern.compile(endElement.getText());
        while (true) {
            try {
                String text = txtReader.readLine().trim();
                Matcher lineMatcher = null;
                if (lineDefinition != null) {
                    lineMatcher = linePattern.matcher(text);
                }
                Matcher endMatcher = endPattern.matcher(text);
                if (endMatcher.matches()) {
                    break;
                }
                if (lineMatcher != null && lineMatcher.matches()) {
                    System.out.println(text);
                    if (dataDefinition != null) {
                        LedgerEntry entry = new LedgerEntry();
                        for (Element dataLine : dataDefinition.getChildren()) {
                            String tag = dataLine.getAttributeValue("content");
                            String regex = dataLine.getText();
                            Matcher matcher = Pattern.compile(regex).matcher(text);
                            String value = "";
                            if (matcher.find()) {
                                value = matcher.group();
                            }
                            if (tag.equals("date")) {
                                entry.setDate(value);
                                text = matcher.replaceFirst("");
                            }
                            if (tag.equals("amount")) {
                                if (sign.equals("-")) {
                                    value = sign + value;
                                }
                                entry.setAmount(value);
                                text = matcher.replaceFirst("");
                                if(section.getAttributeValue("content").equals("fees")) {
                                    String amt = entry.getAmount();
                                    amt=amt.replaceAll("-", "");
                                    amt=amt.replaceAll(",", "");
                                    totalFees+=Float.parseFloat(amt);
                                }
                            }
                            if (tag.equals("description")) {
                                entry.setDescription(value);
                            }
                        }
                        if (!contentDesc.equals("dailyBalance")) {
                            entries.add(entry);
                        }
                    }
                } else {
                    if (!contentDesc.equals("dailyBalance")) {
                        int lastEntry = entries.size() - 1;
                        LedgerEntry entry = entries.get(lastEntry);
                        entry.setDescription(entry.getDescription() + " " + text);
                        entries.set(lastEntry, entry);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(PDFImporter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void getConfig() {
        jdomBuilder = new SAXBuilder();
        String xmlSource = cfg.getInstallDir() + cfg.getFileSep() + "pnc.xml";
        try {
            xmlDoc = jdomBuilder.build(xmlSource);
            Element root = xmlDoc.getRootElement();
            List<Content> configContent = root.getContent();
            List<Element> childElements = root.getChildren();
            Element header = root.getChild("header");
            for (Element e : header.getChildren()) {
                String content = e.getAttributeValue("content");
                String format = e.getAttributeValue("format");
                String text = e.getText();
            }
        } catch (JDOMException ex) {
            Logger.getLogger(PDFImporter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PDFImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
