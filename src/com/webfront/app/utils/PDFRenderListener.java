/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.app.utils;

import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import java.io.PrintWriter;

/**
 *
 * @author rlittle
 */
public class PDFRenderListener implements RenderListener {
    
    protected PrintWriter out;

    public PDFRenderListener(PrintWriter out) {
        this.out=out;
    }
    
    @Override
    public void beginTextBlock() {
        out.print("<");
    }

    @Override
    public void renderText(TextRenderInfo renderInfo) {
        out.print("(");
        out.print(renderInfo.getText());
        out.print(")");
    }

    @Override
    public void endTextBlock() {
        out.print(">");
    }

    @Override
    public void renderImage(ImageRenderInfo renderInfo) {
        
    }
    
}
