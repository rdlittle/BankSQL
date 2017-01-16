/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.app.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rlittle
 */
public class CSVExporter extends Exporter {
    
    public CSVExporter(File f) {
        super(f);
    }

    @Override
    public void doExport() {
        try {
            PrintWriter writer = new PrintWriter(outputFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CSVExporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected Void call() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
