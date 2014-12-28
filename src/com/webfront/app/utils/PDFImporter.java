/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.app.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.jdom2.Content;
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
    private final String xmlSource = "/home/rlittle/tmp/pnc.xml";

    public PDFImporter(String fileName) {
        super(fileName);
        this.fileName = fileName;
    }

    @Override
    public void doImport(BufferedReader reader) throws IOException, ParseException {
        PDFTextStripper pdfStripper = new PDFTextStripper();
        BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(fileName));
        PDDocument document = PDDocument.load(inStream);
        String txtStatement = pdfStripper.getText(document);
        System.out.println(txtStatement);
        try (FileWriter writer = new FileWriter("/home/rlittle/tmp/pdfOut.txt")) {
            writer.write(txtStatement);
        }
        getConfig();

    }

    private void getConfig() {
        jdomBuilder = new SAXBuilder();
        try {
            xmlDoc = jdomBuilder.build(xmlSource);
            Element root = xmlDoc.getRootElement();
            List<Content> configContent = root.getContent();
            List<Element> childElements = root.getChildren();
            for (Element e : childElements) {
                System.out.println(e.getName());
            }
            Element header = root.getChild("header");
            for (Element e : header.getChildren()) {
                String content = e.getAttributeValue("content");
                String format = e.getAttributeValue("format");
                String text = e.getText();
                System.out.println("content type = "+content+", format = "+format+" : "+text);
            }
        } catch (JDOMException ex) {
            Logger.getLogger(PDFImporter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PDFImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
