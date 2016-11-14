/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic.daily.Y_M_D_V;

import com.hulist.logic.daily.DailyFileDataContainer;
import com.hulist.util.Progress;
import java.io.File;

/**
 *
 * @author Aleksander
 */
public class Y_M_D_VDataContainer extends DailyFileDataContainer<Y_M_D_VLineContainer> {

    public Y_M_D_VDataContainer(File sourceFile) {
        super(sourceFile);
    }

    @Override
    public boolean isEmpty() {
        return container.isEmpty();
    }

    @Override
    public Progress getProgress() {
        return this.progress;
    }

    @Override
    public void setProgress(Progress p) {
        this.progress = p;
    }
}
