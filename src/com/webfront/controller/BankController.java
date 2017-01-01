/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.controller;

import com.webfront.bean.BankManager;
import com.webfront.model.Account;
import com.webfront.model.Config;
import com.webfront.model.Payment;
import com.webfront.view.AccountPickerForm;
import com.webfront.view.CategoryView;
import com.webfront.view.ImportForm;
import com.webfront.view.LedgerView;
import com.webfront.view.PaymentView;
import com.webfront.view.SetupForm;
import com.webfront.view.SearchForm;
import com.webfront.view.StoresView;
import com.webfront.view.SummaryView;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author rlittle
 */
public class BankController implements Initializable {

    @FXML
    MenuBar menuBar;

    @FXML
    Menu fileMenu;
    @FXML
    Menu fileNewMenu;
    @FXML
    Menu editMenu;

    @FXML
    MenuItem fileNewAccount;
    @FXML
    MenuItem fileNewCategory;
    @FXML
    MenuItem fileNewStore;
    @FXML
    MenuItem fileExport;
    @FXML
    MenuItem fileImport;
    @FXML
    MenuItem fileOpen;
    @FXML
    MenuItem fileClose;
    @FXML
    MenuItem fileExit;

    @FXML
    Menu editAccounts;
    @FXML
    MenuItem editAccountsRebalance;
    @FXML
    MenuItem editCategories;
    @FXML
    MenuItem editStores;
    @FXML
    MenuItem editPreferences;

    @FXML
    Pane statusPanel;
    @FXML
    TabPane tabPane;
    @FXML
    Tab detailTab;
    @FXML
    Tab summaryTab;
    @FXML
    Tab storesTab;
    @FXML
    Tab categoriesTab;

    @FXML
    Button btnOK;
    @FXML
    Button btnCancel;

    @FXML
    HBox hbox;
    @FXML
    Pane storesPane;

    @FXML
    TreeView treeView1;

    @FXML
    CategoryController categoryController;

    @FXML
    private Pane detailView;

    @FXML
    private DetailViewController detailViewController;

    private static Stage stage;
    private final Config config;
    private final BankManager bankManager;
    private final ObservableList<Account> accountList;
    private final ObservableList<Tab> ledgerTabs;
    private final SimpleBooleanProperty isRebalance;
    private final ObservableMap<Integer, LedgerView> viewMap;
    private final SimpleBooleanProperty isLedgerTab;
    private final StoresView stores;
    private final CategoryView categories;

    public final SimpleBooleanProperty ready;
    public final SimpleBooleanProperty importDone;
    public final SimpleIntegerProperty accountNum;
    private final SimpleObjectProperty<LedgerView> selectedAccount;

    private ImportForm importForm;
    private final PaymentListListener paymentListener;
    private ResourceBundle res;

    public BankController() {
        this.accountList = FXCollections.<Account>observableArrayList();
        this.bankManager = BankManager.getInstance();
        this.ledgerTabs = FXCollections.<Tab>observableArrayList();
        this.isRebalance = new SimpleBooleanProperty(false);
        this.isLedgerTab = new SimpleBooleanProperty(false);
        this.ready = new SimpleBooleanProperty(false);
        this.viewMap = FXCollections.<Integer, LedgerView>observableHashMap();
        this.stores = StoresView.getInstance();
        this.categories = CategoryView.getInstance();
        this.importDone = new SimpleBooleanProperty();
        this.accountNum = new SimpleIntegerProperty();
        this.selectedAccount = new SimpleObjectProperty<>();
        this.config = Config.getInstance();
        paymentListener = new PaymentListListener();
        config.getConfig();
        this.res = ResourceBundle.getBundle("com.webfront.app.bank");
    }

    /**
     * Initializes the controller class.
     *
     */
    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.res = resources;
        for(Account acct : accountList) {
            if(acct.getAccountStatus() != Account.AccountStatus.CLOSED) {
                addLedger(acct);
            }
        }

        summaryTab.setContent(SummaryView.getInstance());
        tabPane.getTabs().addAll(ledgerTabs);

