/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.webfront.model;

import java.io.Serializable;
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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rlittle
 */
@Entity
@Table(name = "distribution")
//@DiscriminatorColumn(name = "accountNum", discriminatorType = DiscriminatorType.INTEGER)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Distribution.findAll", query = "SELECT d FROM Distribution d"),
    @NamedQuery(name = "Distribution.findById", query = "SELECT d FROM Distribution d WHERE d.id = :id"),
//    @NamedQuery(name = "Distribution.findByTransId", query = "SELECT d FROM Distribution d WHERE d.transId = :transId"),
//    @NamedQuery(name = "Distribution.findByCategoryId", query = "SELECT d FROM Distribution d WHERE d.categoryId = :categoryId"),
    @NamedQuery(name = "Distribution.findByAccountNum", query = "SELECT d FROM Distribution d WHERE d.accountNum = :accountNum")})
public class Distribution implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne(optional=false)
    @JoinColumn(name="transId",referencedColumnName = "id")
    private Ledger ledger;
    
    @OneToOne
    @JoinColumn(name = "categoryId")
    private Category category;

    @Column(name = "accountNum")
    private Integer accountNum;
    
    public Distribution() {
    }

    public Distribution(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category cat) {
        this.category = cat;
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
        if (!(object instanceof Distribution)) {
            return false;
        }
        Distribution other = (Distribution) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.webfront.model.Distribution[ id=" + id + " ]";
    }

    /**
     * @return the ledgerTransaction
     */
    public Ledger getLedger() {
        return ledger;
    }

    /**
     * @param ledger the Ledger item to set
     */
    public void setLedger(Ledger ledger) {
        this.ledger = ledger;
    }
}
