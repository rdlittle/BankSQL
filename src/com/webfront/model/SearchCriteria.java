/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.model;

import java.time.LocalDate;

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

    public void SearchCriteria() {
        searchTarget = new String();
        startDate = new String();
        endDate = new String();
        primaryCat = new Category();
        setSecondaryCat(new Category());
        minAmount = new String();
        maxAmount = new String();
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
}
