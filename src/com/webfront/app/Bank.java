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
import com.webfront.bean.AccountManager;
import com.webfront.bean.DistributionManager;
import com.webfront.controller.SummaryController;
import com.webfront.model.Account;
import com.webfront.model.Account.AccountStatus;
import com.webfront.model.Config;
import com.webfront.model.Distribution;
import com.webfront.model.Ledger;
import com.webfront.model.Stores;
import com.webfront.view.AccountPickerForm;
import com.webfront.view.CategoryForm;
import com.webfront.view.ImportForm;
import com.webfront.view.LedgerView;
import com.webfront.view.PreferencesForm;
import com.webfront.view.ReceiptsView;
import com.webfront.view.StoreForm;
import com.webfront.view.StoresView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    public static ArrayList<Account> accountList;
    public static HashMap<Integer, LedgerView> viewList;
    TabPane tabPane;
    private static List<Tab> ledgers;
    int accountId;

    public Bank() {
        this.sdp = new SimpleDoubleProperty();
        this.progressBar = new ProgressBar(0);
        importDone = new SimpleBooleanProperty(true);
        bankName = "";
        config = Config.getInstance();
        bankName = config.getBankName();
        viewList = new HashMap<>();
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
        MenuItem fileOpen = new MenuItem("_Open");
        MenuItem fileNewCategory = new MenuItem("_Category");
        MenuItem fileNewStore = new MenuItem("Sto_re");
        MenuItem fileExit = new MenuItem("E_xit");

        MenuItem editAccounts = new MenuItem("Accounts");
        MenuItem editCategories = new MenuItem("Categories");
        MenuItem editPreferences = new MenuItem("_Preferences");

        fileMenu.setMnemonicParsing(true);
        fileOpen.setMnemonicParsing(true);
        fileImport.setMnemonicParsing(true);
        fileNewMenu.setMnemonicParsing(true);
        fileExit.setMnemonicParsing(true);
        fileNewMenu.getItems().addAll(fileNewCategory, fileNewStore);

        fileMenu.getItems().addAll(fileOpen, fileNewMenu, fileImport, new SeparatorMenuItem(), fileExit);

        editMenu.setMnemonicParsing(true);
        editMenu.setMnemonicParsing(true);
        editMenu.getItems().addAll(editAccounts, editCategories, editPreferences);

        menuBar.getMenus().addAll(fileMenu, editMenu);

        tabPane = new TabPane();

        Tab summaryTab = new Tab("Summary");
        Tab storesTab = new Tab("Stores");
        Tab receiptsTab = new Tab("Receipts");

        summaryTab.setClosable(false);
        storesTab.setClosable(false);
        receiptsTab.setClosable(false);

        setLedgers(new ArrayList<>());
        setAccounts();
        for (Account acct : accountList) {
            if (acct.getAccountStatus() != AccountStatus.CLOSED) {
                LedgerView lv = new LedgerView(acct.getId());
                lv.setPrefSize(scene.getWidth(), scene.getHeight());
                lv.getTable().setPrefSize(scene.getWidth(), scene.getHeight() - TAB_BOTTOM_MARGIN);
                viewList.put(acct.getId(), lv);
                Tab t = new LedgerTab(acct.getBankName(),acct.getId());
                t.setClosable(true);
                t.setContent(lv);
                getLedgers().add(t);
            }
        }

        List<String> importTypes = new ArrayList<>();
        importTypes.add(".txt");
        importTypes.add(".csv");

        StoresView stores = StoresView.getInstance();
        storesTab.setContent(stores);

        ReceiptsView receipts = ReceiptsView.getInstance();
        receiptsTab.setContent(receipts);

        fileOpen.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                AccountPickerForm picker = new AccountPickerForm();
                picker.showForm();
                int newId = picker.accountId;
                if (!viewList.containsKey(newId)) {
                    for (Account acct : accountList) {
                        if (acct.getId() == newId) {
                            LedgerView lv = new LedgerView(newId);
                            lv.setPrefSize(scene.getWidth(), scene.getHeight());
                            lv.getTable().setPrefSize(scene.getWidth(), scene.getHeight() - TAB_BOTTOM_MARGIN);
                            viewList.put(acct.getId(), lv);
                            Tab t = new LedgerTab(acct.getBankName(),newId);
                            t.setClosable(true);
                            t.setContent(lv);
                            getLedgers().add(t);
                            tabPane.getTabs().add(t);
                            tabPane.getSelectionModel().select(t);
                        }
                    }
                } else {
                }
            }
        });

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

        editAccounts.setOnAction(new EventHandler() {
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
        tabPane.getTabs().addAll(ledgers);
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
                    accountId = -1;
                    ImportForm importForm = ImportForm.getInstance(accountList);
                    if (importForm.selectedAccount < 0 && importForm.fileName.isEmpty()) {
                        ImportForm.setForm(null);
                        return;
                    }
                    if (importForm.selectedAccount >= 0) {
                        accountId = importForm.selectedAccount;
                    }
                    if (importForm.fileName != null && !importForm.fileName.isEmpty()) {
                        if (importForm.fileName.contains("pdf")) {
                            importer = new PDFImporter(importForm.fileName, accountId);
                        } else {
                            importer = new CSVImporter(importForm.fileName, accountId);
                        }
                    }
                    if (accountId > 0 && !importForm.fileName.isEmpty()) {
                        progressBar.setVisible(true);
                        importer.setFileName(importForm.fileName);
                        Thread t = new Thread(importer);
                        importForm.fileName = "";
                        importForm.selectedAccount = 0;
                        ImportForm.setForm(null);
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
                                LedgerView view = viewList.get(Integer.valueOf(accountId));
                                for (Ledger item : list) {
                                    view.getLedgerManager().create(item);
                                    Distribution dist = new Distribution(item);
                                    dist.setCategory(item.getPrimaryCat());
                                    distMgr.create(dist);
                                    itemsCreated += 1;
                                    progress = itemsCreated / itemCount;
                                    updateProgress(progress, 1);
                                }
                                return null;
                            }

                            @Override
                            protected void succeeded() {
                                super.succeeded();
                                updateMessage("Done!");
                                LedgerView view = viewList.get(Integer.valueOf(accountId));
                                view.getTable().getItems().clear();
                                view.getList().addAll(importer.getItemList());
                                view.getList().sort(LedgerView.LedgerComparator);
                                view.getTable().setItems(view.getList());
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

        SummaryController summaryController = SummaryController.getInstance();
        summaryController.buildSummary();
        summaryController.getView().setPrefSize(scene.getWidth(), scene.getHeight());
        summaryTab.setContent(summaryController.getView());

        scene.getStylesheets().add("com/webfront/app/bank/css/styles.css");
        primaryStage.setTitle("Bank");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setAccounts() {
        accountList = new ArrayList<>();
        AccountManager am = new AccountManager();
        ObservableList<Account> l = FXCollections.observableArrayList(am.getList("Account.findAll"));
        l.stream().forEach((acct) -> {
            accountList.add(acct);
        });
    }

    public ArrayList<Account> getAccountList() {
        return accountList;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * @return the ledgers
     */
    public static List<Tab> getLedgers() {
        return ledgers;
    }

    /**
     * @param ledgers the ledgers to set
     */
    public void setLedgers(List<Tab> ledgers) {
        this.ledgers = ledgers;
    }
    
    static class LedgerTab extends Tab {
        private Integer accountId;
        
        public LedgerTab(String name,Integer id) {
            super(name);
            this.accountId=id;
            this.setOnClosed(new EventHandler() {
                @Override
                public void handle(Event event) {
                    viewList.remove(LedgerTab.this.accountId);
                    getLedgers().remove(LedgerTab.this);
                }
            });
            
        }
    }

}
