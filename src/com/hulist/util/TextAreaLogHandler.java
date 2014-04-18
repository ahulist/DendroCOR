/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.util;

import java.awt.Color;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author Aleksander Hulist <aleksander.hulist@gmail.com>
 */
public class TextAreaLogHandler extends Handler {

    private static TextAreaLogHandler INSTANCE;
    private Level level = Level.FINEST;

    JTextPane dest;
    StyledDocument doc;

    private TextAreaLogHandler() {
    }

    public static TextAreaLogHandler getInstance() {
        if( INSTANCE == null ){
            INSTANCE = new TextAreaLogHandler();
        }
        return INSTANCE;
    }

    public void setTextArea(JTextPane dest) {
        this.dest = dest;
        this.doc = dest.getStyledDocument();
    }

    @Override
    public void publish(LogRecord record) {
        if( record.getLevel().intValue() >= getLoggingLevel().intValue() ){
            try {
                SimpleAttributeSet as = new SimpleAttributeSet();

                // SEVERE
                if( record.getLevel().equals(Level.SEVERE) ){
                    StyleConstants.setForeground(as, Color.red);
                    StyleConstants.setBold(as, true);

                    // WARNING
                } else if( record.getLevel().equals(Level.WARNING) ){
                    StyleConstants.setForeground(as, Color.red);

                    // INFO
                } else if( record.getLevel().equals(Level.INFO) ){
                    StyleConstants.setForeground(as, Color.BLACK);

                    // FINE
                } else if( record.getLevel().equals(Level.FINE) ){
                    StyleConstants.setForeground(as, Color.getHSBColor(0.59f, 1, 0.74f));

                    // FINER
                } else if( record.getLevel().equals(Level.FINER) ){
                    StyleConstants.setForeground(as, Color.getHSBColor(0.69f, 1, 0.39f));

                    // FINEST
                } else if( record.getLevel().equals(Level.FINEST) ){
                    StyleConstants.setForeground(as, Color.getHSBColor(0.99f, 1, 0.44f));
                }

                doc.insertString(doc.getLength(), record.getMessage() + "\n", as);
            } catch( BadLocationException ex ) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }

    public void setLoggingLevel(Level l) {
        this.level = l;
    }

    public Level getLoggingLevel() {
        return this.level;
    }

}
