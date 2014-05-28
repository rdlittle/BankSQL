/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.webfront.app.utils;

/**
 *
 * @author rlittle
 */
public class StringUtil {
    public static String fTrim(String str) {
        String regex = "^\\s+";
        String newString=str.replaceFirst(regex, "");
        return newString;
    }
}
