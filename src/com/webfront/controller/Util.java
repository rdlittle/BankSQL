/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.webfront.controller;

import java.time.LocalDate;
import java.sql.Date;
import java.util.Calendar;

/**
 *
 * @author rlittle
 */
public class Util {
    
    public static java.util.Date fromLocalDate(LocalDate ld) {
        Date d = Date.valueOf(ld);
        long t=d.getTime();
        java.util.Date d2=Calendar.getInstance().getTime();
        d2.setTime(t);
        return d2;
    }
    
    public static LocalDate toLocalDate(java.sql.Date d) {
        return d.toLocalDate();
    }
    
    public static LocalDate toLocalDate(java.util.Date d) {
        long time=d.getTime();
        Date dt=new Date(time);
        LocalDate ld=dt.toLocalDate();
        return ld;
    }
    
}
