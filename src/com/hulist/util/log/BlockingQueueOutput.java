/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.util.log;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author Aleksander
 */
public class BlockingQueueOutput extends FilterOutputStream {

    private final BlockingQueue<String> q;
    private final ByteArrayOutputStream baos;

    public BlockingQueueOutput(BlockingQueue<String> q, ByteArrayOutputStream baos) {
        super(baos);
        this.baos = baos;
        this.q = q;
    }

    @Override
    public void write(int b) throws IOException {
        System.out.print((char) b);
        baos.write(b);
        if (b==10) {    // newline!
            q.add(baos.toString(StandardCharsets.UTF_8.name()));
            baos.reset();
        }
    }

    public BlockingQueue<String> getQ() {
        return q;
    }
}
