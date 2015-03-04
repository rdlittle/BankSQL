/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author rlittle
 */
@Entity
public class Entry implements Serializable {
   private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
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
       
    @OneToOne
    @JoinColumn(name = "primaryCat")
    private Category primaryCat;
    
    @Basic(optional = false)
    @Column(name = "accountNum")
    private int accountNum;
    
    @OneToMany(cascade = CascadeType.REFRESH, mappedBy="ledger")
    private List<Distribution> distribution;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy="ledgerEntry")
    private List<Receipts> receipts;
    
    public Entry() {
        this.id=null;
        this.distribution=new ArrayList<>();
        this.transDate=Calendar.getInstance().getTime();
        this.transAmt=0;
        this.accountNum=1;
        this.transDesc="";
        this.receipts=new ArrayList<>();
    }

    public Entry(Integer id) {
        this.id = id;
    }

    public Entry(Integer id, Date transDate, float transAmt, float transBal, int accountNum) {
        this.id = id;
        this.transDate = transDate;
        this.transAmt = transAmt;
        this.accountNum = accountNum;
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

    public Category getPrimaryCat() {
        return primaryCat;
    }
    
    public void setPrimaryCat(Category cat) {
        primaryCat = cat;
    }

    public int getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(int accountNum) {
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
        if (!(object instanceof Ledger)) {
            return false;
        }
        Entry other = (Entry) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.webfront.model.Entry[ id=" + id + " ]";
    }

    /**
     * @return the distributionList
     */
    public List<Distribution> getDistribution() {
        return distribution;
    }

    /**
     * @param distributionList the distributionList to set
     */
    public void setDistribution(List<Distribution> distributionList) {
        this.distribution = distributionList;
    }

    /**
     * @return the receipt
     */
    public List<Receipts> getReceipts() {
        return receipts;
    }

    /**
     * @param receipt the receipt to set
     */
    public void setReceipts(List<Receipts> receipt) {
        this.receipts = receipt;
    }

}
