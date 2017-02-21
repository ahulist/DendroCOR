/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.chronology.rcs;

import com.hulist.logic.chronology.tabs.TabsLineContainer;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class RcsLineContainer extends TabsLineContainer {

    public RcsLineContainer(int year, int num, double seg, double age, double raw, double std, double res, double ars) {
        super(year, num, seg, age, raw, std, res, ars);
    }

    public RcsLineContainer() {
        super();
    }
    
}
