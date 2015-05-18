/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.controller.SummaryController;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import static javafx.scene.layout.Region.USE_PREF_SIZE;
import javafx.scene.paint.Color;

/**
 *
 * @author rlittle
 */
public class SummaryView extends Group {

    private final PieChart chart;
    private static final SummaryView view = null;
    private final SummaryController controller;
    final Label caption = new Label("");
    final Label parentCategory = new Label("");
    final Label subTitle = new Label("");
    double x, y;

    public SummaryView() {
        super();

        controller = new SummaryController();
        controller.buildSummary(0);
        
        DropShadow ds = new DropShadow();
        ds.setOffsetY(4.0f);
        ds.setColor(Color.color(0.0f, 0.0f, 0.0f));

        caption.setTextFill(Color.BEIGE);
        caption.setStyle("-fx-font: 24 arial;");
        caption.setEffect(ds);

        parentCategory.setTextFill(Color.CORNFLOWERBLUE);
        parentCategory.setStyle("-fx-font: 24 arial;");
        
        subTitle.setText(controller.getStartDate()+" through "+controller.getEndDate());
        subTitle.setStyle("-fx-font: 18 arial;");

        chart = new PieChart();
        chart.setData(controller.getDataList());
        chart.setTitle("All Categories");
        chart.setTitleSide(Side.TOP);
        chart.setClockwise(true);
        chart.setPrefHeight(600);
        chart.setPrefWidth(1000);
        chart.setMaxHeight(USE_PREF_SIZE);
        chart.setMaxWidth(USE_PREF_SIZE);
        chart.setLegendSide(Side.LEFT);
        Bounds b = chart.getBoundsInParent();
        b = chart.getBoundsInLocal();
        
        subTitle.setTranslateX(500);
        subTitle.setTranslateY(650);

        getChildren().addAll(chart, parentCategory, caption, subTitle);

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
                            caption.setTranslateX(e.getSceneX());
                            caption.setTranslateY(e.getSceneY());
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
            data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED,
                    new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                            caption.setText("");
                            y = chart.boundsInParentProperty().getValue().getHeight();
                            x = chart.boundsInParentProperty().getValue().getWidth();
                            parentCategory.setTranslateX(x);
                            parentCategory.setTranslateY(y);
                            parentCategory.setText("< " + data.getName());
                            Integer id = controller.getCatMap().get(data.getName());
                            chart.setData(controller.getSubCat(id));
                            chart.setTitle(data.getName());
                        }
                    });
        }
        parentCategory.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                parentCategory.setText("");
                chart.setTitle("All Categories");
                chart.setData(controller.getDataList());
            }
        });

    }
}
