/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.bean.DBManager;
import java.util.List;

/**
 *
 * @author rlittle
 */
public class CreditCardLedgerView extends TransactionView{
    
    public CreditCardLedgerView(int id) {
        super();
        accountId = id;
    }

    @Override
    public void setDbManager() {
        dbManager = new DBManager() {

            @Override
            public List getList(String s) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    }
    
}
