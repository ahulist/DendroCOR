/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.util.log;

import com.hulist.util.Misc;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Aleksander
 */
public class BlockingQueueOutput extends FilterOutputStream {

    private final BlockingQueue<String> q;
    private final ByteArrayOutputStream baos;
    private static TextArea ta;

    Logger log = LoggerFactory.getLogger(BlockingQueueOutput.class);

    public BlockingQueueOutput(BlockingQueue<String> q, ByteArrayOutputStream baos) {
        super(baos);
        this.baos = baos;
        this.q = q;
    }

    @Override
    public void write(int b) throws IOException {
        System.out.print((char) b);
        baos.write(b);
        if (b == 10) {    // newline!
            if (BlockingQueueOutput.ta != null) {
                final String msg = baos.toString(StandardCharsets.UTF_8.name());
                Platform.runLater(() -> {
                    BlockingQueueOutput.ta.appendText(msg);
                });
            }
            baos.reset();
        }
    }

    public BlockingQueue<String> getQ() {
        return q;
    }

    public static void setTextArea(TextArea ta) {
        BlockingQueueOutput.ta = ta;
    }
}
