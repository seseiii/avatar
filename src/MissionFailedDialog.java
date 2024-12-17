import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MissionFailedDialog {

    private final JFrame parentFrame; // Store the parent frame if needed
    private final Object welcomeWindow; // Optional reference to the welcome window or other objects

    // Constructor with correct name
    public MissionFailedDialog(JFrame parentFrame, Object welcomeWindow) {

        if (parentFrame == null) {
            this.parentFrame = createProxyFrame();
        } else {
            this.parentFrame = parentFrame;
        }
        this.welcomeWindow = welcomeWindow;
    }

    private JFrame createProxyFrame() {
        JFrame proxyFrame = new JFrame();
        proxyFrame.setUndecorated(true);
        proxyFrame.setSize(0, 0); // Invisible size
        proxyFrame.setLocationRelativeTo(null);
        return proxyFrame;
    }

    public void showMissionFailed() {
        // Create a modal dialog for the mission failed screen
        JDialog missionFailedDialog = new JDialog(parentFrame, true); // Use the parent frame as owner
        missionFailedDialog.setSize(400, 270);
        missionFailedDialog.setLayout(null);
        missionFailedDialog.setUndecorated(true);
        missionFailedDialog.setLocationRelativeTo(parentFrame);

        // Add the mission failed image
        JLabel missionFailedLabel = new JLabel();
        missionFailedLabel.setBounds(0, 0, 400, 260);
        missionFailedLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        try {
            ImageIcon originalIcon = new ImageIcon("src/img/failed.png");

            int imageWidth = 400 - 2;
            int imageHeight = 270 - 2;
            Image scaledImage = originalIcon.getImage().getScaledInstance(imageWidth, imageHeight, Image.SCALE_SMOOTH);

            missionFailedLabel.setIcon(new ImageIcon(scaledImage));
            missionFailedLabel.setHorizontalAlignment(JLabel.CENTER);
            missionFailedLabel.setVerticalAlignment(JLabel.CENTER);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentFrame, "Failed to load 'failed.png': " + e.getMessage(),
                    "Image Load Error", JOptionPane.ERROR_MESSAGE);
        }

        missionFailedDialog.add(missionFailedLabel);

        JButton backButton = new JButton("OK");
        backButton.setBounds(160, 235, 70, 25);
        backButton.setFocusPainted(false);
        Color normalColor = new Color(137, 95, 37);
        Color hoverColor = normalColor.darker();
        backButton.setBackground(normalColor);
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBorderPainted(false);
        backButton.setOpaque(true);

        addHoverEffect(backButton, normalColor, hoverColor);

        backButton.addActionListener(e -> {
            missionFailedDialog.dispose();  // Close the dialog
            failedMessage();

//                // Create a new instance of WelcomeWindow and display it
//                WelcomeWindow welcomeWindow = new WelcomeWindow(); // Replace this with your actual constructor
//                welcomeWindow.setVisible(true);

        });


        missionFailedLabel.setLayout(null);
        missionFailedDialog.setLocationRelativeTo(null);
        missionFailedLabel.add(backButton);

        missionFailedDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        missionFailedDialog.setVisible(true);
    }

    private void addHoverEffect(JButton button, Color normalColor, Color hoverColor) {
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(normalColor);
            }
        });
    }

    private void failedMessage() {
        // Load the background image
        ImageIcon icon = new ImageIcon("src/img/failedMessage.png");
        Image backgroundImage = icon.getImage();

        // Create a frame and set properties
        JFrame frame = new JFrame();
        frame.setSize(Toolkit.getDefaultToolkit().getScreenSize()); // Set to full-screen size
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false); // Disable resizing
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Set the frame to full-screen mode

        // Create a custom panel for the background
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // Draw the background image
            }
        };
        backgroundPanel.setLayout(null); // Use null layout for absolute positioning
        frame.setContentPane(backgroundPanel); // Set as content pane for the frame

        // Add Start Button
        JButton startButton = new JButton("Ok");
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        int buttonWidth = 150;
        int buttonHeight = 40;

        // Set the initial position and size of the button at the bottom-right corner of the container
        startButton.setBounds(backgroundPanel.getWidth() - buttonWidth - 80, backgroundPanel.getHeight() - buttonHeight - 80, buttonWidth, buttonHeight);

        // Style the button with blue color
        startButton.setBackground(new Color(255, 0, 0)); // Light blue color
        startButton.setOpaque(true);
        startButton.setBorderPainted(false); // Remove the border

        // Hover effect for dark blue
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                startButton.setBackground(new Color(255, 0, 0)); // Change to red on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                startButton.setBackground(new Color(70, 130, 180)); // Back to darker blue
            }

        });

        backgroundPanel.add(startButton);

        // Action Listener for Start Button
        startButton.addActionListener(e -> {
            WelcomeWindow welcomeWindow = new WelcomeWindow(); // Create an instance of WelcomeWindow
            welcomeWindow.setVisible(true); // Make the WelcomeWindow visible
            //System.exit(0);
            frame.dispose(); // Close the frame
        });

        // Add a component listener to handle resizing
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Update button position at bottom-right corner when window is resized
                int frameWidth = frame.getWidth();
                int frameHeight = frame.getHeight();

                int buttonX = frameWidth - buttonWidth - 80; // Move further left
                int buttonY = frameHeight - buttonHeight - 80; // Raise button from the bottom
                startButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);

                // Repaint the background to fit the new size
                backgroundPanel.repaint();
            }
        });

        frame.setVisible(true); // Make the frame visible
    }


}