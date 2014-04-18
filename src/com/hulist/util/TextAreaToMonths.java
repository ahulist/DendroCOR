/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.util;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class TextAreaToMonths {

    private final JTextArea area;
    private Logger log;
    private boolean isLoggingOn = true;

    public TextAreaToMonths(JTextArea a) {
        this.area = a;
        this.log = Logger.getLogger(this.getClass().getName());
    }

    public ArrayList<MonthsPair> getList() {
        /*String[] lines = area.getText().split("\\n");
         for( String line : lines) {
         if( line.matches("^([1-9]|1[0-2])-([1-9]|1[0-2])p*$")){
                
         }else{
         log.log(Level.WARNING, "Linia ");
         }
         }*/

        ArrayList<MonthsPair> list = new ArrayList<>();
        String[] lines = area.getText().split("\\n");
        for( String line : lines ) {
            String[] elems = line.trim().split("\\s");
            try {
                assert elems.length == 2 || elems.length == 3;
                int start = Integer.parseInt(elems[0]);
                int end = Integer.parseInt(elems[1]);
                int yearsShift = 0;
                assert start <= end;
                assert start >= 1 && start <= 12;
                assert end >= 1 && end <= 12;
                if( elems.length == 3 ){
                    yearsShift = Integer.parseInt(elems[2]);
                    assert yearsShift >= 0;
                }
                MonthsPair pair = new MonthsPair(Months.getMonth(start), Months.getMonth(end), yearsShift);
                list.add(pair);
            } catch( NumberFormatException | AssertionError e ) {
                if( !line.equals("") && !isLoggingOn ){
                    log.log(Level.WARNING, String.format("Zakres miesi\u0119cy \"%s\" nie jest poprawny.", line));
                }
            }
        }

        return list;
    }

    public void setIsLoggingOn(boolean isLoggingOn) {
        this.isLoggingOn = isLoggingOn;
    }
}
