/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hulist.validators;

import java.util.Calendar;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class YearValidator implements IValidator{
    
    public static final int YEAR_MIN = 1000;
    public static final int YEAR_MAX = Calendar.getInstance().get(Calendar.YEAR)+100;
    
    public static boolean validate(String year){
        boolean result = true;
        int num = -1;
        
        try {
            num = Integer.parseInt(year);
        } catch( NumberFormatException e ) {
            result = false;
        }
        
        if( num<YEAR_MIN || num>YEAR_MAX ){
            result = false;
        }
        
        return result;
    }
}
