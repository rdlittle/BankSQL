/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rlittle
 */
@Entity
@Table(name = "payment")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Payment.findAll", query = "SELECT r FROM Payment r ORDER BY r.transDate DESC, r.id DESC"),
    @NamedQuery(name = "Payment.findAllByDate", query = "SELECT p FROM Payment p WHERE p.transDate BETWEEN :startDate AND :endDate"),
    @NamedQuery(name = "Payment.findOrphans", query = "SELECT r FROM Payment r WHERE r.ledgerEntry IS NULL ORDER BY r.transDate DESC, r.id DESC"),
    @NamedQuery(name = "Payment.findOrphansRange", query = "SELECT p FROM Payment p WHERE p.ledgerEntry IS NULL AND p.accountNum = :acctNum AND p.transDate BETWEEN :startDate AND :endDate"),
    @NamedQuery(name = "Payment.findById", query = "SELECT r FROM Payment r WHERE r.id = :id"),
    @NamedQuery(name = "Payment.findByTransDate", query = "SELECT r FROM Payment r WHERE r.transDate = :transDate"),
    @NamedQuery(name = "Payment.findByTransDesc", query = "SELECT r FROM Payment r WHERE r.transDesc = :transDesc"),
    @NamedQuery(name = "Payment.findBySubCat", query = "SELECT r FROM Payment r WHERE r.subCat = :subCat"),
    @NamedQuery(name = "Payment.findByAccountNum", query = "SELECT r FROM Payment r WHERE r.accountNum = :accountNum"),
    @NamedQuery(name = "Payment.findByTransAmt", query = "SELECT r FROM Payment r WHERE r.transAmt = :transAmt")})
public class Payment implements Serializable {

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

    @OneToOne
    @JoinColumn(name = "transId", referencedColumnName = "id")
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

    @Transient
    SimpleObjectProperty<Category> primaryCatProperty;

    public Payment() {
//        cat1 = new SimpleObjectProperty<>();
//        cat1.addListener(new CatChangeListener());
//        cat1.bind(new SimpleObjectProperty<Category>(primaryCat));
    }

    public Payment(Integer id) {
        this.id = id;
//        cat1 = new SimpleObjectProperty<>();
//        cat1.addListener(new CatChangeListener());
//        cat1.bind(new SimpleObjectProperty<Category>(primaryCat));
    }

    public Payment(Integer id, Date transDate, float transAmt) {
        this.id = id;
        this.transDate = transDate;
        this.transAmt = transAmt;
//        cat1 = new SimpleObjectProperty<>();
//        cat1.addListener(new CatChangeListener());
//        cat1.bind(new SimpleObjectProperty<Category>(primaryCat));
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
        this.ledgerEntry = entry;
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

    public static Comparator<Payment> PaymentComparator = new Comparator<Payment>() {
        @Override
        public int compare(Payment receipt1, Payment receipt2) {
            Long entry1 = receipt1.transDate.getTime();
            Long entry2 = receipt2.transDate.getTime();
            return entry2.compareTo(entry1);
        }
    };

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Payment)) {
            return false;
        }
        Payment other = (Payment) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.webfront.model.Payment[ id=" + id + " ]";
    }

    public static Payment copy(Payment p) {
        Payment copyOfPayment = new Payment();
        if (p != null) {
            copyOfPayment.setId(p.getId());
            copyOfPayment.setAccountNum(p.getAccountNum());
            copyOfPayment.setLedgerEntry(p.getLedgerEntry());
            copyOfPayment.setPrimaryCat(p.getPrimaryCat());
            copyOfPayment.setSubCat(p.getSubCat());
            copyOfPayment.setTransAmt(p.getTransAmt());
            copyOfPayment.setTransDate(p.getTransDate());
            copyOfPayment.setStore(p.getStore());
            copyOfPayment.setTransDesc(p.getTransDesc());
        }
        return copyOfPayment;
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
//        cat1.set(category1);
    }

    private class CatChangeListener implements ChangeListener {

        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            System.out.println(newValue.toString());
        }

    }

}
