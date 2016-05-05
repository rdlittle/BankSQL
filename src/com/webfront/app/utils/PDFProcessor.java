/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.app.utils;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.TextPosition;

/**
 *
 * @author rlittle
 */
public class PDFProcessor extends PDFTextStripper {

    public static StringBuilder tWord = new StringBuilder();
    public static String seek;
    public static String[] seekA;
    public static List wordList = new ArrayList();
    public static boolean is1stChar = true;
    public static boolean lineMatch;
    public static boolean newLine;
    public static int pageNo = 1;
    public static double lastYVal;
    public static StringBuilder line;
    public static ArrayList<String> docText;

    public PDFProcessor(String docPath) throws IOException {
        super.setSortByPosition(true);
        File input = new File(docPath);
        seek = "";
        seekA = new String[1];
        line = new StringBuilder();
        docText = new ArrayList<>();

        PDDocument document = PDDocument.load(input);
        List allPages = document.getDocumentCatalog().getAllPages();

        for (int i = 0; i < allPages.size(); i++) {
            PDPage page = (PDPage) allPages.get(i);
            PDStream contents = page.getContents();

            if (contents != null) {
                processStream(page, page.getResources(), page.getContents().getStream());
            }
            pageNo += 1;
        }
    }

    public ArrayList<String> getText() {
        return docText;
    }

    @Override
    protected void processTextPosition(TextPosition text) {
        String tChar = text.getCharacter();
//        System.out.println("String[" + text.getXDirAdj() + ","
//                + text.getYDirAdj() + " fs=" + text.getFontSize() + " xscale="
//                + text.getXScale() + " height=" + text.getHeightDir() + " space="
//                + text.getWidthOfSpace() + " width="
//                + text.getWidthDirAdj() + "]" + text.getCharacter());
//        String REGEX = "[,.\\[\\](:;!?)/]";
        char c = tChar.charAt(0);
//        lineMatch = matchCharLine(text);
        newLine = isNewLine(text);
        if (newLine) {
            if (line.length() > 0) {
                int lastCharIdx = line.length() - 1;
                char lastChar = line.charAt(lastCharIdx);
//                if (Character.isSpaceChar(lastChar)) {
//                    line.deleteCharAt(lastCharIdx);
//                }
                endWord();
                docText.add(line.toString());
            }
            line.delete(0, line.length());
        }
//        if ((!tChar.matches(REGEX)) && (!Character.isWhitespace(c))) {
//        if ((!is1stChar) && (!newLine)) {
//        if (newLine) {
//            setWordCoord(text, tChar);
//        }
//        if (!is1stChar) {
            appendChar(tChar);
//        }
        if (Character.isWhitespace(c)) {
            endWord();
        }
    }

    protected void appendChar(String tChar) {
        tWord.append(tChar);
        is1stChar = false;
    }

    protected void setWordCoord(TextPosition text, String tChar) {
//        tWord.append("(").append(pageNo).append(")[").append(roundVal(Float.valueOf(text.getXDirAdj()))).append(" : ").append(roundVal(Float.valueOf(text.getYDirAdj()))).append("] ").append(tChar);
        tWord.append(tChar);
        is1stChar = true;
    }

    protected void endWord() {
        String newWord = tWord.toString().replaceAll("[^\\x00-\\x7F]", "");
//        String sWord = newWord.substring(newWord.lastIndexOf(' ') + 1);
//        if (!"".equals(sWord)) {
        line.append(newWord);
//            wordList.add(newWord);
//        }

        tWord.delete(0, tWord.length());
        is1stChar = true;
    }

    protected boolean matchCharLine(TextPosition text) {
        Double yVal = roundVal(Float.valueOf(text.getYDirAdj()));
        if (yVal.doubleValue() == lastYVal) {
            return true;
        }
        lastYVal = yVal.doubleValue();
//        endWord();
        return false;
    }

    protected boolean isNewLine(TextPosition text) {
        Double yVal = roundVal(text.getYDirAdj());
        if (yVal == lastYVal) {
            return false;
        }
        lastYVal = yVal;
        is1stChar = true;
        return true;
    }

    protected Double roundVal(Float yVal) {
        DecimalFormat rounded = new DecimalFormat("0.0'0'");
        Double yValDub = new Double(rounded.format(yVal));
        return yValDub;
    }

}
