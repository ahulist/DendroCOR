/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.util.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.spi.FilterReply;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Aleksander
 */
public class AllFilter extends ch.qos.logback.core.filter.AbstractMatcherFilter {

    @Override
    public FilterReply decide(Object event) {
        if (!isStarted()) {
            return FilterReply.NEUTRAL;
        }

        LoggingEvent loggingEvent = (LoggingEvent) event;

        List<Level> eventsToKeep = Arrays.asList(Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR);
        if (eventsToKeep.contains(loggingEvent.getLevel())) {
            return FilterReply.NEUTRAL;
        } else {
            return FilterReply.DENY;
        }
    }

}
