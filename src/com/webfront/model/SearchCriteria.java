/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author rlittle
 */
public class SearchCriteria {

    private String startDate;
    private String endDate;
    private String searchTarget;
    private String minAmount;
    private String maxAmount;
    private Category primaryCat;
    private Category secondaryCat;
    private String sqlStmt;
    private SimpleStringProperty sqlProperty;
    private Integer storeId;
    private String amount;
    private String date;
    private Float beginningBalance;
    private LocalDate[] dateRange;
    private int account;
    private int[] checkRange;
    private SimpleStringProperty payeeProperty;
    private SimpleObjectProperty<Account> accountProperty;
    private SimpleObjectProperty<Category> primaryCatProperty;
    private SimpleObjectProperty<Category> secondaryCatProperty;
    private SimpleObjectProperty<LocalDate> startDateProperty;
    private SimpleObjectProperty<LocalDate> targetDateProperty;
    private SimpleObjectProperty<LocalDate> endDateProperty;
    private SimpleObjectProperty<Stores> storeProperty;
    private SimpleStringProperty chkStartProperty;
    private SimpleStringProperty chkEndProperty;
    private SimpleStringProperty minAmountProperty;
    private SimpleStringProperty maxAmountProperty;
    /**
     *
     */
    public SearchCriteria() {
        searchTarget = new String();
        startDate = new String();
        endDate = new String();
        primaryCat = new Category();
        secondaryCat = new Category();
        minAmount = new String();
        maxAmount = new String();
        sqlProperty = new SimpleStringProperty();
        sqlProperty.setValue("");
        beginningBalance = new Float(0);
        dateRange = new LocalDate[2];
        storeId = null;
        account = 0;
        checkRange = new int[2];
        
        payeeProperty = new SimpleStringProperty();
        accountProperty = new SimpleObjectProperty<>();
        primaryCatProperty = new SimpleObjectProperty<>();
        secondaryCatProperty = new SimpleObjectProperty<>();
        startDateProperty = new SimpleObjectProperty<>();
        endDateProperty = new SimpleObjectProperty<>();
        storeProperty = new SimpleObjectProperty<>();
        targetDateProperty = new SimpleObjectProperty<>();
        chkStartProperty = new SimpleStringProperty();
        chkEndProperty = new SimpleStringProperty();
        minAmountProperty = new SimpleStringProperty();
        maxAmountProperty = new SimpleStringProperty();
    }

    public void validateRange(LocalDate sDate, LocalDate eDate) throws Exception {
        if (sDate == null || eDate == null) {
            return;
        }
        if (sDate.isAfter(eDate)) {
            throw new Exception("Start date must be before end date");
        }
    }

    /**
     * @return the startDate
     */
    public String getStartDate() {
        return startDate;
    }

