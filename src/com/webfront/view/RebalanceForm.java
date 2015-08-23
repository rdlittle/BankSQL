/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.view;

import com.webfront.model.SearchCriteria;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author rlittle
 */
public class RebalanceForm extends AnchorPane {

    Stage stage;
    Scene scene;
    private static RebalanceForm form = null;
    private FXMLLoader loader;

    @FXML
    DatePicker dateStart;
    @FXML
    TextField txtStart;
    @FXML
    Button btnChooseStart;

    @FXML
    DatePicker dateEnd;
    @FXML
    TextField txtEnd;
    @FXML
    Button btnChooseEnd;

    @FXML
    TextField txtOpeningBalance;

    @FXML
    RadioButton rbDate;
    @FXML
    RadioButton rbTrans;
    @FXML
    RadioButton rbBoth;

    @FXML
    Button btnCancel;
    @FXML
    Button btnGo;
    @FXML
    Label lblMessage;

    public SimpleStringProperty startProperty;
    public SimpleStringProperty endProperty;
    public SimpleStringProperty balanceProperty;
    public SimpleBooleanProperty hasChanged;

    ToggleGroup rbGroup;
    EventHandler<MouseEvent> click;
    LedgerView parentView;
    TextField selectedField;
    int startTrans;
    int endTrans;
    LocalDate startDate;
    LocalDate endDate;
    float balance;
    private SearchCriteria criteria;

    private RebalanceForm() {
        this.loader = null;
        URL location = getClass().getResource("/com/webfront/app/fxml/RebalanceForm.fxml");
        ResourceBundle resources = ResourceBundle.getBundle("com.webfront.app.bank");
        loader = new FXMLLoader(location, resources);
        rbGroup = new ToggleGroup();
        stage = new Stage();
        scene = new Scene(this);
        stage.setScene(scene);
        stage.setTitle("Rebalance");
        dateStart = new DatePicker();
        txtStart = new TextField();
        dateEnd = new DatePicker();
        txtEnd = new TextField();
        txtOpeningBalance = new TextField();
        rbDate = new RadioButton();
        rbDate.setToggleGroup(rbGroup);

        rbTrans = new RadioButton();
        rbTrans.setToggleGroup(rbGroup);

        btnChooseStart = new Button();
        btnChooseEnd = new Button();
        selectedField = new TextField();
        btnGo = new Button();
        btnCancel = new Button();
        hasChanged = new SimpleBooleanProperty(false);
        lblMessage = new Label("");
        txtStart.setId("txtStart");
        txtEnd.setId("txtEnd");
        startTrans = 0;
        endTrans = 0;
        balance = 0;

        startDate = LocalDate.now();
        endDate = LocalDate.now();
        
        criteria = new SearchCriteria();
        
    }

    public static RebalanceForm getInstance(LedgerView view) {
        if (form == null) {
            form = new RebalanceForm();
            form.loader.setRoot(form);
            form.loader.setController(form);
            form.parentView = view;
            try {
                form.loader.load();
                form.addHandlers();
            } catch (IOException ex) {
                Logger.getLogger(PreferencesForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            form.hasChanged.set(false);
        }
        return form;
    }

    @FXML
    public void btnChooseStartOnAction() {

    }

    @FXML
    public void btnChooseEndOnAction() {
        form.lblMessage.setText("");
    }

    @FXML
    public void btnGoOnAction() {
        if (startTrans > endTrans) {
            form.lblMessage.setText("Starting transaction cannot be greater than ending transaction");
            form.txtEnd.requestFocus();
        } else {
            form.lblMessage.setText("");
            if (!txtOpeningBalance.getText().isEmpty() && !"".equals(txtOpeningBalance.getText())) {
                balance = Float.parseFloat(txtOpeningBalance.getText());
                criteria.setBeginningBalance(Float.valueOf(balance));
            }
            hasChanged.set(true);
            closeForm();
        }
    }

    @FXML
    public void closeForm() {
        form.stage.close();
    }

    public void showForm() {
        stage.showAndWait();
    }

    private void addHandlers() {
        form.btnChooseStart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                selectedField = txtStart;
                form.lblMessage.setText("");
                form.stage.setIconified(true);
            }
        });

        form.btnChooseEnd.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                selectedField = txtEnd;
                form.lblMessage.setText("");
                form.stage.setIconified(true);
            }
        });

        form.dateStart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                startDate = dateStart.getValue();
                if (dateEnd.getValue() == null) {
                    endDate = startDate;
                    dateEnd.setValue(endDate);
                } else {
                    if (startDate.isBefore(endDate)) {
                        endDate = startDate;
                        dateEnd.setValue(endDate);
                    }                    
                    if (dateEnd.getChronology().compareTo(dateStart.getChronology()) > 0) {
                        endDate = startDate;
                        dateEnd.setValue(endDate);
                    }
                }
                criteria.setStartDate(startDate.format(DateTimeFormatter.ISO_DATE));
                LocalDate[] dr = criteria.getDateRange();
                dr[0]=startDate;
                criteria.setDateRange(dr);
            }
        });

        form.dateEnd.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                endDate = dateEnd.getValue();
                if (dateStart.getValue() == null) {
                    startDate = endDate;
                    dateStart.setValue(startDate);
                } else {
                    if (endDate.isBefore(startDate)) {
                        startDate = endDate;
                        dateStart.setValue(startDate);
                    }
                    if (dateStart.getChronology().compareTo(dateEnd.getChronology()) > 0) {
                        startDate = endDate;
                        dateStart.setValue(startDate);
                    }
                }
                criteria.setEndDate(endDate.format(DateTimeFormatter.ISO_DATE));                
                LocalDate[] dr = criteria.getDateRange();
                dr[1]=endDate;
                criteria.setDateRange(dr);                
            }
        });

        click = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (stage.isIconified()) {
                    stage.setIconified(false);
                    selectedField.setText(form.parentView.selectedItem.getId().toString());
                    if ("txtStart".equals(selectedField.getId())) {
                        startTrans = Integer.parseInt(selectedField.getText());
                        txtEnd.requestFocus();
                    } else {
                        endTrans = Integer.parseInt(selectedField.getText());
                    }
                }
            }
        };
    }

    /**
     * @return the criteria
     */
    public SearchCriteria getCriteria() {
        return criteria;
    }

    /**
     * @param criteria the criteria to set
     */
    public void setCriteria(SearchCriteria criteria) {
        this.criteria = criteria;
    }

}
