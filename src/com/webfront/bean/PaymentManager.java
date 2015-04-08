/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.bean;

import com.webfront.model.Payment;
import java.io.Serializable;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.persistence.Query;

/**
 *
 * @author rlittle
 */
public class PaymentManager extends DBManager<Payment> implements Serializable {

    public PaymentManager() {
    }
    
    @Override
    public ObservableList<Payment> getList(String q) {
        Query query = em.createNamedQuery("Payment.findAll");
        List<Payment> list = query.getResultList();
        ObservableList olist = FXCollections.observableList(list);
        return olist;
    }

    @Override
    public ObservableList<Payment> doSqlQuery(String q) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
