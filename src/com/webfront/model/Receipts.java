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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rlittle
 */
@Entity
@Table(name = "receipts")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Receipts.findAll", query = "SELECT r FROM Receipts r ORDER BY r.transDate DESC"),
    @NamedQuery(name = "Receipts.findById", query = "SELECT r FROM Receipts r WHERE r.id = :id"),
    @NamedQuery(name = "Receipts.findByTransDate", query = "SELECT r FROM Receipts r WHERE r.transDate = :transDate"),
    @NamedQuery(name = "Receipts.findByTransDesc", query = "SELECT r FROM Receipts r WHERE r.transDesc = :transDesc"),
    @NamedQuery(name = "Receipts.findBySubCat", query = "SELECT r FROM Receipts r WHERE r.subCat = :subCat"),
    @NamedQuery(name = "Receipts.findByAccountNum", query = "SELECT r FROM Receipts r WHERE r.accountNum = :accountNum"),
    @NamedQuery(name = "Receipts.findByTransAmt", query = "SELECT r FROM Receipts r WHERE r.transAmt = :transAmt")})
public class Receipts implements Serializable {

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
    
    @ManyToOne(optional=false)
    @JoinColumn(name = "transId", referencedColumnName="id")
    private Ledger ledgerEntry;

    @OneToOne
    @JoinColumn(name = "storeId")
    private Stores store;

    @OneToOne
    @JoinColumn(name = "primaryCat")
    private Category primaryCat;

    @OneToOne
    @JoinColumn(name = "subCat")
    private Category subCat;

    @Column(name = "accountNum")
    private Integer accountNum;
    
    @Basic(optional = false)
    @Column(name = "transAmt")
    private float transAmt;

    public Receipts() {
    }

    public Receipts(Integer id) {
        this.id = id;
    }

    public Receipts(Integer id, Date transDate, float transAmt) {
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

    public Ledger getLedgerEntry() {
        return this.ledgerEntry;
    }
    
    public void setLedgerEntry(Ledger entry) {
        this.ledgerEntry=entry;
    }

    public Stores getStore() {
        return store;
    }

    public void setStore(Stores store) {
        this.store = store;
    }

    public Category getSubCat() {
        return subCat;
    }

    public void setSubCat(Category subCat) {
        this.subCat = subCat;
    }

    public Integer getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(Integer accountNum) {
        this.accountNum = accountNum;
    }

    public float getTransAmt() {
        return transAmt;
    }

    public void setTransAmt(float transAmt) {
        this.transAmt = transAmt;
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
        if (!(object instanceof Receipts)) {
            return false;
        }
        Receipts other = (Receipts) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.webfront.model.Receipts[ id=" + id + " ]";
    }

    /**
     * @return the category1
     */
    public Category getPrimaryCat() {
        return primaryCat;
    }

    /**
     * @param category1 the category1 to set
     */
    public void setPrimaryCat(Category category1) {
        this.primaryCat = category1;
    }

}
