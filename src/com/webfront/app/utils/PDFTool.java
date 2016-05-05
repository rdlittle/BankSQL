/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.app.utils;

import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.ContentByteUtils;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfContentStreamProcessor;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rlittle
 */
public class PDFTool {

    public void extract(String src, String dest) {
        try {
            PrintWriter out = new PrintWriter(new FileOutputStream(dest));
            PrintWriter out2 = new PrintWriter(new FileOutputStream("2" + dest));
            PdfReader reader = new PdfReader(src);
            PDFRenderListener listener = new PDFRenderListener(out);
            PdfContentStreamProcessor processor = new PdfContentStreamProcessor(listener);
            PdfReaderContentParser parser = new PdfReaderContentParser(reader);
            TextExtractionStrategy strategy;
            for (int i = 2; i <= reader.getNumberOfPages(); i++) {
                strategy = parser.processContent(i, new LocationTextExtractionStrategy());
                out.println(strategy.getResultantText());
                PdfDictionary pageDict = reader.getPageN(i);
                PdfDictionary resourceDict = pageDict.getAsDict(PdfName.RESOURCES);
                processor.processContent(ContentByteUtils.getContentBytesForPage(reader, i), resourceDict);
            }
            out.flush();
            out.close();
            out2.flush();
            out2.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PDFTool.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PDFTool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void loadPdf(String src) {
        try {
            PdfReader reader = new PdfReader(src);
        } catch (IOException ex) {
            Logger.getLogger(PDFTool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
