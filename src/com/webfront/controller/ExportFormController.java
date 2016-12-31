/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.controller;

import com.webfront.app.Bank;
import com.webfront.app.utils.Exporter;
import com.webfront.app.utils.QifExporter;
import com.webfront.bean.AccountManager;
import com.webfront.bean.FormatManager;
import com.webfront.model.Account;
import com.webfront.model.Config;
import com.webfront.model.ExportFormat;
import com.webfront.model.Ledger;
import java.io.File;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author rlittle
 */
public class ExportFormController {

    @FXML
    private DatePicker dpStart;

    @FXML
    private DatePicker dpEnd;

    @FXML
    private ComboBox<Account> cbAccount;

    @FXML
    private ComboBox<ExportFormat> cbExportType;

    @FXML
    private TextField txtPath;

    @FXML
    private Button btnBrowse;

    @FXML
    private Button btnOk;

    @FXML
    private Button btnCancel;

    @FXML
    private ProgressBar progressBar;

    private Stage stage;

    private File outputFile;

    private Exporter exporter;

    @FXML
    void initialize() {
        outputFile = null;
        LocalDate now = LocalDate.now();
        dpStart.valueProperty().set(now.minusDays(30));
        dpEnd.valueProperty().set(now);

        cbAccount.converterProperty().set(new AccountManager.AccountConverter());
        cbAccount.itemsProperty().set(AccountManager.getInstance().getAccounts());

        cbExportType.converterProperty().set(new FormatManager.ExportFormatConverter());
        cbExportType.itemsProperty().set(FormatManager.getInstance().getFormatList());
    }

    @FXML
    void onBtnBrowse(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select statement to import");
        fileChooser.setInitialDirectory(new File(Config.getInstance().getImportDir()));
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            outputFile = selectedFile;
        }
    }

    @FXML
    void onBtnCancel(ActionEvent event) {
        stage.close();
    }

    @FXML
    void onBtnOk(ActionEvent event) {
        if (outputFile == null) {
            txtPath.requestFocus();
            return;
        }
        LocalDate startDate = dpStart.valueProperty().get();
        LocalDate endDate = dpEnd.valueProperty().get();
        Account acct = cbAccount.getValue();
        ExportFormat format = cbExportType.getValue();
        if (format.getExtension().equalsIgnoreCase("qif")) {
            exporter = new QifExporter(outputFile);
            exporter.setAccount(acct);
            exporter.setStartDate(startDate);
            exporter.setEndDate(endDate);
            doExport();
        } else {

        }
    }

    private void doExport() {
        Thread t = new Thread(exporter);
        t.start();
        while (t.isAlive()) {
            try {
                t.join(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Bank.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Task<Void> exportTask;
        exportTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Double itemCount = (double) exporter.getList().size();
                Double progress = (double) 0;
                Double itemsCreated = (double) 0;
                for (Ledger l : exporter.getList()) {
                    itemsCreated += 1;
                    progress = itemsCreated / itemCount;
                    updateProgress(progress, 1);
                }
                return null;
            }
            @Override
            protected void succeeded() {
                super.succeeded();
                stage.close();
            }
        };
        progressBar.progressProperty().bind(exportTask.progressProperty());
        progressBar.setVisible(true);
        if (Platform.isFxApplicationThread()) {
            new Thread((Runnable) exportTask).start();
        } else {
            Platform.runLater(exportTask);
        }        
    }

    /**
     * @return the stage
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * @param stage the stage to set
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
