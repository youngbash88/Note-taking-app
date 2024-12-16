package com.mycompany.notetakingapp;

import javax.swing.ImageIcon;

public class Image {
    private String path;
    private ImageIcon imageIcon;

    public Image(String path) {
        this.path = path;
        this.imageIcon = new ImageIcon(path);
    }

    public java.awt.Image getImage() {  // Fully qualified class name
        return imageIcon.getImage();
    }
}
