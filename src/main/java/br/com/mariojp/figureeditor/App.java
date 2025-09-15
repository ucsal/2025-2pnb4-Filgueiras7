package br.com.mariojp.figureeditor;

import javax.swing.*;
import java.awt.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            JFrame frame = new JFrame("Figure Editor — Clique para inserir figuras");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            DrawingPanel panel = new DrawingPanel();
            
            
            JToolBar toolbar = new JToolBar();
            toolbar.setFloatable(false);
            
          
            JButton colorButton = new JButton("Cor...");
            colorButton.setBackground(panel.getCurrentColor());
            colorButton.setOpaque(true);
            colorButton.addActionListener(e -> {
                Color newColor = JColorChooser.showDialog(frame, "Escolha uma cor", panel.getCurrentColor());
                if (newColor != null) {
                    panel.setCurrentColor(newColor);
                    colorButton.setBackground(newColor);
                }
            });
            
           
            JButton clearButton = new JButton("Limpar");
            clearButton.addActionListener(e -> panel.clear());
            
           
            JButton exportPngButton = new JButton("Exportar PNG");
            exportPngButton.addActionListener(e -> panel.exportPNG(frame));
            
           
            JButton exportSvgButton = new JButton("Exportar SVG");
            exportSvgButton.addActionListener(e -> panel.exportSVG(frame));
            
            toolbar.add(colorButton);
            toolbar.addSeparator();
            toolbar.add(clearButton);
            toolbar.addSeparator();
            toolbar.add(exportPngButton);
            toolbar.add(exportSvgButton);

            frame.setLayout(new BorderLayout());
            frame.add(toolbar, BorderLayout.NORTH);
            frame.add(panel, BorderLayout.CENTER);

            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}