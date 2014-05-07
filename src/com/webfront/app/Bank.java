/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.app;

import com.webfront.view.LedgerView;
import com.webfront.view.ReceiptsView;
import com.webfront.view.StoresView;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author rlittle
 */
public class Bank extends Application {

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(new VBox(), 1300, 800);

        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("_File");
        MenuItem fileExit = new MenuItem("E_xit");
        fileMenu.setMnemonicParsing(true);
        fileExit.setMnemonicParsing(true);

        fileExit.setOnAction((ActionEvent event) -> {
            System.exit(0);
        });

        fileMenu.getItems().add(fileExit);
        menuBar.getMenus().add(fileMenu);

        TabPane tabPane = new TabPane();

        Tab summaryTab = new Tab("Summary");
        Tab ledgerTab = new Tab("Ledger");
        Tab storesTab = new Tab("Stores");
        Tab receiptsTab = new Tab("Receipts");

        summaryTab.setClosable(false);
        ledgerTab.setClosable(false);
        storesTab.setClosable(false);
        receiptsTab.setClosable(false);

        LedgerView ledgerView = new LedgerView();
        ledgerTab.setContent(ledgerView);

        StoresView stores = new StoresView();
        storesTab.setContent(stores);

        ReceiptsView receipts = new ReceiptsView();
        receiptsTab.setContent(receipts);

        tabPane.getTabs().add(summaryTab);
        tabPane.getTabs().add(ledgerTab);
        tabPane.getTabs().add(storesTab);
        tabPane.getTabs().add(receiptsTab);

        ((VBox) scene.getRoot()).getChildren().add(menuBar);
        ((VBox) scene.getRoot()).getChildren().add(tabPane);

        ledgerView.setPrefSize(scene.getWidth(), scene.getHeight());
        ledgerView.getTable().setPrefSize(scene.getWidth(), scene.getHeight() - 100);

        receipts.setStoreList(stores.getList());
        receipts.getStoreAdded().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                receipts.getStoreAdded().setValue(Boolean.FALSE);
                stores.getList().sort(StoresView.StoreComparator);
            }
        });
        
        primaryStage.setTitle("Bank");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
