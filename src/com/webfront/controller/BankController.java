/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.controller;

import com.webfront.bean.BankManager;
import com.webfront.model.Account;
import com.webfront.view.CategoryView;
import com.webfront.view.LedgerView;
import com.webfront.view.StoresView;
import com.webfront.view.SummaryView;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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
public class BankController {
    
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
    
//    @FXML
//    HBox buttonPanel;
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
    
    private static Stage stage;
    private final BankManager bankManager;
    private final ObservableList<Account> accountList;
    private final ObservableList<Tab> ledgerTabs;
    private final SimpleBooleanProperty isRebalance;
    private final ObservableMap<Integer, LedgerView> viewMap;
    private final SimpleBooleanProperty isLedgerTab;
    private final StoresView stores;
    private final CategoryView categories;
    
    public BankController() {
        this.accountList = FXCollections.<Account>observableArrayList();
        this.bankManager = BankManager.getInstance();
        this.ledgerTabs = FXCollections.<Tab>observableArrayList();
        this.isRebalance = new SimpleBooleanProperty(false);
        this.isLedgerTab = new SimpleBooleanProperty(false);
        this.viewMap = FXCollections.<Integer, LedgerView>observableHashMap();
        this.stores = StoresView.getInstance();
        this.categories = CategoryView.getInstance();
        
    }

    /**
     * Initializes the controller class.
     *
     */
    @FXML
    public void initialize() {
        accountList.setAll(bankManager.getList(""));
        for (Account acct : accountList) {
            if (acct.getAccountStatus() != Account.AccountStatus.CLOSED) {
                addLedger(acct);
            }
        }
        summaryTab.setContent(SummaryView.getInstance());
        tabPane.getTabs().addAll(ledgerTabs);
        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {
                Node n = newTab.getContent();
                if (n == null) {
                    isLedgerTab.set(false);
                } else {
                    String title = newTab.getText();
                    Class c = newTab.getContent().getClass();
                    String s = c.getSimpleName();
                    isLedgerTab.set(title.equalsIgnoreCase("Detail"));
                }
            }
        });
        
        editAccountsRebalance.disableProperty().bind(isLedgerTab.not());
    }
    
    @FXML
    public void onAdd() {
        
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
    public void onFileNewStore() {
        
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
    public void onFileImport() {
        
    }
    
    @FXML
    public void onEditAccount() {
        
    }
    
    @FXML
    public void onEditCategories() {
        
    }
    
    @FXML
    public void onEditStores() {
        
    }
    
    @FXML
    public void onEditPreferences() {
        
    }
    
    public void setStage(Stage s) {
        BankController.stage = s;
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
    
}
