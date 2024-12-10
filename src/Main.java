import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main {
    public static void main(String[] args) {
        //new WelcomeWindow();
        RoadMapWindow welcomeWindow = new RoadMapWindow();
    }
}

class WelcomeWindow extends JFrame {

    private final Image backgroundImage;

    public WelcomeWindow() {
        // Load the background image
        ImageIcon icon = new ImageIcon("src/img/welcomee.png");
        backgroundImage = icon.getImage();

        setSize(Toolkit.getDefaultToolkit().getScreenSize()); // Set to full-screen size
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Disable resizing
        setResizable(false);

        // Set the frame to full-screen mode
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Create a custom panel for the background
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw the background image
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(null); // Use null layout for absolute positioning
        setContentPane(backgroundPanel); // Set as content pane for the frame

        // Add Start Button
        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        int buttonWidth = 210;
        int buttonHeight = 50;

        // Style the button
        startButton.setBackground(new Color(227, 141, 60)); // Lighter brownish-orange color
        startButton.setOpaque(true);
        startButton.setBorderPainted(false); // Optional: Remove the border

        // Hover effect
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                startButton.setBackground(new Color(181, 101, 29)); // Darker brownish-orange when hovered
            }

            @Override
            public void mouseExited(MouseEvent e) {
                startButton.setBackground(new Color(227, 141, 60)); // Back to lighter brownish-orange
            }
        });

        backgroundPanel.add(startButton);

        // Action Listener for Start Button
        startButton.addActionListener(e -> {
            new LoginWindow(); // Pass the current dimensions
           dispose(); // Close this window
        });

        // Add a component listener to handle resizing (adjust as needed)
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int frameWidth = getWidth();
                int frameHeight = getHeight();

                // Update button position and size proportionally
                int buttonX = (frameWidth - buttonWidth) / 2;
                int buttonY = frameHeight - 160; // Adjusted vertical position
                startButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);

                // Repaint the background to fit the new size
                backgroundPanel.repaint();
            }
        });

        setVisible(true);
    }
}