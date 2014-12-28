/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rlittle
 */
public class LedgerController {

    public Connection connection;
    public DriverManager manager;
    public String query;
    public String viewQuery;

    public LedgerController() {
        viewQuery = "select transDate,transDesc,primaryCat,subCat,amount,balance ";
        viewQuery+= "from ledger_view where transDate > :transDate;";
        
        query = "select l.transDate as Date, ";
        query += "left(l.transDesc,65) as Description,  ";
        query += "left(pcat.description,40) as Category,  ";
        query += "left(scat.description,20) as 'Sub Category',  ";
        query += "lpad(format(l.transAmt,2),9,' ') as Amount,  ";
        query += "lpad(format(l.transBal,2),9,' ') as Balance ";
        query += "from ledger l ";
        query += "inner join categories pcat on pcat.id=l.primaryCat ";
        query += "inner join distribution dist on dist.transId = l.id ";
        query += "inner join categories scat on scat.id = dist.categoryId;";
    }

    public void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection("jdbc:mysql://mustang/bank?user=rlittle");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(LedgerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
