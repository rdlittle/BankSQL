/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.webfront.bean;

import com.webfront.model.Distribution;
import com.webfront.model.Ledger;
import java.io.Serializable;
import java.util.List;
import javafx.collections.ObservableList;

/**
 *
 * @author rlittle
 */
public class DistributionManager extends DBManager implements Serializable{

    ObservableList<Distribution> list;
    
    @Override
    public List getList(String s) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public List createDistributionList(ObservableList<Ledger> inList, int startId, int endId) {
        list.clear();
        for(int counter=startId; counter<=endId; counter++) {
            Distribution dist=new Distribution();
            dist.setId(counter);
            list.add(dist);
        }
        return list;
    }

    @Override
    public ObservableList doSqlQuery(String q) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
