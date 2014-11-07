/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.webfront.view;

import javafx.scene.chart.PieChart;
import javafx.scene.layout.Pane;

/**
 *
 * @author rlittle
 */
public class SummaryView extends Pane {
    
    private final PieChart chart;
    
    public SummaryView() {
        super();
        chart = new PieChart();
        chart.setPrefHeight(USE_PREF_SIZE);
        chart.setPrefWidth(USE_PREF_SIZE);
        this.getChildren().add(chart);
    }

    /**
     * @return the chart
     */
    public PieChart getChart() {
        return chart;
    }
}
