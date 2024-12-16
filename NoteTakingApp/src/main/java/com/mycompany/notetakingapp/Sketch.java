package com.mycompany.notetakingapp;

import java.awt.Graphics;
import javax.swing.JPanel;

public class Sketch extends JPanel {

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Example of custom drawing
        g.drawString("Note Sketch", 50, 50);
    }
}
