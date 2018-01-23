/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.chronology.crn;

import com.hulist.logic.IColumnTypes;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public enum CrnColumnTypes implements IColumnTypes{
    SSFCRN,
    STDCRN,
    STDSTB,
    SSFSTB;/*,
    RAW,
    STD,
    RES,
    ARS;*/

    public static CrnColumnTypes fromString(String text) {
        for (CrnColumnTypes type : CrnColumnTypes.values()) {
            if (type.name().equalsIgnoreCase(text.trim())) {
                return type;
            }
        }
        return null;
    }
}
