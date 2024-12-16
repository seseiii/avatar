import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class GameManual extends JDialog {
    private BufferedImage backgroundImage;
    private int currentImageIndex = 0;
    private Timer autoNextTimer;
    private JLabel backgroundLabel;
    private String[] imagePaths;
    int sizeWidth = 1240;
    int sizeHeight = 650;

    public GameManual(JFrame parent, String imagePath) {
        super(parent, true);

        // Remove title bar and add border
        setUndecorated(true);
        getRootPane().setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        // Dialog setup
        setSize(sizeWidth, sizeHeight);
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
                    Image scaledImage = backgroundImage.getScaledInstance(680, 520, Image.SCALE_SMOOTH);
                    g2.drawImage(scaledImage, 10, 10, null);
                } else {
                    g2.setColor(Color.RED);
                    g2.drawString("Error: Image not found", 10, 20);
                }
            }
        };
        backgroundPanel.setBounds(0, 0, sizeWidth, sizeHeight);
        backgroundPanel.setLayout(null);
        backgroundPanel.setOpaque(false);
        add(backgroundPanel);

        // Add a JLabel to display images for game2Manual
        backgroundLabel = new JLabel();
        backgroundLabel.setBounds(0, 0, sizeWidth, sizeHeight);
        backgroundPanel.add(backgroundLabel);

    }

    public void game2Manual() {
        // Define the 5 image paths for the slideshow
        imagePaths = new String[]{
                "src/Manual/earth0.png",
                "src/Manual/earth1.png",
                "src/Manual/earth2.png",
                "src/Manual/earth3.png",
                "src/Manual/earth4.png",
                "src/Manual/earth5.png"
        };

        // Set the initial image
        setBackgroundImage(0);

        autoTimer();

        JButton nextButton = new JButton("Skip");
        nextButton.setBounds(sizeWidth - 150, sizeHeight - 80, 100, 30);
        nextButton.setBackground(new Color(173, 216, 230));
        nextButton.setOpaque(true);
        nextButton.setBorderPainted(false);

        nextButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                nextButton.setBackground(new Color(135, 206, 235));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                nextButton.setBackground(new Color(173, 216, 230));
            }
        });

        nextButton.addActionListener(e -> {

        });

        backgroundLabel.add(nextButton);
        setVisible(true);
    }

    private void setBackgroundImage(int index) {
        try {
            BufferedImage img = ImageIO.read(new File(imagePaths[index]));
            if (img != null) {
                backgroundImage = img;
                Image scaledImage = img.getScaledInstance(backgroundLabel.getWidth(), backgroundLabel.getHeight(), Image.SCALE_SMOOTH);
                backgroundLabel.setIcon(new ImageIcon(scaledImage));
            }
        } catch (IOException e) {
            System.out.println("Error loading image: " + imagePaths[index]);
        }
    }

    private void autoTimer() {
        // Create and start the autoNextTimer to switch images every 6 seconds
        autoNextTimer = new Timer(5000, e -> {
            currentImageIndex++;
            if (currentImageIndex < imagePaths.length) {
                setBackgroundImage(currentImageIndex); // Update the image to the next one
            } else {
                autoNextTimer.stop(); // Stop the timer when all images are displayed
                dispose(); // Close the dialog after the last image
            }
        });

        // Start the timer
        autoNextTimer.start();
    }
}
