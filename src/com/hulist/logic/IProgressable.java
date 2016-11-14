/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.logic;

import com.hulist.util.Progress;

/**
 *
 * @author Aleksander
 */
public interface IProgressable {
    public Progress getProgress();
    public void setProgress(Progress p);
}
