/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class Misc {

    public static String stackTraceToString(Throwable e) {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

}
