/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author rlittle
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Xref.findAll", query = "SELECT xref FROM Xref xref"),
    @NamedQuery(name = "Xref.findAllByType", query = "SELECT xref FROM Xref xref WHERE xref.type = :type"),
    @NamedQuery(name = "Xref.findByXrefName", query = "SELECT xref FROM Xref xref WHERE xref.name = :name and xref.type = :type"),
    })
public class Xref implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Integer id;
    
    @Column
    private Character type;
    
    @Column 
    private String name;
    
    @Column
    private Integer fkey;
    

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Xref)) {
            return false;
        }
        Xref other = (Xref) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.webfront.model.Xref[ id=" + getId() + " ]";
    }

    /**
     * @return the type
     */
    public Character getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Character type) {
        this.type = type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the fkey
     */
    public Integer getFkey() {
        return fkey;
    }

    /**
     * @param fkey the fkey to set
     */
    public void setFkey(Integer fkey) {
        this.fkey = fkey;
    }
    
}
