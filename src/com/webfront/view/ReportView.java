/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 *
 * @author rlittle
 */
public class ReportView {

    private final String report;
    private final WebView webView;
    private final Stage stage;
    private final Scene scene;

    public ReportView(String rpt) {
        report = rpt;
        stage = new Stage();
        Pane pane = new Pane();
        scene = new Scene(pane);
        webView = new WebView();
        pane.getChildren().add(webView);
        stage.setScene(scene);

    }

    public void displayReport() {
        webView.getEngine().loadContent(report, "text/html");
        stage.showAndWait();
    }

}
