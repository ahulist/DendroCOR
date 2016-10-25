/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hulist.logic;

import com.hulist.util.MonthsPair;
import com.hulist.util.Pair;
import java.util.HashMap;
import org.joda.time.MonthDay;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class DataToCorrelate {
    
    // MONTHLY
    Column primary;
    HashMap<MonthsPair, Column> climateColumns = new HashMap<>();
    
    // DAILY
    /**
     * Dla danego przedziału -> 2 kolumny do skorelowania:
     * w 1. dane chrono (primary), w 2. dane dzienne
     */
    HashMap<Pair<MonthDay, MonthDay>, Pair<Column, Column>> daily = new HashMap<>();

}
