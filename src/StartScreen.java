import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.border.Border;


class StartScreen extends JDialog {
    private BufferedImage backgroundImage;

    public StartScreen(JFrame parent, String imagePath, Color buttonColor, Runnable onStartAction) {
        super(parent, true);

        // Remove title bar and add border
        setUndecorated(true);
        getRootPane().setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        // Dialog setup
        setSize(400, 380);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(null);

        // Load the image for this instance
        try {
            backgroundImage = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            backgroundImage = null; // Use null as a fallback
        }

        // Custom panel for background image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                // Enable high-quality rendering
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Draw the image
                if (backgroundImage != null) {
                    Image scaledImage = backgroundImage.getScaledInstance(380, 310, Image.SCALE_SMOOTH);
                    g2.drawImage(scaledImage, 10, 10, null);
                } else {
                    g2.setColor(Color.RED);
                    g2.drawString("Error: Image not found", 10, 20);
                }
            }
        };
        backgroundPanel.setBounds(0, 0, 400, 380);
        backgroundPanel.setLayout(null);
        backgroundPanel.setOpaque(false);
        add(backgroundPanel);

        // Start Button with hover effect
        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        startButton.setFocusPainted(false);
        startButton.setBackground(buttonColor);
        startButton.setForeground(Color.WHITE);
        startButton.setBounds((400 - 100) / 2, 330, 100, 40);
        startButton.setBorderPainted(false);
        startButton.setOpaque(true);

        // Hover effect
        Color originalColor = buttonColor;
        Color hoverColor = buttonColor.darker();
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                startButton.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                startButton.setBackground(originalColor);
            }
        });

        // Start button action
        startButton.addActionListener(e -> {
            dispose();
            if (onStartAction != null) {
                onStartAction.run();
            }
        });
        backgroundPanel.add(startButton);
    }
}