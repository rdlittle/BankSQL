/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.app;

import com.webfront.app.utils.Importer;
import com.webfront.bean.AccountManager;
import com.webfront.model.Account;
import com.webfront.model.Account.AccountStatus;
import com.webfront.model.Config;
import com.webfront.model.Stores;
import com.webfront.view.AccountPickerForm;
import com.webfront.view.CategoryForm;
import com.webfront.view.ImportForm;
import com.webfront.view.LedgerView;
import com.webfront.view.PreferencesForm;
import com.webfront.view.PaymentView;
import com.webfront.view.StoreForm;
import com.webfront.view.StoresView;
import com.webfront.view.SummaryView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
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
import javafx.stage.WindowEvent;

/**
 *
 * @author rlittle
 */
public class Bank extends Application {

    final int TAB_BOTTOM_MARGIN = 130;

    private Stage primaryStage;
    public Scene scene;

    private final StoresView stores;

    private final Menu fileMenu = new Menu("_File");
    private final Menu fileNewMenu = new Menu("Ne_w");

    private final Menu editMenu = new Menu("_Edit");

    private final MenuItem fileImport = new MenuItem("_Import");
    private final MenuItem fileOpen = new MenuItem("_Open");
    private final MenuItem fileRefresh = new MenuItem("_Refresh");
    private final MenuItem fileNewCategory = new MenuItem("_Category");
    private final MenuItem fileNewStore = new MenuItem("Sto_re");
    private final MenuItem fileExit = new MenuItem("E_xit");

    private final MenuItem editAccounts = new MenuItem("Accounts");
    private final MenuItem editCategories = new MenuItem("Categories");
    private final MenuItem editPreferences = new MenuItem("_Preferences");
    private final MenuItem editRebalance = new MenuItem("_Rebalance");

    final ProgressBar progressBar;

    private final Config config;
    private final String defaultConfig = ".bankSQL";

    private static List<Tab> ledgers;

    public static ArrayList<Account> accountList;
    public static HashMap<Integer, LedgerView> viewList;

    public final SimpleBooleanProperty importDone;
    public final SimpleIntegerProperty accountNum;
    public final SimpleBooleanProperty isLedger;
    public final SimpleBooleanProperty isRebalance;
    private final SimpleIntegerProperty backgroundActive = new SimpleIntegerProperty(0);

    ResourceBundle bundle;
    Thread importThread;
    Importer importer;
    String tmpDir;
    private final TabPane tabPane;

    int accountId;

