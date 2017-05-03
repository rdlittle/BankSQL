/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.controller;

import java.awt.Canvas;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author rlittle
 */
public class PDFViewer extends AnchorPane {

    @FXML
    private Canvas pdfMarker;

    @FXML
    private ImageView pdfView;

    private static PDFViewer instance;

    Stage stage;

    private PDFViewer(String src) {

    }

    public static PDFViewer getInstance(String src) {
        if (instance == null) {
            instance = new PDFViewer(src);
            URL location = instance.getClass().getResource("/com/webfront/app/fxml/PDFViewer.fxml");
            ResourceBundle resources = ResourceBundle.getBundle("com.webfront.app.bank");
            FXMLLoader loader = new FXMLLoader(location, resources);

            loader.setRoot(instance);
            loader.setController(instance);
            instance.stage = new Stage();
            Scene scene = new Scene(instance);
            instance.stage.setScene(scene);
            instance.stage.showAndWait();
            try {
                loader.load();
            } catch (IOException ex) {
                Logger.getLogger(PDFViewer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return instance;
    }

    @FXML
    void endDraw(MouseEvent event) {
        double x = event.getSceneX();
        double y = event.getSceneY();
    }

    @FXML
    void startDraw(MouseEvent event) {
        double x = event.getSceneX();
        double y = event.getSceneY();
    }
}
