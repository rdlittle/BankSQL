/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.app.bank.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author rlittle
 */
public final class DateConvertor {

    public static String convert(String text) throws ParseException {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        Date inDate = inputDateFormat.parse(text);
        Calendar cal = Calendar.getInstance();
        cal.setTime(inDate);
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH);
        int d = cal.get(Calendar.DAY_OF_MONTH);
        LocalDate lDate = LocalDate.of(y, m, d);
        return lDate.toString();
    }
}