    public Bank() {
        this.importDone = new SimpleBooleanProperty(false);
        this.tabPane = new TabPane();
        this.progressBar = new ProgressBar(0);
        accountNum = new SimpleIntegerProperty();
        isLedger = new SimpleBooleanProperty(false);
        isRebalance = new SimpleBooleanProperty(false);
        config = Config.getInstance();
        viewList = new HashMap<>();
        config.getConfig();
        stores = StoresView.getInstance();
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        scene = new Scene(new VBox(), Double.parseDouble(config.getWidth()), Double.parseDouble(config.getHeight()));
        primaryStage.setX(Double.parseDouble(config.getX()));
        primaryStage.setY(Double.parseDouble(config.getY()));

        MenuBar menuBar = new MenuBar();

        fileMenu.setMnemonicParsing(true);
        fileOpen.setMnemonicParsing(true);
        fileImport.setMnemonicParsing(true);
        fileNewMenu.setMnemonicParsing(true);
        fileExit.setMnemonicParsing(true);
        fileNewMenu.getItems().addAll(fileNewCategory, fileNewStore);

        fileMenu.getItems().addAll(fileOpen, fileRefresh, fileNewMenu, fileImport, new SeparatorMenuItem(), fileExit);

        editMenu.setMnemonicParsing(true);
        editMenu.getItems().addAll(editAccounts, editCategories, editPreferences, editRebalance);

        menuBar.getMenus().addAll(fileMenu, editMenu);

        Tab summaryTab = new Tab("Summary");
        Tab storesTab = new Tab("Stores");
        Tab paymentTab = new Tab("Payments");

        summaryTab.setClosable(false);
        storesTab.setClosable(false);
        paymentTab.setClosable(false);

        setLedgers(new ArrayList<>());
        setAccounts();
        for (Account acct : accountList) {
            if (acct.getAccountStatus() != AccountStatus.CLOSED) {
                addLedger(acct);
            }
        }

        List<String> importTypes = new ArrayList<>();
        importTypes.add(".txt");
        importTypes.add(".csv");

        storesTab.setContent(stores);

        PaymentView payment = PaymentView.getInstance();
        paymentTab.setContent(payment);

        editRebalance.disableProperty().bindBidirectional(isLedger);
        fileRefresh.disableProperty().bindBidirectional(isLedger);

        summaryTab.setContent(SummaryView.getInstance());

        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {
                isLedger.set(!"LedgerView".equalsIgnoreCase(newTab.getContent().getClass().getSimpleName()));
            }
        });

        tabPane.getTabs().add(summaryTab);
        tabPane.getTabs().addAll(ledgers);
        tabPane.getTabs().add(storesTab);
        tabPane.getTabs().add(paymentTab);

        Pane statusPanel = new Pane();
        statusPanel.setPrefSize(scene.getWidth(), 100);
        statusPanel.setMaxHeight(100);
        statusPanel.setPadding(new Insets(1, 5, 1, 5));
        statusPanel.getChildren().add(progressBar);
        progressBar.setPrefWidth(scene.getWidth() / 2);
        progressBar.setVisible(false);
        progressBar.visibleProperty().bind(backgroundActive.greaterThan(0));

        ((VBox) scene.getRoot()).getChildren().add(menuBar);
        ((VBox) scene.getRoot()).getChildren().add(tabPane);
        ((VBox) scene.getRoot()).getChildren().add(statusPanel);

        payment.setPrefSize(scene.getWidth(), scene.getHeight());
        payment.getTable().setPrefSize(scene.getWidth(), scene.getHeight() - TAB_BOTTOM_MARGIN);

        stores.setPrefSize(scene.getWidth(), scene.getHeight());
        stores.getTable().setPrefSize(scene.getWidth(), scene.getHeight() - TAB_BOTTOM_MARGIN);

        payment.setStoreList(stores.getList());
        payment.getStoreAdded().addListener((Observable observable) -> {
            payment.getStoreAdded().setValue(Boolean.FALSE);
            stores.getList().sort(StoresView.StoreComparator);
        });

        scene.getStylesheets().add("com/webfront/app/bank/css/styles.css");
        primaryStage.setTitle("Bank");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(new EventHandler() {
            @Override
            public void handle(Event event) {
                config.setWidth(Double.toString(scene.getWidth()));
                config.setHeight(Double.toString(scene.getHeight()));
                config.setX(Double.toString(primaryStage.getX()));
                config.setY(Double.toString(primaryStage.getY()));
                config.setConfig();
            }
        });

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

    private void addLedger(Account acct) {
        LedgerTask task = new LedgerTask(acct.getId());
//        LedgerView lv;
//        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
//            @Override
//            public void handle(WorkerStateEvent event) {
//                lv = (LedgerView)event.getSource().getValue();
//            }
//        });
        
        LedgerView lv = LedgerView.newInstance(acct.getId());
        isRebalance.bind(lv.isRebalance);
        viewList.put(acct.getId(), lv);
        Tab t = new LedgerTab(acct.getBankName(), acct.getId());
        t.setClosable(true);
        t.setContent(lv);
        getLedgers().add(t);
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

    private void setHandlers() {
        fileOpen.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                AccountPickerForm picker = new AccountPickerForm();
                picker.showForm();
                int newId = picker.accountId;
                if (!viewList.containsKey(newId)) {
                    for (Account acct : accountList) {
                        if (acct.getId() == newId) {
                            addLedger(acct);
                            int pos = 1;
                            for (Tab t : getLedgers()) {
                                if (tabPane.getTabs().indexOf(t) > -1) {
                                    pos += 1;
                                }
                                if (t.getId().equals(acct.getId().toString())) {
                                    tabPane.getTabs().add(pos, t);
                                    tabPane.getSelectionModel().select(t);
                                    break;
                                }
                            }

                        }
                    }
                }
            }
        });

        fileImport.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                ImportForm importForm = ImportForm.getInstance(accountList);
                importDone.bind(ImportForm.importDone);
                accountNum.bind(ImportForm.accountNum);
            }
        });

        fileRefresh.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                LedgerView lv = (LedgerView) tabPane.getSelectionModel().getSelectedItem().getContent();
                lv.doRefresh();
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
                CategoryForm.getInstance().showForm();
            }
        });

        fileExit.setOnAction((ActionEvent event) -> {
            primaryStage.fireEvent(new Event(WindowEvent.WINDOW_CLOSE_REQUEST));
            System.exit(0);
        });

        editCategories.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                CategoryForm catForm = CategoryForm.getInstance();
                catForm.showForm();
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
                int idx = prefs.getTabPane().getTabs().indexOf(prefs.getGeneralTab());
                prefs.getTabPane().getSelectionModel().select(idx);
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
                int idx = prefs.getTabPane().getTabs().indexOf(prefs.getAccountTab());
                prefs.getTabPane().getSelectionModel().select(idx);
                prefs.showForm();
            }
        });

        editRebalance.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                LedgerView lv = (LedgerView) tabPane.getSelectionModel().getSelectedItem().getContent();
                lv.doRebalance();
            }
        });

        importDone.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (importDone.getValue() == true) {
                    if (ImportForm.accountNum != null) {
                        accountId = ImportForm.accountNum.get();
                        LedgerView view = viewList.get(Integer.valueOf(accountId));
                        view.getList().addAll(ImportForm.newItems);
                        view.getList().sort(LedgerView.LedgerComparator);
                        view.getTable().setItems(view.getList());
                        importDone.unbind();
                    }
                }
            }
        });

    }

    /**
     * @param ledgers the ledgers to set
     */
    public void setLedgers(List<Tab> ledgers) {
        this.ledgers = ledgers;
    }

    static class LedgerTab extends Tab {

        private Integer accountId;

        public LedgerTab(String name, Integer id) {
            super(name);
            this.setId(id.toString());
            this.accountId = id;
            this.setOnClosed(new EventHandler() {
                @Override
                public void handle(Event event) {
                    viewList.remove(LedgerTab.this.accountId);
                    getLedgers().remove(LedgerTab.this);
                }
            });

        }
    }
    
    private class LedgerTask extends Task<LedgerView> {
        int acct;
        private final LedgerView view;
        
        public LedgerTask(int n) {
            this.acct = n;
            view = null;
        }
        
        @Override
        protected LedgerView call() throws Exception {
            return LedgerView.newInstance(acct);
        }
        
    }

}
