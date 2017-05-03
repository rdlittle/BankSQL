/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.app;

import com.webfront.controller.BankController;
import com.webfront.model.Config;
import java.util.Locale;
import java.util.ResourceBundle;
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

<<<<<<< HEAD
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
    private final MenuItem fileNewStatement = new MenuItem("St_atement type");
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
        fileNewStatement.setMnemonicParsing(true);
        fileExit.setMnemonicParsing(true);
        fileNewMenu.getItems().addAll(fileNewCategory, fileNewStore, fileNewStatement);

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
=======
    private final String location = "/com/webfront/app/fxml/Bank.fxml";
    private final String propertyString = "com.webfront.app.bank";
    private final Config config = Config.getInstance();
    ResourceBundle rb;

    @Override
    public void start(Stage primaryStage) throws Exception {
        config.getConfig();
        rb = ResourceBundle.getBundle(propertyString, Locale.getDefault());
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(BankController.class.getResource(location));
        loader.setResources(rb);
        AnchorPane root = loader.<AnchorPane>load();
        BankController controller = loader.getController();
        controller.setStage(primaryStage);
        Scene scene = new Scene(root);
        controller.getFileExit().setOnAction(new EventHandler() {
>>>>>>> revision1
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
<<<<<<< HEAD
        setHandlers();
=======
>>>>>>> revision1
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
<<<<<<< HEAD

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
        
        fileNewStatement.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

=======
>>>>>>> revision1
}
