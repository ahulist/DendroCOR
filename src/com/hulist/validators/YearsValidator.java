/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.validators;

import com.hulist.gui2.GUIMain;
import java.util.Calendar;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class YearsValidator {

    public static final int YEAR_MIN = 1000;
    public static final int YEAR_MAX = Calendar.getInstance().get(Calendar.YEAR) + 100;

    Logger log = LoggerFactory.getLogger(YearsValidator.class);

    /**
     * Full validation
     *
     * @param yearStart
     * @param yearEnd
     * @param silent
     * @return
     */
    public boolean validateRange(String yearStart, String yearEnd, boolean silent) {
        boolean result;

        if (!(validateSingleYear(yearStart, silent) & validateSingleYear(yearEnd, silent))) {
            result = false;
        } else {
            result = validateOrder(Integer.parseInt(yearStart), Integer.parseInt(yearEnd), silent);
        }

        return result;
    }

    /**
     * Checks whether year is an integer and it does not exceed YEAR_MIN and
     * YEAR_MAX
     *
     * @param year
     * @param silent
     * @return
     */
    public boolean validateSingleYear(String year, boolean silent) {
        boolean result = true;
        int num = -1;

        try {
            num = Integer.parseInt(year);
        } catch (NumberFormatException e) {
            if (!silent) {
                log.warn(String.format(ResourceBundle.getBundle(GUIMain.BUNDLE, GUIMain.getCurrLocale()).getString("Liczba niecałkowita"),year));
            }
            result = false;
            return result;
        }

        if (num < YEAR_MIN) {
            if (!silent) {
                log.warn(String.format(ResourceBundle.getBundle(GUIMain.BUNDLE, GUIMain.getCurrLocale()).getString("Rok %d musi być większy niż %d"), num,YEAR_MIN));
            }
            result = false;
        } else if (num > YEAR_MAX) {
            if (!silent) {
                log.warn(String.format(ResourceBundle.getBundle(GUIMain.BUNDLE, GUIMain.getCurrLocale()).getString("Rok %d musi być mniejszy niż %d"), num, YEAR_MAX));
            }
            result = false;
        }

        return result;
    }

    /**
     * Checks if endYead is after startYear
     *
     * @param yearStart
     * @param yearEnd
     * @return
     */
    private boolean validateOrder(int yearStart, int yearEnd, boolean silent) {
        boolean result = true;

        if (yearStart > yearEnd) {
            result = false;
            if (!silent) {
                log.warn(ResourceBundle.getBundle(GUIMain.BUNDLE, GUIMain.getCurrLocale()).getString("Rok początkowy musi być mniejszy niż rok końcowy"));
            }
        }

        return result;
    }
}
