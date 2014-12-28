/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.bean;

import com.webfront.model.Receipts;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.persistence.Query;

/**
 *
 * @author rlittle
 */
public class ReceiptsManager extends DBManager<Receipts> {

    @Override
    public ObservableList<Receipts> getList(String q) {
        Query query = em.createNamedQuery("Receipts.findAll");
        List<Receipts> list = query.getResultList();
        ObservableList olist = FXCollections.observableList(list);
        return olist;
    }

}
