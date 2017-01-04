/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.controller;

import com.webfront.app.utils.Exporter;
import com.webfront.app.utils.QifExporter;
import com.webfront.bean.AccountManager;
import com.webfront.bean.FormatManager;
import com.webfront.model.Account;
import com.webfront.model.Config;
import com.webfront.model.ExportFormat;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
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
        for (ExportFormat ef : cbExportType.getItems()) {
            fileChooser.getExtensionFilters().add(new ExtensionFilter(ef.getDescription(), ef.getExtension()));
        }
        File selectedFile = fileChooser.showSaveDialog(stage);

        if (selectedFile != null) {
            outputFile = selectedFile;
            txtPath.setText(outputFile.getPath());
            List<String> l = fileChooser.getSelectedExtensionFilter().getExtensions();
            for(ExportFormat ef : cbExportType.getItems()) {
                if(l.get(0).toString().contains(ef.getExtension())) {
                    cbExportType.valueProperty().set(ef);
                    break;
                }
            }
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
        if (format.getExtension().equalsIgnoreCase("*.qif")) {
            exporter = new QifExporter(outputFile);
            exporter.setAccount(acct);
            exporter.setStartDate(startDate);
            exporter.setEndDate(endDate);
            exporter.isDoneProperty.addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            stage.close();
                        }
                    });
                }
            });

            progressBar.progressProperty().bind(exporter.progressProperty());
            progressBar.setVisible(true);
            if (Platform.isFxApplicationThread()) {
                new Thread((Runnable) exporter).start();
            } else {
                Platform.runLater(exporter);
            }
        } else {

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
