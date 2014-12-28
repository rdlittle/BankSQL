/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.app;

import com.webfront.app.utils.CSVImporter;
import com.webfront.app.utils.Importer;
import com.webfront.app.utils.PDFImporter;
import com.webfront.app.utils.StringUtil;
import com.webfront.bean.DistributionManager;
import com.webfront.controller.SummaryController;
import com.webfront.model.Config;
import com.webfront.model.Distribution;
import com.webfront.model.Ledger;
import com.webfront.model.Stores;
import com.webfront.view.CategoryForm;
import com.webfront.view.LedgerView;
import com.webfront.view.PreferencesForm;
import com.webfront.view.ReceiptsView;
import com.webfront.view.StoreForm;
import com.webfront.view.StoresView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
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
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
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
    ResourceBundle bundle;
    Thread importThread;
    SimpleBooleanProperty importDone;
    Importer importer;
    Importer pdfImporter;
    String bankName;
    String tmpDir;
    private final Config config;
    private final String defaultConfig = ".bankSQL";

    public Bank() {
        this.sdp = new SimpleDoubleProperty();
        this.progressBar = new ProgressBar(0);
        importDone = new SimpleBooleanProperty(true);
        bankName = "";
        config = Config.getInstance();
        bankName = config.getBankName();
        this.importer = new CSVImporter("", bankName);
        config.setConfig();
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
        MenuItem editPreferences = new MenuItem("_Preferences");

        fileMenu.setMnemonicParsing(true);
        fileImport.setMnemonicParsing(true);
        fileNewMenu.setMnemonicParsing(true);
        fileExit.setMnemonicParsing(true);
        fileNewMenu.getItems().addAll(fileNewCategory, fileNewStore);

        fileMenu.getItems().addAll(fileNewMenu, fileImport, new SeparatorMenuItem(), fileExit);

        editMenu.setMnemonicParsing(true);
        editMenu.setMnemonicParsing(true);
        editMenu.getItems().addAll(editCategories, editPreferences);

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

        List<String> importTypes = new ArrayList<>();
        importTypes.add(".txt");
        importTypes.add(".csv");
        LedgerView ledgerView = LedgerView.getInstance();
        ledgerTab.setContent(ledgerView);

        StoresView stores = StoresView.getInstance();
        storesTab.setContent(stores);

        ReceiptsView receipts = ReceiptsView.getInstance();
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

        editPreferences.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                PreferencesForm prefs = PreferencesForm.getInstance(config);
                prefs.hasChanged.addListener(new InvalidationListener() {
                    @Override
                    public void invalidated(Observable observable) {
                        config.setConfig();
                    }
                });
                prefs.showForm();
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
                if (importDone.getValue() == false) {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Select statement to import");
                    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files (*.txt) (*.csv)", "*.txt", "*.csv"));
                    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf"));
                    File selectedFile = fileChooser.showOpenDialog(primaryStage);
                    if (selectedFile != null) {
                        if (selectedFile.getAbsoluteFile().getName().contains("pdf")) {
                            importer = new PDFImporter(selectedFile.getAbsolutePath());
                        }
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
                    Group summary = new Group();
                    HBox hbox = new HBox();
                    VBox labelsBox = new VBox();
                    VBox valuesBox = new VBox();
                    ArrayList<Label> labels = new ArrayList<>();
                    ArrayList<Label> values = new ArrayList<>();

                    labels.add(new Label("Start date :"));
                    labels.add(new Label("End date :"));
                    labels.add(new Label("Beginning balance : "));
                    labels.add(new Label("Total deposits : "));
                    labels.add(new Label("Total withdrawals : "));
                    labels.add(new Label("Total checks : "));
                    labels.add(new Label("Total fees :"));
                    labels.add(new Label("Ending balance : "));

                    values.add(new Label(importer.startDate));
                    values.add(new Label(importer.endDate));
                    values.add(new Label(StringUtil.toCurrency(importer.beginningBalance.toString())));
                    values.add(new Label(StringUtil.toCurrency(importer.totalDeposits.toString())));
                    values.add(new Label(StringUtil.toCurrency(importer.totalWithdrawals.toString())));
                    values.add(new Label(StringUtil.toCurrency(importer.totalChecks.toString())));
                    values.add(new Label(StringUtil.toCurrency(importer.totalFees.toString())));

                    values.add(new Label(StringUtil.toCurrency(importer.endingBalance.toString())));

                    labelsBox.getChildren().addAll(labels);
                    valuesBox.getChildren().addAll(values);

                    labelsBox.setPadding(new Insets(0.0, 0.0, 20.0, 0.0));
                    valuesBox.setAlignment(Pos.CENTER_RIGHT);
                    valuesBox.setPadding(new Insets(0.0, 0.0, 20.0, 0.0));

                    hbox.getChildren().addAll(labelsBox, valuesBox);
                    hbox.setPadding(new Insets(10.0, 20.0, 0.0, 10.0));

                    summary.getChildren().add(hbox);
                    summaryTab.setContent(summary);
                }
            }
        });

        //summaryTab.setContent(stores);
        SummaryController summaryController = SummaryController.getInstance();
        summaryController.buildSummary();
        summaryController.getView().setPrefSize(scene.getWidth(), scene.getHeight());
        summaryTab.setContent(summaryController.getView());

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
