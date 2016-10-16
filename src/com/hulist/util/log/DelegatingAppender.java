/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.util.log;

import ch.qos.logback.core.OutputStreamAppender;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author Aleksander
 * @param <E>
 */
public class DelegatingAppender<E> extends OutputStreamAppender<E> {
    
    private static final BlockingQueueOutput DELEGATING_OUTPUT_STREAM = new BlockingQueueOutput(new LinkedBlockingQueue<>(), new ByteArrayOutputStream());

    @Override
    public void start() {
        setOutputStream(DELEGATING_OUTPUT_STREAM);
        super.start();
    }
    
    public static BlockingQueueOutput getBlockingQ(){
        return DELEGATING_OUTPUT_STREAM;
    }

}
