/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rlittle
 */
@Entity
@Table(name = "charges")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Charges.findAll", query = "SELECT c FROM Charges c"),
    @NamedQuery(name = "Charges.findById", query = "SELECT c FROM Charges c WHERE c.id = :id"),
    @NamedQuery(name = "Charges.findByTransDate", query = "SELECT c FROM Charges c WHERE c.transDate = :transDate"),
    @NamedQuery(name = "Charges.findByTransDesc", query = "SELECT c FROM Charges c WHERE c.transDesc = :transDesc"),
    @NamedQuery(name = "Charges.findByTransAmt", query = "SELECT c FROM Charges c WHERE c.transAmt = :transAmt"),
    @NamedQuery(name = "Charges.findByPrimaryCat", query = "SELECT c FROM Charges c WHERE c.primaryCat = :primaryCat"),
    @NamedQuery(name = "Charges.findByAccountNum", query = "SELECT c FROM Charges c WHERE c.accountNum = :accountNum")})
public class Charges implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "transDate")
    @Temporal(TemporalType.DATE)
    private Date transDate;
    @Column(name = "transDesc")
    private String transDesc;
    @Basic(optional = false)
    @Column(name = "transAmt")
    private float transAmt;
    @Column(name = "primaryCat")
    private Integer primaryCat;
    @Column(name = "accountNum")
    private Integer accountNum;

    public Charges() {
    }

    public Charges(Integer id) {
        this.id = id;
    }

    public Charges(Integer id, Date transDate, float transAmt) {
        this.id = id;
        this.transDate = transDate;
        this.transAmt = transAmt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getTransDate() {
        return transDate;
    }

    public void setTransDate(Date transDate) {
        this.transDate = transDate;
    }

    public String getTransDesc() {
        return transDesc;
    }

    public void setTransDesc(String transDesc) {
        this.transDesc = transDesc;
    }

    public float getTransAmt() {
        return transAmt;
    }

    public void setTransAmt(float transAmt) {
        this.transAmt = transAmt;
    }

    public Integer getPrimaryCat() {
        return primaryCat;
    }

    public void setPrimaryCat(Integer primaryCat) {
        this.primaryCat = primaryCat;
    }

    public Integer getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(Integer accountNum) {
        this.accountNum = accountNum;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Charges)) {
            return false;
        }
        Charges other = (Charges) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.webfront.app.Charges[ id=" + id + " ]";
    }
    
}
