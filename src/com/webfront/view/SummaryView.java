/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.controller.SummaryController;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 *
 * @author rlittle
 */
public class SummaryView extends StackPane {

    private final PieChart chart;
    private static final SummaryView view = null;
    private final SummaryController controller;
    final Label caption = new Label("");

    public SummaryView() {
        super();
        DropShadow ds = new DropShadow();
        ds.setOffsetY(4.0f);
        ds.setColor(Color.color(0.0f, 0.0f, 0.0f));
        caption.setTextFill(Color.WHITE);
        caption.setStyle("-fx-font: 24 arial;");
        caption.setEffect(ds);
        controller = new SummaryController();
        controller.buildSummary(0);
        chart = new PieChart();
        chart.setData(controller.getDataList());
        chart.setTitle("Income to Expense Summary");
        chart.setPrefHeight(600);
        chart.setPrefWidth(1000);
        chart.setMaxHeight(USE_PREF_SIZE);
        chart.setMaxWidth(USE_PREF_SIZE);
        chart.setLegendSide(Side.LEFT);

        getChildren().addAll(chart, caption);
        setHandler();
    }

    public SummaryView getView() {
        return this;
    }

    private void setHandler() {
        for (final PieChart.Data data : chart.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED,
                    new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                            caption.setTranslateX(e.getSceneX() - 600);
                            caption.setTranslateY(e.getSceneY() - 400);
                            int pct = (int) data.getPieValue();
                            caption.setText(data.getName() + " " + Integer.toString(pct));
                        }
                    });
            data.getNode().addEventHandler(MouseEvent.MOUSE_EXITED,
                    new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                            caption.setText("");
                        }
                    });
        }
    }
}
