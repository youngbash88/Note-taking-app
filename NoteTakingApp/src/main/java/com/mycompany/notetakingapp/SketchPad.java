package com.mycompany.notetakingapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class SketchPad extends JFrame {
    private BufferedImage canvas;
    private Graphics2D graphics;
    private Point lastPoint;
    private List<Point> currentStroke;
    private List<List<Point>> strokes;
    private Color currentColor;
    private int strokeWidth;
    private BufferedImage savedImage;
    private JPanel drawingPanel;

    public SketchPad() {
        setTitle("Sketch Pad");
        setSize(600, 400);
        setLocationRelativeTo(null);
        
        initializeDrawingComponents();
        setupDrawingPanel();
        setupToolbar();
        layoutComponents();
    }

    private void initializeDrawingComponents() {
        canvas = new BufferedImage(600, 400, BufferedImage.TYPE_INT_RGB);
        graphics = canvas.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 600, 400);
        graphics.setColor(Color.BLACK);
        
        currentColor = Color.BLACK;
        strokeWidth = 2;
        strokes = new ArrayList<>();
    }

    private void setupDrawingPanel() {
        drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(canvas, 0, 0, null);
            }
        };
        
        drawingPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastPoint = e.getPoint();
                currentStroke = new ArrayList<>();
                currentStroke.add(lastPoint);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                strokes.add(currentStroke);
            }
        });
        
        drawingPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point currentPoint = e.getPoint();
                graphics.setColor(currentColor);
                graphics.setStroke(new BasicStroke(strokeWidth));
                graphics.drawLine(lastPoint.x, lastPoint.y, currentPoint.x, currentPoint.y);
                lastPoint = currentPoint;
                currentStroke.add(currentPoint);
                drawingPanel.repaint();
            }
        });
    }

    private void setupToolbar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        JButton colorButton = new JButton("Color");
        colorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose Color", currentColor);
            if (newColor != null) {
                currentColor = newColor;
            }
        });
        
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> {
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, 600, 400);
            graphics.setColor(currentColor);
            strokes.clear();
            drawingPanel.repaint();
        });
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            savedImage = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_RGB);
            savedImage.getGraphics().drawImage(canvas, 0, 0, null);
            dispose();
        });
        
        toolbar.add(colorButton);
        toolbar.add(clearButton);
        toolbar.add(saveButton);
        
        add(toolbar, BorderLayout.NORTH);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        add(drawingPanel, BorderLayout.CENTER);
    }
    
    public BufferedImage getSavedImage() {
        return savedImage;
    }
}