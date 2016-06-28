/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.util;

import java.io.File;

/**
 * Utility class for displaying File in JList
 * @author Aleksander
 */
public class FileWrap extends File{

    public FileWrap(String pathname) {
        super(pathname);
    }
    
    public FileWrap(File file){
        super(file.getAbsolutePath());
    }

    @Override
    public String toString() {
        return getName();
        /*
        File parent = this.getParentFile();
        String desc = "...\\" + parent.getName() + "\\" + this.getName();
        if (parent.getName().equals("")) {
            desc = this.getAbsolutePath();
        }
        return desc;
        */
    }
    
    public String getTooltip(){
        return getAbsolutePath();
    }
}
