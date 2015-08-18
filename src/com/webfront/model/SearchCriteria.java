/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
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
    private String storeId;
    private String amount;
    private String date;
    private Float beginningBalance;
    private LocalDate[] dateRange;

    /**
     *
     */
    public SearchCriteria() {
        searchTarget = new String();
        startDate = new String();
        endDate = new String();
        primaryCat = new Category();
        secondaryCat=new Category();
        minAmount = new String();
        maxAmount = new String();
        sqlProperty = new SimpleStringProperty();
        sqlProperty.setValue("");
        beginningBalance = new Float(0);
        dateRange = new LocalDate[2];
    }

    public void validateRange(LocalDate sDate, LocalDate eDate) throws Exception {
        if(sDate==null || eDate==null) {
            return;
        }
        if(sDate.isAfter(eDate)) {
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
        str += "primaryCat: " + getPrimaryCat().getDescription()+", ";
        str += "secondaryCat: "+getSecondaryCat().getDescription();
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
    public String getStoreId() {
        return storeId;
    }

    /**
     * @param storeId the storeId to set
     */
    public void setStoreId(String storeId) {
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
        return date;
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
}
