/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.app.Bank;
import com.webfront.app.utils.CSVImporter;
import com.webfront.app.utils.Importer;
import com.webfront.app.utils.PDFImporter;
import com.webfront.app.utils.QifImporter;
import com.webfront.bean.AccountManager;
import com.webfront.model.Account;
import com.webfront.model.Account.StatementFormat;
import com.webfront.model.Config;
import com.webfront.model.Ledger;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
    ComboBox<Account> cbAccount;
    @FXML
    Label lblMessage;
    @FXML
    ProgressBar importProgress;

    public static ArrayList<Ledger> newItems;
    private final SimpleObjectProperty<LedgerView> ledgerViewProperty;

    Stage stage;
    public Account selectedAccount;
    public String fileName;

    private static ImportForm form = null;
    private boolean hasFile;
    private boolean hasAccount;
    public static SimpleBooleanProperty importDone;
    public static SimpleIntegerProperty accountNum;

    private final HashMap<StatementFormat, String> stmtFormatFileTypes;

    private ImportForm(ObservableList<Account> list) {
        fileName = "";
        newItems = new ArrayList<>();
        btnOK = new Button();
        btnCancel = new Button();
        txtFileName = new TextField();
        cbAccount = new ComboBox<>();
        importProgress = new ProgressBar();
        importDone = new SimpleBooleanProperty();
        accountNum = new SimpleIntegerProperty();
        ledgerViewProperty = new SimpleObjectProperty();
        stmtFormatFileTypes = new HashMap<>();
    }

    public static ImportForm getInstance(ObservableList<Account> list) {
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

            form.cbAccount.converterProperty().setValue(new AccountManager.AccountConverter());
            form.cbAccount.itemsProperty().setValue(AccountManager.getInstance().getAccounts());

            form.cbAccount.valueProperty().addListener(new ChangeListener<Account>() {
                @Override
                public void changed(ObservableValue<? extends Account> observable, Account oldValue, Account newValue) {
                    form.selectedAccount = form.cbAccount.getValue();
                    form.hasAccount = true;
                    form.btnOK.setDisable(form.hasFile == true ? false : true);
                    ImportForm.accountNum.set(form.cbAccount.getValue().getId());
                }
            });
            form.btnBrowse.disableProperty().bind(form.cbAccount.valueProperty().isNull());
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

        StatementFormat sf = form.cbAccount.getValue().getStatementFormat();
        switch (sf) {
            case PDF:
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf"));
                break;
            case QIF:
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("QIF Files (*.qif)", "*.qif"));
                break;
            default:
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files (*.txt) (*.csv)", "*.txt", "*.csv"));
                break;
        }

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
        importProgress.progressProperty().set(0);
        importDone.set(false);

        Importer importer;
        if (fileName.contains("pdf")) {
            importer = new PDFImporter(fileName, form.cbAccount.getValue());
        } else if (fileName.contains("qif")) {
            importer = new QifImporter(fileName,form.cbAccount.getValue());
        } else {
            importer = new CSVImporter(fileName, form.cbAccount.getValue());
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
        Task<Void> importTask;
        importTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                newItems = importer.getItemList();
                Double itemCount = (double) newItems.size();
                Double progress = (double) 0;
                Double itemsCreated = (double) 0;
                LedgerView view = ledgerViewProperty.getValue();
                for (Ledger item : newItems) {
                    view.getLedgerManager().create(item);
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
                updateMessage("Import failed!  " + super.exceptionProperty().toString());
                importProgress.progressProperty().unbind();
                importProgress.setProgress(0);
                importProgress.setVisible(false);
                form.stage.close();
            }
        };
        importProgress.progressProperty().bind(importTask.progressProperty());
        if (Platform.isFxApplicationThread()) {
            new Thread((Runnable) importTask).start();
        } else {
            Platform.runLater(importTask);
        }

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

    /**
     * @return the viewProperty
     */
    public SimpleObjectProperty<LedgerView> getLedgerViewProperty() {
        return ledgerViewProperty;
    }

}
