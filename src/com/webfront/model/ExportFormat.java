/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rlittle
 */
@Entity
@Table(name = "formats")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ExportFormat.findAll", query = "SELECT f FROM ExportFormat f ORDER BY f.extension")
    })

public class ExportFormat implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type")
    @Basic(optional = false)
    private String extension;
    
    @Column
    private String description;
    
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the extension
     */
    public String getExtension() {
        return "*."+extension;
    }

    /**
     * @param extension the extension to set
     */
    public void setExtension(String extension) {
        this.extension = extension;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (extension != null ? extension.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExportFormat other = (ExportFormat) obj;

        return Objects.equals(this.extension, other.extension);
    }
    
    
    public static Comparator<ExportFormat> ExportFormatComparator = new Comparator<ExportFormat>() {
        public int compare(ExportFormat ef1, ExportFormat ef2) {
            String s1 = ef1.getDescription();
            String s2 = ef2.getDescription();
            return s1.compareTo(s2);
        }
    };

}
