/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.app.utils;

import com.webfront.bean.LedgerManager;
import com.webfront.model.Config;
import com.webfront.model.Ledger;
import com.webfront.model.LedgerItem;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.jdom2.Content;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author rlittle
 */
public class PDFImporter extends Importer {

//    protected String fileName;
//    SAXBuilder jdomBuilder;
//    Document xmlDoc;
    private String txtOutput;
    private final Config cfg = Config.getInstance();
    private BufferedReader txtReader;
    Float lastBalance;
    Integer currentLine;

    class ElementNotFoundException extends Exception {

        public ElementNotFoundException(String msg) {
            super(msg);
        }
    };

    ArrayList<LedgerItem> entries;
    HashMap<Integer, String> buffer;
    HashMap<String, Integer> markers;
    TreeSet sectionMarks;

    public PDFImporter(String fileName, int accountId) {
        super(fileName, accountId);
        this.fileName = fileName;
        entries = new ArrayList<>();
        lastBalance = new Float(0.0);
        buffer = new HashMap<>();
        markers = new HashMap<>();
        currentLine = 0;
        LedgerManager mgr = LedgerManager.getInstance();
        int lastId = mgr.getLastId(accountId);
        if (lastId > 0) {
            Ledger item = mgr.getItem(lastId);
            if (item != null) {
                lastBalance = item.getTransBal();
            }
        }
    }

