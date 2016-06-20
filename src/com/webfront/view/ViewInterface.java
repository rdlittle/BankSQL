/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.bean.LedgerManager;
import com.webfront.bean.PaymentManager;
import com.webfront.model.Ledger;
import com.webfront.model.Payment;
import com.webfront.model.Stores;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;

/**
 *
 * @author rlittle
 */
public interface ViewInterface {
    public BooleanProperty getStoreAdded();
    public ObservableList<Stores> getStoreList();
    public Control getTable();
    public PaymentManager getPaymentManager();
    public ObservableList<Payment> getList();
    public void updateItem(Payment p);
    public void removeItem(Payment p);
    public void updateTrans(int idx);
    public Ledger getLedgerItem(String id);
    public LedgerManager getLedgerManager();
}
