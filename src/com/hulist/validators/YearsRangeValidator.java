/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hulist.validators;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class YearsRangeValidator {
    
    public static boolean validate(int yearStart, int yearEnd){
        boolean result = true;
        
        if( yearStart > yearEnd ){
            result = false;
        }
        
        return result;
    }
    
    public static boolean validate(String yearStart, String yearEnd){
        boolean result = true;
        
        if( !(YearValidator.validate(yearStart) && YearValidator.validate(yearEnd)) ){
            result = false;
        }else{
            result = validate(Integer.parseInt(yearStart), Integer.parseInt(yearEnd));
        }
        
        return result;
    }
}
