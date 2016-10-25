/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Aleksander
 */
public class Concurrent {
    
    public final static int CORES = Runtime.getRuntime().availableProcessors();
    public final static ExecutorService es = Executors.newFixedThreadPool(CORES);
    
}
