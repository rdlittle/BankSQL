/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.app;

import com.webfront.controller.BankController;
import com.webfront.model.Config;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author rlittle
 */
public class Bank extends Application {

    private final String location = "/com/webfront/app/fxml/Bank.fxml";
    private final Config config = Config.getInstance();

    @Override
    public void start(Stage primaryStage) throws Exception {
        config.getConfig();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(BankController.class.getResource(location));
        AnchorPane root = loader.<AnchorPane>load();
        BankController controller = loader.getController();
        Scene scene = new Scene(root);
        controller.getFileExit().setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                primaryStage.fireEvent(new Event(WindowEvent.WINDOW_CLOSE_REQUEST));
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("JFX Bank");
        primaryStage.setX(Double.parseDouble(config.getX()));
        primaryStage.setY(Double.parseDouble(config.getY()));
        primaryStage.setOnCloseRequest(new EventHandler() {
            @Override
            public void handle(Event event) {
                config.setWidth(Double.toString(scene.getWidth()));
                config.setHeight(Double.toString(scene.getHeight()));
                config.setX(Double.toString(primaryStage.getX()));
                config.setY(Double.toString(primaryStage.getY()));
                config.setConfig();
                Platform.exit();
            }
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
