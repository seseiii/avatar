import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LastWindow extends JFrame {


    private Image backgroundImage;

    public LastWindow() {

        String username = LoginWindow.getLoggedInUsername();  // Get the logged-in username
        AccountManager.updatePlayerStatus(username);  // Update status

        // Load the background image
        ImageIcon icon = new ImageIcon("src/img/end.png");
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
        JButton startButton = new JButton("Ok");
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        int buttonWidth = 150;
        int buttonHeight = 40;

        // Set the initial position and size of the button at the bottom-right corner of the container
        startButton.setBounds(backgroundPanel.getWidth() - buttonWidth - 80, backgroundPanel.getHeight() - buttonHeight - 80, buttonWidth, buttonHeight); // Adjusted the X position further to the left

        // Style the button with blue color
        startButton.setBackground(new Color(173, 216, 230)); // Light blue color
        startButton.setOpaque(true);
        startButton.setBorderPainted(false); // Optional: Remove the border

        // Hover effect for dark blue
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                startButton.setBackground(new Color(70, 130, 180)); // Darker blue when hovered
            }

            @Override
            public void mouseExited(MouseEvent e) {
                startButton.setBackground(new Color(173, 216, 230)); // Back to lighter blue
            }
        });

        backgroundPanel.add(startButton);

        // Action Listener for Start Button
        startButton.addActionListener(e -> {
            // Load a new image when the button is clicked
            ImageIcon newIcon = new ImageIcon("src/img/next.png"); // New image path
            backgroundImage = newIcon.getImage();

            // Repaint the background panel to show the new image
            backgroundPanel.repaint();

            // Remove the button after it is clicked
            startButton.setVisible(false);

            // Start a timer to close the window after 10 seconds
            Timer timer = new Timer(5000, evt -> {
                System.exit(0); // Exit the system after 10 seconds
            });
            timer.setRepeats(false); // Make sure the timer runs only once
            timer.start();
        });

        // Add a component listener to handle resizing (adjust as needed)
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Update button position at bottom-right corner when window is resized
                int frameWidth = getWidth();
                int frameHeight = getHeight();

                // Update button position to the right bottom of the panel, moving it even more to the left
                int buttonX = frameWidth - buttonWidth - 80; // Moved further left by 80px for more space
                int buttonY = frameHeight - buttonHeight - 80; // Raised button by 80px from the bottom
                startButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);

                // Repaint the background to fit the new size
                backgroundPanel.repaint();
            }
        });

        setVisible(true);



    }
}