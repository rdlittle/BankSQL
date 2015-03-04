/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
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
public class Charges extends Entry implements Serializable {
    private static final long serialVersionUID = 1L;
    public Charges() {
    }
}
