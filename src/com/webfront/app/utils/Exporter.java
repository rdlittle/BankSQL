/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.app.utils;

import com.webfront.model.LedgerItem;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rlittle
 */
public abstract class Exporter implements Runnable {
    private ArrayList<LedgerItem> list;
    private File outputFile;
    
    public Exporter(File f) {
        outputFile = f;
    }
    
    @Override
    public void run() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            for (LedgerItem item : list) {
                
            }
        } catch (IOException ex) {
            Logger.getLogger(Exporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