    /**
     * 
     * @param reader BufferedReader pointing to the file being imported
     * @throws IOException
     * @throws ParseException 
     */
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
                String text = "";
                while (text != null) {
                    text = txtReader.readLine();
                    buffer.put(currentLine++, text);
                }
            }
            getAccountConfig();
            currentLine = 0;
            Element root = accountConfigXml.getRootElement();
            int maxLines = buffer.size() - 3;
            int markedLine = 0;
            // Scan the output and mark the start of each section
            for (Element el : root.getChildren()) {
                for (Element section : el.getChildren()) {
                    String sectionName = section.getAttributeValue("content");
                    Element startElement = section.getChild("start");
                    Element endElement = section.getChild("end");
                    if (startElement != null) {
                        boolean endHasBounds = true;
                        if (endElement.getAttribute("bounded") != null) {
                            String bounds = endElement.getAttributeValue("bounded");
                            if (bounds.equals("false")) {
                                endHasBounds = false;
                            }
                        }
                        Pattern linePattern = Pattern.compile(startElement.getText());
                        String text = "";
                        boolean elementFound = false;
                        while (currentLine < maxLines) {
                            text = buffer.get(currentLine++);
                            if (linePattern.matcher(text).matches()) {
                                markedLine = currentLine - 1;
                                markers.put(sectionName, markedLine);
                                elementFound = true;
                                if (!endHasBounds) {
                                    currentLine--;
                                }
                                break;
                            }
                        }
                        if (!elementFound) {
                            currentLine = markedLine;
                        }
                    }
                }
            }

            ArrayList<Integer> lineNumbers = new ArrayList<>(markers.values());
            lineNumbers.sort(new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o1.compareTo(o2);
                }
            });
            sectionMarks = new TreeSet(markers.values());
            currentLine = 0;
            for (Element element : root.getChildren()) {
                int lines = 0;
                if (element.getAttribute("lines") != null) {
                    lines = element.getAttribute("lines").getIntValue();
                }
                for (Element section : element.getChildren()) {
                    String contentDesc;
                    contentDesc = (section.getAttribute("content") == null ? "" : section.getAttributeValue("content"));
                    if (markers.containsKey(contentDesc)) {
                        currentLine = markers.get(contentDesc);
                        processSection(section);
                    }
                }
            }
        } catch (DataConversionException ex) {
            Logger.getLogger(PDFImporter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ElementNotFoundException ex) {
            Logger.getLogger(PDFImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        entries.sort(LedgerItem.LedgerComparator);
        for (LedgerItem item : entries) {
            java.util.Date date = new java.util.Date(DateConvertor.toLong(item.getDate(), "MM/dd/yyyy"));
            String amountString = item.getAmount();
            boolean isCredit = true;
            if (item.getAmount().startsWith("-")) {
                isCredit = false;
                String amt = item.getAmount().replaceFirst("-", "");
                item.setAmount(amt);
            }
            float amount = Float.parseFloat(amountString);
            if (isCredit) {
                lastBalance += amount;
                totalDeposits += amount;
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

    public void processSection(Element section) throws ElementNotFoundException {
        String name = section.getName();
        boolean hasEntry = false;
        String contentDesc = (section.getAttribute("content") == null ? "" : section.getAttributeValue("content"));
        int lines = 1;
        int maxLines = 0;
        int linesProcessed = 0;
        boolean endHasBounds = true;
        Element startElement = section.getChild("start");
        Element endElement = section.getChild("end");
        Element dataDefinition = section.getChild("data");
        Element lineDefinition = section.getChild("line");
        Pattern dataPattern = null;
        Pattern linePattern = null;
        String transType = (section.getAttribute("type") == null ? "info" : section.getAttributeValue("type"));
        String prevLine = "";
        int nextSection = buffer.size() - 1;
        if (sectionMarks.higher(currentLine) != null) {
            nextSection = (int) sectionMarks.higher(currentLine);
        }

        if (startElement.getAttributeValue("lines") != null) {
            lines = Integer.parseInt(startElement.getAttributeValue("lines"));
            currentLine += lines;
        }
        if (lineDefinition != null) {
            linePattern = Pattern.compile(lineDefinition.getText());
            if (lineDefinition.getAttribute("lines") != null) {
                String l = lineDefinition.getAttributeValue("lines");
                if (!l.equals("+")) {
                    maxLines = Integer.parseInt(lineDefinition.getAttributeValue("lines"));
                }
            }
        }
        if (endElement.getAttribute("bounded") != null) {
            String bounds = endElement.getAttributeValue("bounded");
            if (bounds.equals("false")) {
                endHasBounds = false;
            }
        }
        Pattern endPattern = Pattern.compile(endElement.getText());
        while (currentLine < nextSection) {
            prevLine = buffer.get(currentLine - 1);
            String text = buffer.get(currentLine++);
            Matcher lineMatcher = null;
            if (lineDefinition != null) {
                lineMatcher = linePattern.matcher(text);
            }
            Matcher endMatcher = endPattern.matcher(text);
            if (endMatcher.matches()) {
                if (!endHasBounds) {
                    currentLine -= 1;
                }
                break;
            } else {
                if (currentLine >= buffer.size()) {
                    throw new ElementNotFoundException("Not found");
                }
            }
            if (lineMatcher != null && lineMatcher.matches()) {
                if (!contentDesc.equals("discard")) {
//                    System.out.println(text);
                }
                hasEntry = false;
                if (dataDefinition != null) {
                    LedgerItem entry = new LedgerItem();
                    for (Element dataLine : dataDefinition.getChildren()) {
                        String tag = dataLine.getAttributeValue("content");
                        String regex = dataLine.getText();
                        Matcher matcher = Pattern.compile(regex).matcher(text);
                        String value = "";
                        if (matcher.find()) {
                            value = matcher.group();
                        }
                        switch (tag) {
                            case "beginningBalance":
                                beginningBalance = Float.parseFloat(value.replaceAll(",", ""));
                                break;
                            case "totalDeposits":
                                value.replaceAll(",", "");
                                totalDeposits = Float.parseFloat(value.replaceAll(",", ""));
                                break;
                            case "totalWithdrawals":
                                value.replaceAll(",", "");
                                totalWithdrawals = Float.parseFloat(value.replaceAll(",", ""));
                                break;
                            case "endingBalance":
                                value.replaceAll(",", "");
                                endingBalance = Float.parseFloat(value.replaceAll(",", ""));
                                break;
                            case "accountNumber":
                                summary.put("accountNumber", value);
                                break;
                            case "periodFrom":
                                summary.put("startDate", value);
                                break;
                            case "periodTo":
                                summary.put("endDate", value);
                                break;
                            case "date":
                                entry.setDate(value);
                                text = matcher.replaceFirst("");
                                break;
                            case "check":
                                entry.setCheckNumber(value);
                                break;
                            case "amount":
                                if (transType.equals("debit")) {
                                    value = "-" + value;
                                }
                                entry.setAmount(value);
                                text = matcher.replaceFirst("");
                                if (section.getAttributeValue("content").equals("fees")) {
                                    String amt = entry.getAmount();
                                    amt = amt.replaceAll("-", "");
                                    amt = amt.replaceAll(",", "");
                                    totalFees += Float.parseFloat(amt);
                                }
                                break;
                            case "description":
                                entry.setDescription(value);
                                break;
                        }
                    }
                    if (maxLines > 0 && ++linesProcessed == maxLines) {
                        return;
                    }
                    if (!contentDesc.equals("dailyBalance") && !contentDesc.endsWith("Info")) {
                        entries.add(entry);
                        hasEntry = true;
                    }
                }
            } else {
                if (linePattern.matcher(prevLine).matches() && hasEntry) {
                    if (!contentDesc.equals("dailyBalance")) {
                        int lastEntry = entries.size() - 1;
                        LedgerItem entry = entries.get(lastEntry);
                        entry.setDescription(entry.getDescription() + " " + text);
                        entries.set(lastEntry, entry);
                        hasEntry = false;
                    }
                }
            }

        }
    }

    @Override
    public void getAccountConfig() {
        jdomBuilder = new SAXBuilder();
        String xmlSource = cfg.getInstallDir() + cfg.getFileSep() + "pnc.xml";
        try {
            accountConfigXml = jdomBuilder.build(xmlSource);
            Element root = accountConfigXml.getRootElement();
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
