/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.bean;

import com.webfront.model.ExportFormat;
import java.io.Serializable;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;
import javax.persistence.Query;

/**
 *
 * @author rlittle
 */
public class FormatManager extends DBManager<ExportFormat> implements Serializable {

    private static FormatManager instance;
    private static ObservableList<ExportFormat> formatList;

    protected FormatManager() {
        formatList = FXCollections.<ExportFormat>observableArrayList();
    }

    public static FormatManager getInstance() {
        if (instance == null) {
            instance = new FormatManager();
        }
        return instance;
    }

    private void setFormatList() {
        Query query = em.createNamedQuery("ExportFormat.findAll");
        List<ExportFormat> l = query.getResultList();
        formatList.setAll(l);
    }

    public ObservableList<ExportFormat> getFormatList() {
        if (formatList.isEmpty()) {
            setFormatList();
        }
        return formatList;
    }

    public static class ExportFormatConverter extends StringConverter {

        @Override
        public String toString(Object object) {
            ExportFormat ef = (ExportFormat) object;
            return ef.getDescription()+" ("+ef.getExtension()+")";
        }

        @Override
        public Object fromString(String string) {
            for (ExportFormat ef : formatList) {
                String s = ef.getDescription()+" ("+ef.getExtension()+")";
                if (s.equalsIgnoreCase(string)) {
                    return ef;
                }
            }
            return null;
        }

    }

    @Override
    public List<ExportFormat> getList(String s) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ObservableList<ExportFormat> doSqlQuery(String q) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
