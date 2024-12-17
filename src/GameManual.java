import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.event.ActionListener;


public class GameManual extends JDialog {
    private BufferedImage backgroundImage;
    private int currentImageIndex = 0;
    private Timer autoNextTimer;
    private JLabel backgroundLabel;
    private String[] imagePaths;
    int sizeWidth = 1240;
    int sizeHeight = 650;


    public GameManual(JFrame parent, String imagePath,Color buttonColor, Runnable onStartAction ) {
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

        // Start Button with hover effect
        JButton skipButton = new JButton("Skip");
        skipButton.setFont(new Font("Arial", Font.BOLD, 20));
        skipButton.setFocusPainted(false);
        skipButton.setBackground(buttonColor);
        skipButton.setForeground(Color.WHITE);
        skipButton.setBounds(sizeWidth - 150, sizeHeight - 80, 100, 30);
        skipButton.setBorderPainted(false);
        skipButton.setOpaque(true);

        // Hover effect
        Color originalColor = buttonColor;
        Color hoverColor = buttonColor.darker();
        skipButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                skipButton.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                skipButton.setBackground(originalColor);
            }
        });

        // Start button action
        skipButton.addActionListener(e -> {
            dispose();
            if (onStartAction != null) {
                onStartAction.run();
            }
        });
        backgroundPanel.add(skipButton);
    }

    public void game1Manual() {
        // Define the 5 image paths for the slideshow
        imagePaths = new String[]{
                "src/Manual/water0.png",
                "src/Manual/water1.png",
                "src/Manual/water2.png",
                "src/Manual/water3.png",
                "src/Manual/water4.png",
                "src/Manual/water5.png",
                "src/Manual/water6.png",
                "src/Manual/water7.png",
                "src/Manual/water8.png"
        };

        // Set the initial image
        setBackgroundImage(0);

        autoTimer();
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
    }

    public void game3Manual() {
        // Define the 5 image paths for the slideshow
        imagePaths = new String[]{
                "src/Manual/fire0.png",
                "src/Manual/fire1.png",
                "src/Manual/fire2.png",
                "src/Manual/fire3.png",
                "src/Manual/fire4.png",
                "src/Manual/fire5.png",
                "src/Manual/fire6.png",
                "src/Manual/fire7.png",
                "src/Manual/fire8.png"
        };

        // Set the initial image
        setBackgroundImage(0);

        autoTimer();
    }

    public void game4Manual() {
        // Define the 5 image paths for the slideshow
        imagePaths = new String[]{
                "src/Manual/air0.png",
                "src/Manual/air1.png",
                "src/Manual/air2.png",
                "src/Manual/air3.png",
                "src/Manual/air4.png",
                "src/Manual/air5.png",
                "src/Manual/air6.png"
        };

        // Set the initial image
        setBackgroundImage(0);

        autoTimer();
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

        autoNextTimer.start();
    }

}
