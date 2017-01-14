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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
@Table(name = "ledger")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ledger.findAll", query = "SELECT l FROM Ledger l order by l.id desc"),
    @NamedQuery(name = "Ledger.findAllByType", query = "SELECT l FROM Ledger l WHERE l.account in (SELECT a FROM Account a where a.accountType = :accountType and a.accountStatus = :accountStatus) ORDER BY l.transDate DESC,l.id"),
    @NamedQuery(name = "Ledger.findById", query = "SELECT l FROM Ledger l WHERE l.id = :id"),
    @NamedQuery(name = "Ledger.findByTransDate", query = "SELECT l FROM Ledger l WHERE l.transDate = :transDate"),
    @NamedQuery(name = "Ledger.findByTransDesc", query = "SELECT l FROM Ledger l WHERE l.transDesc = :transDesc"),
    @NamedQuery(name = "Ledger.findByTransAmt", query = "SELECT l FROM Ledger l WHERE l.transAmt = :transAmt"),
    @NamedQuery(name = "Ledger.findByTransBal", query = "SELECT l FROM Ledger l WHERE l.transBal = :transBal"),
    @NamedQuery(name = "Ledger.findByPrimaryCat", query = "SELECT l FROM Ledger l WHERE l.primaryCat = :primaryCat"),
    @NamedQuery(name = "Ledger.findByCheckNum", query = "SELECT l FROM Ledger l WHERE l.checkNum = :checkNum"),
    @NamedQuery(name = "Ledger.findByAccountNum", query = "SELECT l FROM Ledger l WHERE l.account.id = :accountNum order by l.transDate desc,l.id desc"),
    @NamedQuery(name = "Ledger.findAllByDate", query = "SELECT l FROM Ledger l WHERE l.transDate BETWEEN :startDate AND :endDate"),
    @NamedQuery(name = "Ledger.findRangeByAccountNum", query = "SELECT l FROM Ledger l WHERE l.account.id = :accountNum AND l.transDate BETWEEN :startDate AND :endDate"),
    @NamedQuery(name = "Ledger.findRangeById", query = "SELECT l FROM Ledger l WHERE l.account = :accountNum and l.id BETWEEN :startId AND :endId ORDER BY l.transDate,l.id"),
    @NamedQuery(name = "Ledger.findRangeByDate", query = "SELECT l FROM Ledger l WHERE l.account = :accountNum and l.transDate BETWEEN :startDate AND :endDate ORDER BY l.transDate,l.id"),
    @NamedQuery(name = "Ledger.findRangeByTransAmt", query="SELECT l FROM Ledger l WHERE l.account = :accountNum and l.transAmt BETWEEN :minAmount AND :maxAmount ORDER BY l.transDate,l.id")})
public class Ledger implements Serializable {
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
    
    @Basic(optional = false)
    @Column(name = "transBal")
    private float transBal;
       
    @OneToOne
    @JoinColumn(name = "primaryCat")
    private Category primaryCat;
    
    @OneToOne
    @JoinColumn(name = "subCat")
    private Category subCat;    

    @Column(name = "checkNum")
    private String checkNum;
    
    @OneToOne
    @JoinColumn(name = "accountNum")
    private Account account;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy="ledgerEntry")
    private List<Payment> payments;
    
    public Ledger() {
        this.id=null;
        this.transDate=Calendar.getInstance().getTime();
        this.transAmt=0;
//        this.accountNum=1;
        this.transBal=0;
        this.transDesc="";
        this.checkNum="";
        this.payments=new ArrayList<>();
    }

    public Ledger(Integer id) {
        this.id = id;
        this.payments=new ArrayList<>();
    }

    public Ledger(Integer id, Date transDate, float transAmt, float transBal, Account acct) {
        this.id = id;
        this.transDate = transDate;
        this.transAmt = transAmt;
        this.transBal = transBal;
        this.account = acct;
        this.payments=new ArrayList<>();
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

    public float getTransBal() {
        return transBal;
    }

    public void setTransBal(float transBal) {
        this.transBal = transBal;
    }

    public Category getPrimaryCat() {
        return primaryCat;
    }
    
    public void setPrimaryCat(Category cat) {
        primaryCat = cat;
    }

    public Category getSubCat() {
        return subCat;
    }
    
    public void setSubCat(Category cat) {
        subCat = cat;
    }
    
    public String getCheckNum() {
        return checkNum;
    }

    public void setCheckNum(String checkNum) {
        this.checkNum = checkNum;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account acct) {
        this.account = acct;
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
        Ledger other = (Ledger) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.webfront.model.Ledger[ id=" + id + " ]";
    }

    /**
     * @return the receipt
     */
    public List<Payment> getPayment() {
        return payments;
    }

    /**
     * @param receipt the receipt to set
     */
    public void setPayment(List<Payment> receipt) {
        this.payments = receipt;
    }
}
