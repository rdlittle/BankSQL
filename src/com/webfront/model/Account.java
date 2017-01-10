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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rlittle
 */
@Entity
@Table(name = "account")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Account.findAll", query = "SELECT acct FROM Account acct"),
    @NamedQuery(name = "Account.findById", query = "SELECT acct FROM Account acct WHERE acct.id = :id"),
    @NamedQuery(name = "Account.findByAccountName", query = "SELECT acct FROM Account acct WHERE acct.accountName = :accountName"),
    @NamedQuery(name = "Account.findByBankName", query = "SELECT acct FROM Account acct WHERE acct.bankName = :bankName"),
    })
public class Account implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Column
    private String accountName;
    @Column
    private String accountNumber;
    @Column
    private String bankName;
    @Column
    private String routingNumber;
    @Column
    private String address1;
    @Column
    private String address2;
    @Column
    private String city;
    @Column(name = "state")
    private String stateAbbr;
    @Column
    private String postalCode;
    @Column
    private String phoneNumber;
    
    @Column
    @Enumerated(EnumType.ORDINAL)
    private AccountStatus accountStatus;
    
    @Column
    @Enumerated(EnumType.ORDINAL)
    private AccountType accountType;
    
    @Column
    @Enumerated(EnumType.STRING)
    private StatementFormat statementFormat;
    
    @Column
    private String configName;
    
    @Column
    private boolean xlateStore;
    
    @Column boolean xlateCat;

    public static enum AccountType {
        CHECKING, SAVINGS, CREDIT;
    }

    public static enum StatementFormat {
        CSV, TAB, FIXED, PDF, QIF;
    }
    
    public static enum AccountStatus {
        ACTIVE,INACTIVE,CLOSED;
    }

    /**
     *
     * @return accountName
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     *
     * @param name The name of the account. For example: MyBank Preferred Checking
     */
    public void setAccountName(String name) {
        accountName = name;
    }

    /**
     * @return the accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * @param accountNumber the accountNumber to set
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * @return the bankName
     */
    public String getBankName() {
        return bankName;
    }

    /**
     * @param bankName the bankName to set
     */
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    /**
     * @return the routingNumber
     */
    public String getRoutingNumber() {
        return routingNumber;
    }

    /**
     * @param routingNumber the routingNumber to set
     */
    public void setRoutingNumber(String routingNumber) {
        this.routingNumber = routingNumber;
    }

    /**
     * @return the address2
     */
    public String getAddress2() {
        return address2;
    }

    /**
     * @param address2 the address2 to set
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return the state
     */
    public String getStateAbbr() {
        return stateAbbr;
    }

    /**
     * @param state the state to set
     */
    public void setStateAbbr(String state) {
        this.stateAbbr = state;
    }

    /**
     * @return the postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @param postalCode the postalCode to set
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber the phoneNumber to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return the address1
     */
    public String getAddress1() {
        return address1;
    }

    /**
     * @param address1 the address1 to set
     */
    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    /**
     * @return the accountStatus
     */
    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    /**
     * @param accountStatus the accountStatus to set
     */
    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the accountType
     */
    public AccountType getAccountType() {
        return accountType;
    }

    /**
     * @param accountType the accountType to set
     */
    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    /**
     * @return the statementFormat
     */
    public StatementFormat getStatementFormat() {
        return statementFormat;
    }

    /**
     * @param statementFormat the statementFormat to set
     */
    public void setStatementFormat(StatementFormat statementFormat) {
        this.statementFormat = statementFormat;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.id.toString());
        sb.append(" ");
        sb.append(this.accountName);
        sb.append(" ");
        sb.append(this.accountNumber);
        sb.append(" ");
        sb.append(this.bankName);
        sb.append(" ");
        sb.append(this.routingNumber);
        sb.append(" ");
        sb.append(this.accountType.toString());
        sb.append(" ");
        sb.append(this.accountStatus.toString());
        return sb.toString();
    }

    /**
     * @return the configName
     */
    public String getConfigName() {
        return configName;
    }

    /**
     * @param configName the configName to set
     */
    public void setConfigName(String configName) {
        this.configName = configName;
    }

    /**
     * @return the zlateStore
     */
    public boolean isXlateStore() {
        return xlateStore;
    }

    /**
     * @param xlateStore the translateStoreName to set
     */
    public void setXlateStore(boolean xlateStore) {
        this.xlateStore = xlateStore;
    }
    
    public boolean isXlateCat() {
        return this.xlateCat;
    }
    
    public void setXlateCat(boolean b) {
        this.xlateCat = b;
    }

}
