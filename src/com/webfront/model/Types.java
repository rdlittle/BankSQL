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
@Table(name = "types")
@NamedQueries({
    @NamedQuery(name = "Types.findAll", query = "SELECT t FROM Types t ORDER BY t.code,t.name")
    ,
    @NamedQuery(name = "Types.findAllByCode", query = "SELECT t FROM Types t WHERE t.code = :code")
    ,
    @NamedQuery(name = "Types.findByTypesCode", query = "SELECT t FROM Types t WHERE t.name = :name and t.code = :code"),})
public class Types implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Character code;
    
    @Column
    private String name;
    
    public Types() {
        
    }

    /**
     * @return the code
     */
    public Character getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(Character code) {
        this.code = code;
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
    
}
