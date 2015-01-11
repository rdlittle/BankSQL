/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.model.Account;
import com.webfront.model.Config;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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

    ArrayList<Account> accountList;
    Stage stage;
    public int selectedAccount;
    public String fileName;

    private static ImportForm form = null;

    private ImportForm(ArrayList<Account> list) {
        fileName = "";
        selectedAccount = -1;
        accountList = new ArrayList<>();
        btnOK = new Button();
        btnCancel = new Button();
        txtFileName = new TextField();
        cbAccount = new ComboBox();
        accountList = list;
    }

    public static ImportForm getInstance(ArrayList<Account> list) {
        if (form == null) {
            form = new ImportForm(list);
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
                            break;
                        }
                    }
                }
            });

            form.stage.setScene(scene);
            form.stage.setTitle("Import Statement");
            form.stage.showAndWait();
        }
        return form;
    }

    @FXML
    public void btnBrowseClicked() {
        form.lblMessage.setText("");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select statement to import");
        fileChooser.setInitialDirectory(new File(Config.getInstance().getInstallDir()));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files (*.txt) (*.csv)", "*.txt", "*.csv"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf"));
        File selectedFile = fileChooser.showOpenDialog(stage);
        if(selectedFile != null) {
            form.txtFileName.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    public void btnOKClicked() {
        if(form.txtFileName.getText().isEmpty()) {
            form.lblMessage.setText("You must select a file to import.");
        } else if (form.selectedAccount < 0) {
            form.lblMessage.setText("You must select an account.");
        } else {
            fileName = txtFileName.getText();
            form.stage.close();
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
}
