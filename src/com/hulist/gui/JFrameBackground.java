/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hulist.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 *
 * @author Aleksander
 */
public class JFrameBackground extends JFrame {

    private final BufferedImage img;

    public JFrameBackground() {
        super();
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(getClass().getClassLoader().getResource("resources/background_700.jpg"));
        } catch (IOException ex) {
            Logger.getLogger(JFrameBackground.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (bi != null) {
            img = bi;
            //setUndecorated(true);
        } else {
            img = null;
        }
    }

    @Override
    public void paint(Graphics g) {
        //Pick one of the two painting methods below.

        //Option 1:
        //Define the bounding region to paint based on image size.
        //Be careful, if the image is smaller than the JOptionPane size you
        //will see a solid white background where the image does not reach.
        //g.drawImage(img, 0, 0, img.getWidth(), img.getHeight());
        
        //Option 2:
        //If the image can be guaranteed to be larger than the JOptionPane's size
        if (img != null) {
            Dimension curSize = this.getSize();
            g.drawImage(img, 0, 0, curSize.width, curSize.height, null);
        }

        //Make sure to paint all the other properties of Swing components.
        super.paint(g);
    }
}
