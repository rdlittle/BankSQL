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
import com.webfront.view.PreferencesForm;
import com.webfront.view.StoresView;
import com.webfront.view.SummaryView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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

    public final SimpleBooleanProperty importDone;
    public final SimpleIntegerProperty accountNum;
    private final SimpleObjectProperty<LedgerView> selectedAccount;

    private ImportForm importForm;
    private final PaymentListListener paymentListener;

    public BankController() {
        this.accountList = FXCollections.<Account>observableArrayList();
        this.bankManager = BankManager.getInstance();
        this.ledgerTabs = FXCollections.<Tab>observableArrayList();
        this.isRebalance = new SimpleBooleanProperty(false);
        this.isLedgerTab = new SimpleBooleanProperty(false);
        this.viewMap = FXCollections.<Integer, LedgerView>observableHashMap();
        this.stores = StoresView.getInstance();
        this.categories = CategoryView.getInstance();
        this.importDone = new SimpleBooleanProperty();
        this.accountNum = new SimpleIntegerProperty();
        this.selectedAccount = new SimpleObjectProperty<>();
        this.config = Config.getInstance();
        paymentListener = new PaymentListListener();
        config.getConfig();
    }

    /**
     * Initializes the controller class.
     *
     */
    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accountList.setAll(bankManager.getList(""));
        for (Account acct : accountList) {
            if (acct.getAccountStatus() != Account.AccountStatus.CLOSED) {
                addLedger(acct);
            }
        }

        summaryTab.setContent(SummaryView.getInstance());
        tabPane.getTabs().addAll(ledgerTabs);
        Tab t = new Tab();
//        PaymentView.getInstance().getList().addListener(paymentListener);
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
//                    detailViewController.addListener(paymentListener);
                } else {
                    isLedgerTab.set(newTab instanceof LedgerTab);
//                    detailViewController.removeListener(paymentListener);
                }
            }
        });

        importDone.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (importDone.getValue() == true) {
                    if (ImportForm.accountNum != null) {
                        LedgerView view = viewMap.get(Integer.valueOf(accountNum.get()));
                        view.getList().addAll(ImportForm.newItems);
                        view.getList().sort(LedgerView.LedgerComparator);
                        view.getTable().setItems(view.getList());
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
                LedgerView l = viewMap.get(newValue);
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

    }

    @FXML
    public void onFileExit() {
        stage.fireEvent(new Event(WindowEvent.WINDOW_CLOSE_REQUEST));
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
    public void onEditCategories() {

    }

    @FXML
    public void onEditStores() {

    }

    @FXML
    public void onImportSetup() {
        PDFViewer view = PDFViewer.getInstance("");
    }

    @FXML
    public void onEditPreferences() {
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

    public void setStage(Stage s) {
        BankController.stage = s;
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
            detailViewController.table.refresh();
        }
    }

}
