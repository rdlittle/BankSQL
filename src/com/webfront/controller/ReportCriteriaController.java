/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.controller;

import com.webfront.model.SearchCriteria;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author rlittle
 */
public class ReportCriteriaController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    private SearchCriteria criteria;
    String stmt;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        criteria = new SearchCriteria();
    }
    
    private void doSearch() {
        
    }
    
}
