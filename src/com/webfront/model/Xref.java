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
import javax.persistence.Table;

/**
 *
 * @author rlittle
 */
@Entity
@Table(name = "xref")
@NamedQueries({
    @NamedQuery(name = "Xref.findAll", query = "SELECT xref FROM Xref xref ORDER BY xref.type,xref.name")
    ,
    @NamedQuery(name = "Xref.findAllByType", query = "SELECT xref FROM Xref xref WHERE xref.type = :type")
    ,
    @NamedQuery(name = "Xref.findByXrefName", query = "SELECT xref FROM Xref xref WHERE xref.name = :name and xref.type = :type"),})

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
    private Integer cat1;

    @Column
    private Integer cat2;

    public Xref() {
        
    }
    
    public Xref(String n, Character t) {
        name = n;
        type = t;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
    public Integer getCat1() {
        return cat1;
    }

    /**
     * @param cat1 the fkey to set
     */
    public void setCat1(Integer cat1) {
        this.cat1 = cat1;
    }

    /**
     * @return the cat2
     */
    public Integer getCat2() {
        return cat2;
    }

    /**
     * @param cat2 the cat2 to set
     */
    public void setCat2(Integer cat2) {
        this.cat2 = cat2;
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

}
