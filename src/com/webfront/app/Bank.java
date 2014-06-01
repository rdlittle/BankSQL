/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.app;

import com.webfront.app.utils.Importer;
import com.webfront.bean.DistributionManager;
import com.webfront.model.Distribution;
import com.webfront.model.Ledger;
import com.webfront.model.Stores;
import com.webfront.view.CategoryForm;
import com.webfront.view.LedgerView;
import com.webfront.view.ReceiptsView;
import com.webfront.view.StoreForm;
import com.webfront.view.StoresView;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author rlittle
 */
public class Bank extends Application {

    final int TAB_BOTTOM_MARGIN = 130;
    final ProgressBar progressBar;
    final SimpleDoubleProperty sdp;
    Thread importThread;
    SimpleBooleanProperty importDone;
    Importer importer;

    public Bank() {
        this.sdp = new SimpleDoubleProperty();
        this.progressBar = new ProgressBar(0);
        importDone = new SimpleBooleanProperty(true);
        this.importer = new Importer("");
    }

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

        fileMenu.getItems().addAll(fileNewMenu, fileImport, new SeparatorMenuItem(), fileExit);

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
                importDone.setValue(Boolean.FALSE);
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
                new CategoryForm().showForm();
            }
        });

        fileExit.setOnAction((ActionEvent event) -> {
            System.exit(0);
        });

        editCategories.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                new CategoryForm().showForm();
            }
        });

        tabPane.getTabs().add(summaryTab);
        tabPane.getTabs().add(ledgerTab);
        tabPane.getTabs().add(storesTab);
        tabPane.getTabs().add(receiptsTab);

        Pane statusPanel = new Pane();
        statusPanel.setPrefSize(scene.getWidth(), 110);
        statusPanel.setPadding(new Insets(1, 5, 1, 5));
        statusPanel.getChildren().add(progressBar);
        progressBar.setPrefWidth(scene.getWidth() / 2);
        progressBar.setVisible(false);

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
        receipts.getStoreAdded().addListener((Observable observable) -> {
            receipts.getStoreAdded().setValue(Boolean.FALSE);
            stores.getList().sort(StoresView.StoreComparator);
        });

        importDone.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                System.out.println("importDone value has change to "+importDone.getValue());
                if (importDone.getValue() == false) {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Select statement to import");
                    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
                    File selectedFile = fileChooser.showOpenDialog(primaryStage);
                    if (selectedFile != null) {
                        progressBar.setVisible(true);
                        String fileName = selectedFile.getAbsolutePath();
                        importer.setFileName(fileName);
                        Thread t = new Thread(importer);
                        t.start();
                        while (t.isAlive()) {
                            try {
                                t.join(1000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(Bank.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        Task<Void> importTask = new Task<Void>() {
                            @Override
                            protected Void call() throws Exception {
                                ArrayList<Ledger> list = importer.getItemList();
                                DistributionManager distMgr = new DistributionManager();
                                Double itemCount = (double) list.size();
                                Double progress = (double) 0;
                                Double itemsCreated = (double) 0;
                                for (Ledger item : list) {
                                    ledgerView.getLedgerManager().create(item);
                                    Distribution dist = new Distribution(item);
                                    dist.setCategory(item.getPrimaryCat());
                                    distMgr.create(dist);
                                    itemsCreated += 1;
                                    progress = itemsCreated / itemCount;
                                    updateProgress(progress, 1);
                                    //System.out.println(progressBar.getProgress() + " (" + itemsCreated + " of " + itemCount + ")");
                                }
                                return null;
                            }

                            @Override
                            protected void succeeded() {
                                super.succeeded();
                                updateMessage("Done!");
                                System.out.println("importTask.succeeded():  Thread is done");
                                ledgerView.getTable().getItems().clear();
                                ledgerView.getList().addAll(importer.getItemList());
                                ledgerView.getList().sort(LedgerView.LedgerComparator);
                                ledgerView.getTable().setItems(ledgerView.getList());
                                progressBar.progressProperty().unbind();
                                progressBar.setProgress(0);
                                progressBar.setVisible(false);
                                importDone.setValue(Boolean.TRUE);
                            }

                            @Override
                            protected void cancelled() {
                                super.cancelled();
                                updateMessage("Cancelled!");
                            }

                            @Override
                            protected void failed() {
                                super.failed();
                                updateMessage("Failed!");
                            }
                        };
                        progressBar.progressProperty().bind(importTask.progressProperty());
                        new Thread(importTask).start();
                    }
                } else {
                    VBox labels = new VBox();
                    Group summary = new Group();
                    labels.getChildren().add(new Label("Beginning balance on " + importer.startDate + ": " + importer.beginningBalance.toString()));
                    labels.getChildren().add(new Label("Total deposits: " + importer.totalDeposits.toString()));
                    labels.getChildren().add(new Label("Total withdrawals: " + importer.totalWithdrawals.toString()));
                    labels.getChildren().add(new Label("Total checks: " + importer.totalChecks.toString()));
                    labels.getChildren().add(new Label("Total fees: " + importer.totalFees.toString()));
                    labels.getChildren().add(new Label("Ending balance on " + importer.endDate + ": " + importer.endingBalance.toString()));
                    summary.getChildren().add(labels);
                    summaryTab.setContent(summary);     
                }
            }
        });

        scene.getStylesheets().add("com/webfront/app/bank/css/styles.css");
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
