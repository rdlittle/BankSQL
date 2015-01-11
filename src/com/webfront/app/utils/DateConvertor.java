/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.app.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;

/**
 *
 * @author rlittle
 */
public final class DateConvertor {

    public static String convert(String text) throws ParseException {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        java.util.Date inDate = inputDateFormat.parse(text);
        Calendar cal = Calendar.getInstance();
        cal.setTime(inDate);
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH);
        int d = cal.get(Calendar.DAY_OF_MONTH);
        LocalDate lDate = LocalDate.of(y, m, d);
        return lDate.toString();
    }

    public static Long toLong(String text) throws ParseException {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        java.util.Date inDate = inputDateFormat.parse(text);
        Calendar cal = Calendar.getInstance();
        cal.setTime(inDate);
        return cal.getTimeInMillis();
    }

    public static Long toLong(String text, String regex) throws ParseException {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat(regex);
        java.util.Date inDate = inputDateFormat.parse(text);
        Calendar cal = Calendar.getInstance();
        cal.setTime(inDate);
        return cal.getTimeInMillis();
    }    
    /*
     * @param LocalDate ld
     * @return java.util.Date
     */
    public static java.util.Date fromLocalDate(LocalDate ld) {
        java.sql.Date d = java.sql.Date.valueOf(ld);
        long t = d.getTime();
        java.util.Date d2 = Calendar.getInstance().getTime();
        d2.setTime(t);
        return d2;
    }

    public static LocalDate toLocalDate(java.sql.Date d) {
        return d.toLocalDate();
    }

    public static LocalDate toLocalDate(java.util.Date d) {
        long time = d.getTime();
        java.sql.Date dt = new java.sql.Date(time);
        LocalDate ld = dt.toLocalDate();
        return ld;
    }

    public static java.util.Date toUtilDate(java.sql.Date sqlDate) {
        long time = sqlDate.getTime();
        java.util.Date utilDate = new java.util.Date(time);
        return utilDate;
    }

    public static java.sql.Date toSQLDate(java.util.Date utilDate) {
        long time = utilDate.getTime();
        return new java.sql.Date(time);
    }
}
