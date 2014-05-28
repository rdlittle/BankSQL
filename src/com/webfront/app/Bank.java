/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.app;

import com.webfront.app.utils.Importer;
import com.webfront.model.Stores;
import com.webfront.view.CategoryForm;
import com.webfront.view.LedgerView;
import com.webfront.view.ReceiptsView;
import com.webfront.view.StoreForm;
import com.webfront.view.StoresView;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author rlittle
 */
public class Bank extends Application {

    final int TAB_BOTTOM_MARGIN = 130;

    @Override
    public void start(Stage primaryStage) {

        Scene scene = new Scene(new VBox(), 1300, 800);

        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("_File");
        Menu fileNewMenu = new Menu("Ne_w");

        Menu editMenu = new Menu("_Edit");

        MenuItem fileImport = new MenuItem("_Import");
        MenuItem fileNewCategory = new MenuItem("_Category");
        MenuItem fileNewStore = new MenuItem("Sto_re");
        MenuItem fileExit = new MenuItem("E_xit");
        MenuItem editCategories = new MenuItem("Categories");

        fileMenu.setMnemonicParsing(true);
        fileImport.setMnemonicParsing(true);
        fileNewMenu.setMnemonicParsing(true);
        fileExit.setMnemonicParsing(true);
        fileNewMenu.getItems().addAll(fileNewCategory, fileNewStore);

        fileMenu.getItems().addAll(fileImport, fileNewMenu, fileExit);

        editMenu.setMnemonicParsing(true);
        editMenu.setMnemonicParsing(true);
        editMenu.getItems().addAll(editCategories);

        menuBar.getMenus().addAll(fileMenu, editMenu);

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

        fileImport.setOnAction(new EventHandler() {

            @Override
            public void handle(Event event) {
                Importer importer=new Importer();
                Thread t=new Thread(importer);
                t.start();
                while(t.isAlive()) {
                    try {
                        t.join(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Bank.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                System.out.println("import finished");
                ledgerView.getList().addAll(importer.getItemList());
            }
        });

        fileNewStore.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                new StoreForm(stores, new Stores()).showForm();
            }
        });

        fileNewCategory.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                new CategoryForm();
            }
        });

        fileExit.setOnAction((ActionEvent event) -> {
            System.exit(0);
        });

        editCategories.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                new CategoryForm();
            }
        });

        tabPane.getTabs().add(summaryTab);
        tabPane.getTabs().add(ledgerTab);
        tabPane.getTabs().add(storesTab);
        tabPane.getTabs().add(receiptsTab);

        Pane statusPanel = new Pane();
        statusPanel.setPrefSize(scene.getWidth(), 110);
        statusPanel.setStyle("-fx-background-color: silver; -fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 2; -fx-margin: 3px;");
        statusPanel.setPadding(new Insets(1, 5, 1, 5));
        statusPanel.getChildren().add(new Label("Status Bar"));

        ((VBox) scene.getRoot()).getChildren().add(menuBar);
        ((VBox) scene.getRoot()).getChildren().add(tabPane);
        ((VBox) scene.getRoot()).getChildren().add(statusPanel);

        ledgerView.setPrefSize(scene.getWidth(), scene.getHeight());
        ledgerView.getTable().setPrefSize(scene.getWidth(), scene.getHeight() - TAB_BOTTOM_MARGIN);

        receipts.setPrefSize(scene.getWidth(), scene.getHeight());
        receipts.getTable().setPrefSize(scene.getWidth(), scene.getHeight() - TAB_BOTTOM_MARGIN);

        stores.setPrefSize(scene.getWidth(), scene.getHeight());
        stores.getTable().setPrefSize(scene.getWidth(), scene.getHeight() - TAB_BOTTOM_MARGIN);

        receipts.setStoreList(stores.getList());
        receipts.getStoreAdded().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                receipts.getStoreAdded().setValue(Boolean.FALSE);
                stores.getList().sort(StoresView.StoreComparator);
            }
        });
        scene.getStylesheets().add("com/webfront/app/bank/css/styles.css");
        primaryStage.setTitle("Bank");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void doImport() {
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
