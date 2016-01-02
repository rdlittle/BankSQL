/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.app.Bank;
import static com.webfront.app.Bank.viewList;
import com.webfront.app.utils.CSVImporter;
import com.webfront.app.utils.Importer;
import com.webfront.app.utils.PDFImporter;
import com.webfront.bean.DistributionManager;
import com.webfront.model.Account;
import com.webfront.model.Config;
import com.webfront.model.Distribution;
import com.webfront.model.Ledger;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author rlittle
 */
public class ImportForm extends AnchorPane {

    @FXML
    Button btnOK, btnCancel, btnBrowse;
    @FXML
    TextField txtFileName;
    @FXML
    ComboBox cbAccount;
    @FXML
    Label lblMessage;
    @FXML
    ProgressBar importProgress;

    ArrayList<Account> accountList;
    public static ArrayList<Ledger> newItems;
    
    Stage stage;
    public int selectedAccount;
    public String fileName;

    private static ImportForm form = null;
    private boolean hasFile;
    private boolean hasAccount;
    public static SimpleBooleanProperty importDone;
    public static SimpleIntegerProperty accountNum;

    private ImportForm(ArrayList<Account> list) {
        fileName = "";
        selectedAccount = -1;
        accountList = new ArrayList<>();
        newItems = new ArrayList<>();
        btnOK = new Button();
        btnCancel = new Button();
        txtFileName = new TextField();
        cbAccount = new ComboBox();
        accountList = list;
        importProgress = new ProgressBar();
        importDone = new SimpleBooleanProperty();
        accountNum = new SimpleIntegerProperty();
    }

    public static ImportForm getInstance(ArrayList<Account> list) {
        if (form == null) {
            form = new ImportForm(list);
            form.hasAccount = false;
            form.hasFile = false;
            importDone.set(false);
            URL location = form.getClass().getResource("/com/webfront/app/fxml/ImportForm.fxml");
            ResourceBundle resources = ResourceBundle.getBundle("com.webfront.app.bank");
            FXMLLoader loader = new FXMLLoader(location, resources);
            loader.setRoot(form);
            loader.setController(form);

            Scene scene;
            form.stage = new Stage();
            scene = new Scene(form);

            try {
                Object load;
                load = loader.load();
            } catch (IOException ex) {
                Logger.getLogger(ImportForm.class.getName()).log(Level.SEVERE, null, ex);
            }
            form.accountList.stream().forEach((acct) -> {
                form.cbAccount.getItems().addAll(acct.getBankName() + " - " + acct.getAccountName());
            });

            form.cbAccount.setOnAction((Event event) -> {
                ComboBox cb = (ComboBox) event.getSource();
                if (cb.getValue() != null && !cb.getValue().toString().isEmpty()) {
                    String sName = cb.getValue().toString();
                    for (Account acct : form.accountList) {
                        if (sName.equals(acct.getBankName() + " - " + acct.getAccountName())) {
                            form.selectedAccount = acct.getId();
                            form.hasAccount = true;
                            form.btnOK.setDisable(form.hasFile == true ? false : true);
                            ImportForm.accountNum.set(acct.getId());
                            break;
                        }
                    }
                }
            });
            
            form.btnOK.setDisable(true);
            form.stage.setScene(scene);
            form.stage.setTitle("Import Statement");
        }
        importDone.set(false);
        form.txtFileName.setText(null);
        form.cbAccount.getSelectionModel().select(-1);
        form.importProgress.setVisible(false);
        form.stage.show();
        return form;
    }

    @FXML
    public void btnBrowseClicked() {
        form.lblMessage.setText("");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select statement to import");
        fileChooser.setInitialDirectory(new File(Config.getInstance().getImportDir()));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files (*.txt) (*.csv)", "*.txt", "*.csv"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf"));
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            form.txtFileName.setText(selectedFile.getAbsolutePath());
            fileName = form.txtFileName.getText();
            form.hasFile = true;
            form.btnOK.setDisable((form.hasAccount != true));
        }
    }

    @FXML
    public void btnOKClicked() {
        importProgress.setVisible(true);
        importDone.set(false);

        Importer importer;
        if (fileName.contains("pdf")) {
            importer = new PDFImporter(fileName, form.selectedAccount);
        } else {
            importer = new CSVImporter(fileName, form.selectedAccount);
        }
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
                newItems = importer.getItemList();
                DistributionManager distMgr = new DistributionManager();
                Double itemCount = (double) newItems.size();
                Double progress = (double) 0;
                Double itemsCreated = (double) 0;
                LedgerView view = viewList.get(Integer.valueOf(selectedAccount));
                for (Ledger item : newItems) {
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
                importProgress.progressProperty().unbind();
                importProgress.setProgress(0);
                importProgress.setVisible(false);
                form.stage.close();
                importDone.setValue(true);
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                updateMessage("Cancelled!");
                form.stage.close();
            }

            @Override
            protected void failed() {
                super.failed();
                updateMessage("Failed!");
                form.stage.close();
            }
        };
        importProgress.progressProperty().bind(importTask.progressProperty());
        new Thread(importTask).start();
    }

    @FXML
    public void btnCancelClicked() {
        stage.close();
    }

    /**
     * @return the form
     */
    public static ImportForm getForm() {
        return form;
    }

    /**
     * @param aForm the form to set
     */
    public static void setForm(ImportForm aForm) {
        form = aForm;
    }
}