        Tab t = new Tab();
        t.setContent(PaymentView.getInstance());
        t.setText("Payments");
        tabPane.getTabs().add(t);

        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {
                Node n = newTab.getContent();
                if (n == null) {
                    isLedgerTab.set(false);
                } else if (newTab.getText().equals("Payments")) {
                    isLedgerTab.set(false);
                    PaymentView.getInstance().getList().addListener(paymentListener);
                } else {
                    isLedgerTab.set(newTab instanceof LedgerTab);
                    PaymentView.getInstance().getList().removeListener(paymentListener);
                }
            }
        });

        importDone.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (importDone.getValue() == true) {
                    if (ImportForm.accountNum != null) {
                        LedgerView view = viewMap.get(accountNum.get());
                        view.getList().addAll(ImportForm.newItems);
                        view.getList().sort(LedgerView.LedgerComparator);
                        detailViewController.addLedgerEntries(ImportForm.newItems);
                        importDone.unbind();
                        accountNum.unbind();
                    }
                }
            }
        });

        editAccountsRebalance.disableProperty().bind(isLedgerTab.not());
        accountNum.addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                LedgerView l = viewMap.get((Integer) newValue);
                selectedAccount.setValue(l);
            }
        });
    }

    private void addLedger(Account acct) {
        LedgerView lv = LedgerView.newInstance(acct.getId());
        isRebalance.bind(lv.isRebalance);
        viewMap.put(acct.getId(), lv);
        Tab t = new LedgerTab(acct.getBankName(), acct.getId());
        t.setClosable(true);
        t.setContent(lv);
        ledgerTabs.add(t);
    }

    public MenuItem getFileExit() {
        return fileExit;
    }

    @FXML
    public void onFileExport() {
        FXMLLoader loader = new FXMLLoader();
        URL url = getClass().getResource("/com/webfront/app/fxml/ExportForm.fxml");
        loader.setLocation(url);
        try {
            loader.load();
            ExportFormController controller = loader.getController();
            Stage s = new Stage();
            s.setScene(new Scene(loader.getRoot()));
            controller.setStage(s);
            s.showAndWait();
        } catch (IOException ex) {
            Logger.getLogger(ExportFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    public void onFileImport() {
        importForm = ImportForm.getInstance(accountList);
        importDone.bind(ImportForm.importDone);
        accountNum.bind(ImportForm.accountNum);
        importForm.getLedgerViewProperty().bind(selectedAccount);
    }

    @FXML
    public void onAdd() {
        System.out.println("BankController.onAdd()");
    }

    @FXML
    public void onDelete() {

    }

    @FXML
    public void onFileNewAccount() {
        SetupForm prefs = SetupForm.getInstance(config);
        prefs.getTabPane().getSelectionModel().select(prefs.getAccountTab());

        prefs.isNewAccount = true;
        prefs.hasChanged.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                config.setConfig();
            }
        });
        prefs.btnNewOnAction();
        prefs.showForm();
    }

    @FXML
    public void onFileNewCategory() {

    }

    @FXML
    void onFileNewStatement(ActionEvent event) {

    }

    @FXML
    public void onFileNewStore() {

    }

    @FXML
    public void onFileOpen() {
        AccountPickerForm picker = new AccountPickerForm();
        picker.showForm();
        int newId = picker.accountId;
        if (newId == 0) {
            return;
        }
        if (!viewMap.containsKey(newId)) {
            for (Account acct : accountList) {
                if (acct.getId() == newId) {
                    addLedger(acct);
                    int pos = 1;
                    for (Tab t : ledgerTabs) {
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

    @FXML
    public void onFileClose() {
        Tab t = tabPane.getSelectionModel().selectedItemProperty().get();
        if (t instanceof LedgerTab) {
            ledgerTabs.remove(t);
            tabPane.getTabs().remove(t);
        }
    }

    @FXML
    public void onFileExit() {
        System.exit(0);
    }

    @FXML
    public void onEditAccount() {

    }

    @FXML
    public void onEditRebalance() {
        LedgerView lv = (LedgerView) tabPane.getSelectionModel().getSelectedItem().getContent();
        lv.doRebalance();
    }

    @FXML
    public void onEditSearch() {
        SearchForm sf = new SearchForm();
        sf.setForm();
        sf.showForm();
    }

    @FXML
    public void onEditCategories() {

    }

    @FXML
    public void onEditStores() {

    }

    @FXML
    public void onEditXref() {
        FXMLLoader viewLoader = new FXMLLoader();
        String v = "/com/webfront/app/fxml/XrefView.fxml";
        String t = "Cross-references";
        String rs = res.getString("viewXref");
        URL url = BankController.class.getResource(v);
        viewLoader.setLocation(url);
        viewLoader.setResources(res);
        try {
            Pane root = viewLoader.<AnchorPane>load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
//            root.addEventFilter(KeyEvent.KEY_PRESSED, event -> System.out.println("Pressed: "+event.getCode()));
            stage.setTitle(t);
            ControllerInterface ctrl = viewLoader.getController();
            ctrl.getBtnClose().setOnAction(new EventHandler() {
                @Override
                public void handle(Event event) {
                    ctrl.getBtnClose().removeEventHandler(EventType.ROOT, this);
                    stage.close();
                }
            });

            stage.showAndWait();
        } catch (IOException ex) {
            Logger.getLogger(BankController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    public void onImportSetup() {
        PDFViewer view = PDFViewer.getInstance("");
    }

    @FXML
    public void onEditPreferences() {
        SetupForm prefs = SetupForm.getInstance(config);
        prefs.isNewAccount = false;
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

    public void setStage(Stage s) {
        BankController.stage = s;
    }

    /**
     * @return the stage
     */
    public static Stage getStage() {
        return stage;
    }

    private class LedgerTab extends Tab {

        private Integer accountId;

        public LedgerTab(String name, Integer id) {
            super(name);
            this.setId(id.toString());
            this.accountId = id;
            this.setOnClosed(new EventHandler() {
                @Override
                public void handle(Event event) {
                    viewMap.remove(LedgerTab.this.accountId);
                    ledgerTabs.remove(LedgerTab.this);
                }
            });

        }

    }

    private class PaymentListListener implements ListChangeListener<Payment> {

        @Override
        public void onChanged(Change<? extends Payment> c) {
            while (c.next()) {
                if (c.wasUpdated()) {
                    ObservableList l = c.getList();
                    detailViewController.doUpdate(l);
                } else if (c.wasRemoved()) {
                } else if (c.wasAdded()) {

                }
            }
        }
    }

}
