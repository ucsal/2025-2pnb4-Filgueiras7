package br.com.mariojp.figureeditor;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

class DrawingPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_SIZE = 60;
    private final List<ColoredShape> shapes = new ArrayList<>();
    private Point startDrag = null;
    private Color currentColor = new Color(30, 144, 255); // Cor padrão azul

    DrawingPanel() {
        setBackground(Color.WHITE);
        setOpaque(true);
        setDoubleBuffered(true);

        var mouse = new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1 && startDrag == null) {
                    int size = Math.max(Math.min(DEFAULT_SIZE, DEFAULT_SIZE), 10);
                    Shape s = new Ellipse2D.Double(e.getPoint().x, e.getPoint().y, size, size);
                    shapes.add(new ColoredShape(s, currentColor));
                    repaint();
                }
            }
        };
        addMouseListener(mouse);        
        addMouseMotionListener(mouse);
    }

    void clear() {
        shapes.clear();
        repaint();
    }
    
    Color getCurrentColor() {
        return currentColor;
    }
    
    void setCurrentColor(Color color) {
        this.currentColor = color;
    }

    void exportPNG(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar como PNG");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Images", "png"));
        fileChooser.setSelectedFile(new File("drawing.png"));
        
        if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".png")) {
                file = new File(file.getAbsolutePath() + ".png");
            }
            
            try {
                BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = image.createGraphics();
                
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                for (ColoredShape cs : shapes) {
                    g2d.setColor(cs.color);
                    g2d.fill(cs.shape);
                    g2d.setColor(new Color(0, 0, 0, 70));
                    g2d.setStroke(new BasicStroke(1.2f));
                    g2d.draw(cs.shape);
                }
                
                g2d.dispose();
                ImageIO.write(image, "PNG", file);
                JOptionPane.showMessageDialog(parent, "PNG exportado com sucesso!");
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent, "Erro ao salvar PNG: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    void exportSVG(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar como SVG");
        fileChooser.setFileFilter(new FileNameExtensionFilter("SVG Images", "svg"));
        fileChooser.setSelectedFile(new File("drawing.svg"));
        
        if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".svg")) {
                file = new File(file.getAbsolutePath() + ".svg");
            }
            
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                writer.write(String.format("<svg width=\"%d\" height=\"%d\" xmlns=\"http://www.w3.org/2000/svg\">\n", 
                    getWidth(), getHeight()));
                writer.write("  <rect width=\"100%\" height=\"100%\" fill=\"white\"/>\n");
                
                for (ColoredShape cs : shapes) {
                    if (cs.shape instanceof Ellipse2D) {
                        Ellipse2D ellipse = (Ellipse2D) cs.shape;
                        writer.write(String.format(
                            "  <ellipse cx=\"%.1f\" cy=\"%.1f\" rx=\"%.1f\" ry=\"%.1f\" " +
                            "fill=\"rgb(%d,%d,%d)\" stroke=\"rgba(0,0,0,0.27)\" stroke-width=\"1.2\"/>\n",
                            ellipse.getCenterX(), ellipse.getCenterY(),
                            ellipse.getWidth() / 2, ellipse.getHeight() / 2,
                            cs.color.getRed(), cs.color.getGreen(), cs.color.getBlue()
                        ));
                    }
                }
                
                writer.write("</svg>");
                JOptionPane.showMessageDialog(parent, "SVG exportado com sucesso!");
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent, "Erro ao salvar SVG: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override 
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (ColoredShape cs : shapes) {
            g2.setColor(cs.color);
            g2.fill(cs.shape);
            g2.setColor(new Color(0, 0, 0, 70));
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(cs.shape);
        }

        g2.dispose();
    }
    

    private static class ColoredShape {
        final Shape shape;
        final Color color;
        
        ColoredShape(Shape shape, Color color) {
            this.shape = shape;
            this.color = color;
        }
    }
}