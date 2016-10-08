/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.util.log;

import java.io.IOException;
import java.io.OutputStream;
import javafx.scene.control.TextArea;

/**
 *
 * @author Aleksander
 */
public class TextAreaOutputStream extends OutputStream {

    private final TextArea textArea;

    public TextAreaOutputStream(TextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {
        textArea.appendText(String.valueOf((char) b));
    }
}