    public Date asDate(LocalDate ld) {
        Instant instant = ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the searchTarget
     */
    public String getSearchTarget() {
        return searchTarget;
    }

    /**
     * @param searchTarget the searchTarget to set
     */
    public void setSearchTarget(String searchTarget) {
        this.searchTarget = searchTarget;
    }

    /**
     * @return the primaryCat
     */
    public Category getPrimaryCat() {
        return primaryCat;
    }

    /**
     * @param primaryCat the primaryCat to set
     */
    public void setPrimaryCat(Category primaryCat) {
        this.primaryCat = primaryCat;
    }

    /**
     * @return the minAmount
     */
    public String getMinAmount() {
        return minAmount;
    }

    /**
     * @param minAmount the minAmount to set
     */
    public void setMinAmount(String minAmount) {
        this.minAmount = minAmount;
    }

    /**
     * @return the maxAmount
     */
    public String getMaxAmount() {
        return maxAmount;
    }

    /**
     * @param maxAmount the maxAmount to set
     */
    public void setMaxAmount(String maxAmount) {
        this.maxAmount = maxAmount;
    }

    @Override
    public String toString() {
        String str = "searchTarget: " + searchTarget + ", ";
        str += "startDate: " + startDate + ", ";
        str += "endDate: " + endDate + ", ";
        str += "minAmt: " + minAmount + ", ";
        str += "maxAmt: " + maxAmount + ", ";
        str += "primaryCat: " + getPrimaryCat().getDescription() + ", ";
        str += "secondaryCat: " + getSecondaryCat().getDescription();
        return str;
    }

    /**
     * @return the secondaryCat
     */
    public Category getSecondaryCat() {
        return secondaryCat;
    }

    /**
     * @param secondaryCat the secondaryCat to set
     */
    public void setSecondaryCat(Category secondaryCat) {
        this.secondaryCat = secondaryCat;
    }

    /**
     * @return the sqlStmt
     */
    public String getSqlStmt() {
        return sqlStmt;
    }

    /**
     * @param sqlStmt the sqlStmt to set
     */
    public void setSqlStmt(String sqlStmt) {
        this.sqlStmt = sqlStmt;
    }

    /**
     * @return the sqlProperty
     */
    public SimpleStringProperty getSqlProperty() {
        return sqlProperty;
    }

    /**
     * @param sqlProperty the sqlProperty to set
     */
    public void setSqlProperty(SimpleStringProperty sqlProperty) {
        this.sqlProperty = sqlProperty;
    }

    /**
     * @return the storeId
     */
    public Integer getStoreId() {
        return storeId;
    }

    /**
     * @param storeId the storeId to set
     */
    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    /**
     * @return the amount
     */
    public String getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(String amount) {
        this.amount = amount;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return targetDateProperty.getValue().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return the beginningBalance
     */
    public Float getBeginningBalance() {
        return beginningBalance;
    }

    /**
     * @param beginningBalance the beginningBalance to set
     */
    public void setBeginningBalance(Float beginningBalance) {
        this.beginningBalance = beginningBalance;
    }

    /**
     * @return the dateRange
     */
    public LocalDate[] getDateRange() {
        return dateRange;
    }

    /**
     * @param dateRange the dateRange to set
     */
    public void setDateRange(LocalDate[] dateRange) {
        this.dateRange = dateRange;
    }

    /**
     * @return the account
     */
    public int getAccount() {
        return account;
    }

    /**
     * @param account the account to set
     */
    public void setAccount(int account) {
        this.account = account;
    }

    /**
     * @return the checkRange
     */
    public int[] getCheckRange() {
        return checkRange;
    }

    /**
     * @param checkRange the checkRange to set
     */
    public void setCheckRange(int[] checkRange) {
        this.checkRange = checkRange;
    }

    /**
     * @return the payee
     */
    public SimpleStringProperty getPayeeProperty() {
        return payeeProperty;
    }

    /**
     * @param payeeProperty the payee to set
     */
    public void setPayeeProperty(SimpleStringProperty payeeProperty) {
        this.payeeProperty = payeeProperty;
    }

    /**
     * @return the accountProperty
     */
    public SimpleObjectProperty<Account> getAccountProperty() {
        return accountProperty;
    }

    /**
     * @param accountProperty the accountProperty to set
     */
    public void setAccountProperty(SimpleObjectProperty<Account> accountProperty) {
        this.accountProperty = accountProperty;
    }

    /**
     * @return the primaryCatProperty
     */
    public SimpleObjectProperty<Category> getPrimaryCatProperty() {
        return primaryCatProperty;
    }

    /**
     * @param primaryCatProperty the primaryCatProperty to set
     */
    public void setPrimaryCatProperty(SimpleObjectProperty<Category> primaryCatProperty) {
        this.primaryCatProperty = primaryCatProperty;
    }

    /**
     * @return the secondaryCatProperty
     */
    public SimpleObjectProperty<Category> getSecondaryCatProperty() {
        return secondaryCatProperty;
    }

    /**
     * @param secondaryCatProperty the secondaryCatProperty to set
     */
    public void setSecondaryCatProperty(SimpleObjectProperty<Category> secondaryCatProperty) {
        this.secondaryCatProperty = secondaryCatProperty;
    }

    /**
     * @return the startDateProperty
     */
    public SimpleObjectProperty<LocalDate> getStartDateProperty() {
        return startDateProperty;
    }

    /**
     * @param startDateProperty the startDateProperty to set
     */
    public void setStartDateProperty(SimpleObjectProperty<LocalDate> startDateProperty) {
        this.startDateProperty = startDateProperty;
    }

    /**
     * @return the endDateProperty
     */
    public SimpleObjectProperty<LocalDate> getEndDateProperty() {
        return endDateProperty;
    }

    /**
     * @param endDateProperty the endDateProperty to set
     */
    public void setEndDateProperty(SimpleObjectProperty<LocalDate> endDateProperty) {
        this.endDateProperty = endDateProperty;
    }

    /**
     * @return the storeProperty
     */
    public SimpleObjectProperty<Stores> getStoreProperty() {
        return storeProperty;
    }

    /**
     * @param storeProperty the storeProperty to set
     */
    public void setStoreProperty(SimpleObjectProperty<Stores> storeProperty) {
        this.storeProperty = storeProperty;
    }

    /**
     * @return the minAmountProperty
     */
    public SimpleStringProperty getMinAmountProperty() {
        return minAmountProperty;
    }

    /**
     * @param minAmountProperty the minAmountProperty to set
     */
    public void setMinAmountProperty(SimpleStringProperty minAmountProperty) {
        this.minAmountProperty = minAmountProperty;
    }

    /**
     * @return the maxAmountProperty
     */
    public SimpleStringProperty getMaxAmountProperty() {
        return maxAmountProperty;
    }

    /**
     * @param maxAmountProperty the maxAmountProperty to set
     */
    public void setMaxAmountProperty(SimpleStringProperty maxAmountProperty) {
        this.maxAmountProperty = maxAmountProperty;
    }

    /**
     * @return the chkStartProperty
     */
    public SimpleStringProperty getChkStartProperty() {
        return chkStartProperty;
    }

    /**
     * @param chkStartProperty the chkStartProperty to set
     */
    public void setChkStartProperty(SimpleStringProperty chkStartProperty) {
        this.chkStartProperty = chkStartProperty;
    }

    /**
     * @return the chkEndProperty
     */
    public SimpleStringProperty getChkEndProperty() {
        return chkEndProperty;
    }

    /**
     * @param chkEndProperty the chkEndProperty to set
     */
    public void setChkEndProperty(SimpleStringProperty chkEndProperty) {
        this.chkEndProperty = chkEndProperty;
    }

    /**
     * @return the targetDateProperty
     */
    public SimpleObjectProperty<LocalDate> getTargetDateProperty() {
        return targetDateProperty;
    }

    /**
     * @param targetDateProperty the targetDateProperty to set
     */
    public void setTargetDateProperty(SimpleObjectProperty<LocalDate> targetDateProperty) {
        this.targetDateProperty = targetDateProperty;
    }

}
