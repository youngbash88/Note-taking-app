package com.mycompany.notetakingapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

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
    private String username;  // Add username field
    private static final String SKETCH_DIR = "sketches";

    public SketchPad(String username) {  // Modified constructor
        this.username = username;
        setTitle("Sketch Pad");
        ImageIcon im = new ImageIcon("notes.png");
        setIconImage(im.getImage());
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  // Changed to DISPOSE_ON_CLOSE

        initializeDrawingComponents();
        setupDrawingPanel();
        setupToolbar();
        // Create main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Setup and add toolbar
        JToolBar toolbar = setupToolbar();
        mainPanel.add(toolbar, BorderLayout.NORTH);
        // Setup drawing panel
        setupDrawingPanel();
        
        // Add drawing panel to a scroll pane
        JScrollPane scrollPane = new JScrollPane(drawingPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        // Add main panel to frame
        add(mainPanel);
        
        createSketchDirectory();
    }

    private void createSketchDirectory() {
        String userSketchPath = getUserSketchDir();
        File sketchDir = new File(userSketchPath);
        if (!sketchDir.exists()) {
            sketchDir.mkdirs();
        }
    }

    private String getUserSketchDir() {
        return Paths.get(FileManager.BASE_FOLDER_PATH, username, SKETCH_DIR).toString();
    }

    private void initializeDrawingComponents() {
        canvas = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        graphics = canvas.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 800, 600);
        graphics.setColor(Color.BLACK);

        currentColor = Color.BLACK;
        strokeWidth = 2;
        strokes = new ArrayList<>();

        // Enable antialiasing for smoother lines
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    private void setupDrawingPanel() {
        drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.drawImage(canvas, 0, 0, null);
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
                graphics.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                graphics.drawLine(lastPoint.x, lastPoint.y, currentPoint.x, currentPoint.y);
                lastPoint = currentPoint;
                currentStroke.add(currentPoint);
                drawingPanel.repaint();
            }
        });

        // Set preferred size for the drawing panel
        drawingPanel.setPreferredSize(new Dimension(800, 600));
    }

    
    private JToolBar setupToolbar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        JButton colorButton = createToolbarButton("Color", "Choose drawing color");
        JButton strokeButton = createToolbarButton("Stroke", "Adjust stroke width");
        JButton clearButton = createToolbarButton("Clear", "Clear canvas");
        JButton saveButton = createToolbarButton("Save", "Save sketch");
        JButton undoButton = createToolbarButton("Undo", "Undo last stroke");

        colorButton.addActionListener(e -> chooseColor());
        strokeButton.addActionListener(e -> adjustStrokeWidth());
        clearButton.addActionListener(e -> clearCanvas());
        saveButton.addActionListener(e -> saveSketch());
        undoButton.addActionListener(e -> undoLastStroke());

        toolbar.add(colorButton);
        toolbar.addSeparator();
        toolbar.add(strokeButton);
        toolbar.addSeparator();
        toolbar.add(clearButton);
        toolbar.addSeparator();
        toolbar.add(undoButton);
        toolbar.addSeparator();
        toolbar.add(saveButton);

        return toolbar;  // Now returns the toolbar instead of void
    }
    private JButton createToolbarButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        return button;
    }

    private void chooseColor() {
        Color newColor = JColorChooser.showDialog(this, "Choose Color", currentColor);
        if (newColor != null) {
            currentColor = newColor;
        }
    }

    private void adjustStrokeWidth() {
        String input = JOptionPane.showInputDialog(this,
                "Enter stroke width (1-20):",
                strokeWidth);
        if (input != null) {
            try {
                int width = Integer.parseInt(input);
                strokeWidth = Math.max(1, Math.min(20, width));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.");
            }
        }
    }

    private void clearCanvas() {
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        graphics.setColor(currentColor);
        strokes.clear();
        drawingPanel.repaint();
    }

    private void undoLastStroke() {
        if (!strokes.isEmpty()) {
            strokes.remove(strokes.size() - 1);
            redrawCanvas();
        }
    }

    private void redrawCanvas() {
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (List<Point> stroke : strokes) {
            graphics.setColor(currentColor);
            graphics.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            for (int i = 0; i < stroke.size() - 1; i++) {
                Point p1 = stroke.get(i);
                Point p2 = stroke.get(i + 1);
                graphics.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }
        drawingPanel.repaint();
    }

    private void saveSketch() {
        // Create unique filename with timestamp
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "sketch_" + timestamp + ".png";
        String filePath = Paths.get(getUserSketchDir(), fileName).toString();

        savedImage = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_RGB);
        savedImage.getGraphics().drawImage(canvas, 0, 0, null);

        try {
            File file = new File(filePath);
            ImageIO.write(savedImage, "PNG", file);
            JOptionPane.showMessageDialog(this, "Sketch saved successfully!");
            dispose();  // Close the sketch pad after saving
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving sketch: " + ex.getMessage());
        }
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(drawingPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);
    }

    public BufferedImage getSavedImage() {
        return savedImage;
    }

    public String getSavedImagePath() {
        return savedImage != null ? Paths.get(getUserSketchDir(), "sketch_"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".png").toString() : null;
    }
}
