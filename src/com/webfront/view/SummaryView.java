/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.controller.SummaryController;
import javafx.application.Platform;
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
public final class SummaryView extends Group {

    private final PieChart chart;
    private static SummaryView instance = null;
    private static SummaryController controller;
    final Label caption = new Label("");
    final Label parentCategory = new Label("");
    final Label subTitle = new Label("");
    final Label chartHeader = new Label("");
    double x, y;

    protected SummaryView() {
        super();

        controller = SummaryController.getInstance();

        DropShadow ds = new DropShadow();
        ds.setOffsetY(4.0f);
        ds.setColor(Color.color(0.0f, 0.0f, 0.0f));

        caption.setTextFill(Color.BEIGE);
        caption.setStyle("-fx-font: 24 arial;");
        caption.setEffect(ds);

        parentCategory.setTextFill(Color.CORNFLOWERBLUE);
        parentCategory.setStyle("-fx-font: 22 arial;");
        parentCategory.setTranslateX(0);

        subTitle.setText(controller.getStartDate() + " through " + controller.getEndDate());
        subTitle.setStyle("-fx-font: 18 arial;");

        chart = new PieChart();

        chart.setTitle("All Categories");
        chart.setTitleSide(Side.TOP);
        chart.setClockwise(true);
        chart.setPrefHeight(600);
        chart.setPrefWidth(1200);
        chart.setMaxHeight(USE_PREF_SIZE);
        chart.setMaxWidth(USE_PREF_SIZE);
        chart.setLegendSide(Side.LEFT);
        Bounds b = chart.getBoundsInParent();
        b = chart.getBoundsInLocal();

        subTitle.setTranslateX(500);
        subTitle.setTranslateY(650);

        buildData();
        setHandler();

    }

    public void buildData() {
        if (Platform.isFxApplicationThread()) {
            controller.buildSummary(0);
        } else {
            Platform.runLater(() -> controller.buildSummary(0));
        }
        chart.setData(controller.getDataList());
        getChildren().addAll(chart, parentCategory, caption, subTitle);
    }

    public synchronized static SummaryView getInstance() {
        if (instance == null) {
            instance = new SummaryView();
        }
        return instance;
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
                    parentCategory.setTranslateX(x - 200);
                    parentCategory.setTranslateY(y);
                    parentCategory.setText("< " + data.getName());
                    chartHeader.setText(parentCategory.getText());
                    Integer id = controller.getCatMap().get(data.getName());
                    if (controller.hasChildren(id)) {
                        chart.setData(controller.getSubCat(id));
                    } else {
                        chart.setData(controller.getDetail(id));
                    }
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
