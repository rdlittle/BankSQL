/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.controller.SummaryController;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import static javafx.scene.layout.Region.USE_PREF_SIZE;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 *
 * @author rlittle
 */
public class SummaryView extends StackPane {

    private final PieChart chart;
    private static SummaryView view = null;
    private SummaryController controller;
    final Label caption = new Label("");

    public SummaryView() {
        super();
        controller = new SummaryController();
        controller.buildSummary();
        chart = new PieChart();
        chart.setData(controller.getDataList());
        chart.setTitle("Income to Expense Summary");
        chart.setPrefHeight(600);
        chart.setPrefWidth(1000);
        chart.setMaxHeight(USE_PREF_SIZE);
        chart.setMaxWidth(USE_PREF_SIZE);
        chart.setLegendSide(Side.LEFT);
        caption.setTextFill(Color.DARKORANGE);
        caption.setStyle("-fx-font: 24 arial;");
        getChildren().addAll(chart, caption);
    }

    public SummaryView getView() {
        return this;
    }
    
    public static SummaryView getInstance() {
        if (view == null) {
            view = new SummaryView();
            for (final PieChart.Data data : view.getChart().getData()) {
                data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
                        new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent e) {
                                view.caption.setTranslateX(e.getSceneX());
                                view.caption.setTranslateY(e.getSceneY());
                                view.caption.setText(String.valueOf(data.getPieValue()) + "%");
                            }
                        });
            }
        }
        return view;
    }

    /**
     * @return the chart
     */
    public PieChart getChart() {
        return view.chart;
    }
}
