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

/**
 *
 * @author rlittle
 */
public class PDFImporter extends Importer {

    private String txtOutput;
    private final Config cfg = Config.getInstance();
    private BufferedReader txtReader;
    Float lastBalance;
    Integer currentLine;
    public static List wordList = new ArrayList();
    public static boolean is1stChar = true;
    public static boolean lineMatch;
    public static int pageNo = 1;
    public static double lastYVal;

    class ElementNotFoundException extends Exception {

        public ElementNotFoundException(String msg) {
            super(msg);
        }
    };

    ArrayList<LedgerItem> entries;
    HashMap<Integer, String> buffer;
    HashMap<String, Integer> markers;
    TreeSet sectionMarks;
    String sectionName;
    String pageBreakStart;
    String pageBreakEnd;
    boolean hasPageBreak;
    Pattern pageBreakStartPattern;
    Pattern pageBreakEndPattern;

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
        /*
        Extract the currTextual data from the PDF
         */

        PDFTextStripper pdfStripper = new PDFTextStripper();
        BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(fileName));
        PDDocument document = PDDocument.load(inStream);
        txtOutput = pdfStripper.getText(document);
        try (FileWriter writer = new FileWriter(cfg.getTmpDir() + cfg.getFileSep() + "pdfOut.txt")) {
            writer.write(txtOutput);
            writer.close();
            txtReader = new BufferedReader(new FileReader(cfg.getTmpDir() + cfg.getFileSep() + "pdfOut.txt"));
            String currText = "";
            while (currText != null) {
                currText = txtReader.readLine();
                buffer.put(currentLine++, currText);
            }
        }
        getAccountConfig();
        currentLine = 0;
        Element root = accountConfigXml.getRootElement();
        int maxLines = buffer.size() - 3;
        int markedLine = 0;
        pageBreakStart = "";
        pageBreakEnd = "";
        hasPageBreak = false;
        // Scan the output and mark the start of each section
        for (Element el : root.getChildren()) {
            for (Element section : el.getChildren()) {
                sectionName = section.getAttributeValue("name");
                Element startElement = section.getChild("start");
                Element endElement = section.getChild("end");
                if (sectionName.equals("pageLayout")) {
                    Element e = section.getChild("pageBreak");
                    if (e == null) {
                        break;
                    }
                    startElement = e.getChild("start");
                    endElement = e.getChild("end");
                    if (startElement != null) {
                        pageBreakStart = startElement.getText();
                        pageBreakStartPattern = Pattern.compile(pageBreakStart);
                    }
                    if (endElement != null) {
                        pageBreakEnd = endElement.getText();
                        pageBreakEndPattern = Pattern.compile(pageBreakEnd);
                    }
                    if (pageBreakEnd.isEmpty()) {
                        pageBreakStart = "";
                        pageBreakStartPattern = null;
                    }
                    if (!pageBreakStart.isEmpty() && !pageBreakEnd.isEmpty()) {
                        hasPageBreak = true;
                    }
                    break;
                }
                if (startElement != null) {

                    boolean endHasBounds = true;
                    if (endElement.getAttribute("bounded") != null) {
                        String bounds = endElement.getAttributeValue("bounded");
                        if (bounds.equals("false")) {
                            endHasBounds = false;
                        }
                    }
                    Pattern linePattern = Pattern.compile(startElement.getText());
                    String currText = "";
                    boolean elementFound = false;
                    while (currentLine < maxLines) {
                        currText = buffer.get(currentLine++);
                        currText = currText.replaceAll("(<\\/?.+\\/?>)", "");
                        if (linePattern.matcher(currText).matches()) {
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
            // Top level elements: header, summary & detail
            int offset = 0;
            if (element.getAttribute("offset") != null) {
                try {
                    offset += element.getAttribute("offset").getIntValue();
                } catch (DataConversionException ex) {
                    Logger.getLogger(PDFImporter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            // Sections: checks, deposits, withdrawals, etc.
            for (Element section : element.getChildren()) {
                sectionName = (section.getAttribute("name") == null ? "" : section.getAttributeValue("name"));
                if (markers.containsKey(sectionName)) {
                    currentLine = markers.get(sectionName);
                    try {
                        processSection(section);
                    } catch (ElementNotFoundException ex) {
                        Logger.getLogger(PDFImporter.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
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
        int offset = 0;
        int maxLines = 0;
        int linesProcessed = 0;
        int wrapField = -1;

        Element startElement = section.getChild("start");
        Element endElement = section.getChild("end");
        Element dataDefinition = section.getChild("data");
        Element lineDefinition = section.getChild("line");
        Element lineStart = null;
        Element lineEnd = null;

        Pattern linePattern = null;

        String transType = (section.getAttribute("type") == null ? "info" : section.getAttributeValue("type"));
        String prevText;
        String currText;
        String nextText;

        int nextSection = buffer.size() - 1;
        if (sectionMarks.higher(currentLine) != null) {
            nextSection = (int) sectionMarks.higher(currentLine);
        }

        if (startElement.getAttributeValue("offset") != null) {
            offset += Integer.parseInt(startElement.getAttributeValue("offset"));
        }

        if (lineDefinition != null) {
            linePattern = Pattern.compile(lineDefinition.getText());
            if (lineDefinition.getAttribute("offset") != null) {
                offset += Integer.parseInt(lineDefinition.getAttributeValue("offset"));
            }
            if (lineDefinition.getAttribute("wrapField") != null) {
                wrapField = Integer.parseInt(lineDefinition.getAttributeValue("wrapField"));
            }
        }
        currentLine += offset;

        Pattern endPattern = Pattern.compile(endElement.getText());

        while (currentLine < nextSection) {
            prevText = buffer.get(currentLine - 1);
            currText = buffer.get(currentLine);
            nextText = buffer.get(currentLine + 1);

            Matcher lineMatcher = null;
            if (lineDefinition != null) {
                if (linePattern != null) {
                    lineMatcher = linePattern.matcher(currText);
                }
            }
            Matcher endMatcher = endPattern.matcher(currText);

            int fieldCount = 1;
            if (dataDefinition != null) {
                if (dataDefinition.getAttribute("fields") != null) {
                    fieldCount = Integer.parseInt(dataDefinition.getAttribute("fields").getValue());
                }
                if (dataDefinition.getChild("start") != null) {
                    lineStart = dataDefinition.getChild("start");
                }
                if (dataDefinition.getChild("end") != null) {
                    lineEnd = dataDefinition.getChild("end");
                }
            }

            /*
            pageBreak 
            Relies on there being text that indicates the end one page and the start of another
            "start" refers to text at the end of a page
            "end" refers to text at the start of the next page
            Example: "continued on next page" (start) "page 2" (end)
             */
            if (hasPageBreak && pageBreakStartPattern.matcher(currText).matches()) {
                while (!pageBreakEndPattern.matcher(currText).matches()) {
                    currentLine++;
                    prevText = buffer.get(currentLine - 1);
                    currText = buffer.get(currentLine);
                    nextText = buffer.get(currentLine + 1);
                }
            }

            if (lineMatcher != null) {
                if (fieldCount > 1) {
                    if (!lineMatcher.matches() && offset > 0) {
                        // Look for text that starts with field 0
                        if (dataDefinition != null) {
                            boolean hasStart = false;
                            boolean hasEnd = false;
                            if (lineStart != null) {
                                Pattern pStart = Pattern.compile(lineStart.getText());
                                if (pStart.matcher(currText).find()) {
                                    hasStart = true;
                                }
                            }
                            if (lineEnd != null) {
                                Pattern pEnd = Pattern.compile(lineEnd.getText());
                                if (pEnd.matcher(currText).matches()) {
                                    hasEnd = true;
                                } else if (hasStart && pEnd.matcher(nextText).find()) {
                                    buffer.put(currentLine + 1, currText + " " + nextText);
                                    buffer.put(currentLine, "");
                                }
                            }
                            
                            if (lineStart == null && lineEnd == null) {
                                buffer.put(currentLine + 1, currText + " " + nextText);
                                buffer.put(currentLine, "");
                            }
                        }
                    }
                }
            }

            if (lineMatcher != null && lineMatcher.matches()) {
//                System.out.println(currText);
                if (dataDefinition != null) {
                    LedgerItem entry = new LedgerItem();
                    for (Element dataLine : dataDefinition.getChildren()) {
                        String tag = dataLine.getAttributeValue("name") == null ? "" : dataLine.getAttributeValue("name");
                        String regex = dataLine.getText();
                        Matcher matcher = Pattern.compile(regex).matcher(currText);
                        int fieldNumber = 0;
                        if (dataLine.getAttribute("number") != null) {
                            fieldNumber = Integer.parseInt(dataLine.getAttributeValue("number"));
                        }
                        String value = "";
                        if (matcher.find()) {
                            value = matcher.group();
                        }
                        if (fieldNumber == wrapField) {
                            if (linePattern != null && hasPageBreak) {
                                if (!linePattern.matcher(nextText).matches()) {
                                    if (pageBreakStartPattern.matcher(nextText).matches()) {
                                        while (!pageBreakEndPattern.matcher(currText).matches()) {
                                            currentLine++;
                                            prevText = buffer.get(currentLine - 1);
                                            currText = buffer.get(currentLine);
                                            nextText = buffer.get(currentLine + 1);
                                        }
                                        if (pageBreakEndPattern.matcher(nextText).matches()) {
                                            currentLine += 2;
                                        }
//                                        break;
                                    }
                                    value += " " + nextText;
                                    currentLine++;
                                }
                            }
                        }
                        switch (tag) {
                            case "beginningBalance":
                                beginningBalance = Float.parseFloat(value.replaceAll(",", ""));
                                break;
                            case "totalDeposits":
                                value = value.replaceAll(",", "");
                                totalDeposits = Float.parseFloat(value.replaceAll(",", ""));
                                break;
                            case "totalWithdrawals":
                                value = value.replaceAll(",", "");
                                totalWithdrawals = Float.parseFloat(value.replaceAll(",", ""));
                                break;
                            case "endingBalance":
                                value = value.replaceAll(",", "");
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
                                currText = matcher.replaceFirst("");
                                break;
                            case "check":
                                entry.setDescription("Check # " + value);
                                entry.setCheckNumber(value);
                                break;
                            case "amount":
                                if (transType.equals("debit")) {
                                    value = "-" + value;
                                }
                                entry.setAmount(value);
                                currText = matcher.replaceFirst("");
                                if (section.getAttributeValue("name").equals("fees")) {
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
                    if (!transType.equals("info")) {
                        entries.add(entry);
                    }
                }
            }
            currentLine++;
        }
    }

    @Override
    public void getAccountConfig() {
        super.getAccountConfig();
        Element root = accountConfigXml.getRootElement();
        List<Content> configContent = root.getContent();
        List<Element> childElements = root.getChildren();
        Element header = root.getChild("header");
        for (Element e : header.getChildren()) {
            String content = e.getAttributeValue("name");
            String format = e.getAttributeValue("format");
            String currText = e.getText();
        }
    }
}
