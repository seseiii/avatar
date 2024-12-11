import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.Properties;

public class RoadMapWindow extends JFrame {

    private final Image backgroundImage;

    // Declare buttons as instance variables
    private static OvalButton game1Button;
    private static OvalButton game2Button;
    private static OvalButton game3Button;
    private static OvalButton game4Button;

    private static final String SAVE_FILE = "button_positions.properties"; // Save file for button positions

    public RoadMapWindow() {
        // Load the background image
        ImageIcon icon = new ImageIcon("src/img/roadmap.png"); // Path to the background image
        backgroundImage = icon.getImage();

        // Set up the frame
        setTitle("Road Map");
        setSize(Toolkit.getDefaultToolkit().getScreenSize()); // Full-screen size
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Create a panel for the background
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(null);

        // Initialize and create buttons
        game1Button = new OvalButton("Game 1");
        game2Button = new OvalButton("Game 2");
        game3Button = new OvalButton("Game 3");
        game4Button = new OvalButton("Game 4");

        // Game 1 is unlocked by default
        game1Button.setLocked(false);

        // Other games remain locked
        game2Button.setLocked(true);
        game3Button.setLocked(true);
        game4Button.setLocked(true);

        // Load saved button positions
        loadButtonPositions();

        // Add functionality to Game 1 button
        game1Button.addActionListener(e -> {
            if (!game1Button.isLocked()) {
                new Game1(this).setVisible(true); // Pass the current `RoadMapWindow` instance to `Game1`


            } else {
                JOptionPane.showMessageDialog(this, "Game 1 is locked! Complete the required steps to unlock.");
            }
        });

        // Add functionality to Game 1 button
        game2Button.addActionListener(e -> {
            if (!game2Button.isLocked()) {
                Game2 game2Instance = new Game2(this); // Create an instance of Game2
                game2Instance.setVisible(true);


                if (game2Instance != null) { // Check if the Game2 instance is not null
                    game2Instance.dispose(); // Dispose the Game2 instance when done
                }
            } else {
                JOptionPane.showMessageDialog(this, "Game 2 is locked! Complete the required steps to unlock.");
            }

        });

        // Add functionality to Game 1 button
        game3Button.addActionListener(e -> {
            if (!game1Button.isLocked()) {

                new Game3(this);

            } else {
                JOptionPane.showMessageDialog(this, "Game 3 is locked! Complete the required steps to unlock.");
            }
        });

        // Add functionality to Game 1 button
        game4Button.addActionListener(e -> {
            if (!game1Button.isLocked()) {

                //dito lagay game4
                new Game4(this).setVisible(true);


            } else {
                JOptionPane.showMessageDialog(this, "Game 4 is locked! Complete the required steps to unlock.");
            }
        });

        // Add hover listeners for locked games
        addHoverMessage(game2Button, "Complete Game 1 to unlock this game.");
        addHoverMessage(game3Button, "Complete Game 2 to unlock this game.");
        addHoverMessage(game4Button, "Complete Game 3 to unlock this game.");

        // Add buttons to the panel
        backgroundPanel.add(game1Button);
        backgroundPanel.add(game2Button);
        backgroundPanel.add(game3Button);
        backgroundPanel.add(game4Button);

        // Add the background panel to the frame
        getContentPane().add(backgroundPanel);

        setVisible(true);
    }

    // Load button positions from a file
    private void loadButtonPositions() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(SAVE_FILE)) {
            properties.load(fis);

            game1Button.setBounds(
                    Integer.parseInt(properties.getProperty("game1.x", "260")),
                    Integer.parseInt(properties.getProperty("game1.y", "720")),
                    130, 65
            );
            game2Button.setBounds(
                    Integer.parseInt(properties.getProperty("game2.x", "540")),
                    Integer.parseInt(properties.getProperty("game2.y", "500")),
                    130, 65
            );
            game3Button.setBounds(
                    Integer.parseInt(properties.getProperty("game3.x", "960")),
                    Integer.parseInt(properties.getProperty("game3.y", "330")),
                    132, 66 // Default 2% size increase
            );
            game4Button.setBounds(
                    Integer.parseInt(properties.getProperty("game4.x", "1320")),
                    Integer.parseInt(properties.getProperty("game4.y", "510")),
                    130, 65
            );
        } catch (IOException e) {
            // If the file doesn't exist, set default positions
            game1Button.setBounds(279, 535, 108, 55);
            game2Button.setBounds(259, 270, 110, 56);
            game3Button.setBounds(710, 169, 114, 57);
            game4Button.setBounds(890, 371, 116, 55);
        }
    }

    // Add a hover message to a button
    private void addHoverMessage(OvalButton button, String message) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isLocked()) {
                    JToolTip tooltip = button.createToolTip();
                    tooltip.setTipText(message);
                    Popup popup = PopupFactory.getSharedInstance().getPopup(
                            button,
                            tooltip,
                            button.getLocationOnScreen().x + button.getWidth(),
                            button.getLocationOnScreen().y
                    );
                    popup.show();

                    // Hide the tooltip after a short delay
                    Timer timer = new Timer(2000, evt -> popup.hide());
                    timer.setRepeats(false);
                    timer.start();
                }
            }
        });
    }

    // Static method to unlock Game 2 and update Game 1
    public void unlockGame2() {
        game2Button.setLocked(false); // Unlock Game 2
        game1Button.setText("Completed"); // Update Game 1 button text
        game1Button.setForeground(Color.RED); // Change text color to red
        game1Button.setEnabled(false); // Optionally disable Game 1 button
        JOptionPane.showMessageDialog(this, "Game 2 Unlocked!"); // Notify the player

    }

    public void unlockGame3() {
        game3Button.setLocked(false); // Unlock Game 2
        game2Button.setText("Completed"); // Update Game 1 button text
        game2Button.setForeground(Color.RED); // Change text color to red
        game2Button.setEnabled(false); // Optionally disable Game 1 button
        JOptionPane.showMessageDialog(this, "Game 3 Unlocked!"); // Notify the player

    }

    public void unlockGame4() {
        game4Button.setLocked(false); // Unlock Game 2
        game3Button.setText("Completed"); // Update Game 1 button text
        game3Button.setForeground(Color.RED); // Change text color to red
        game3Button.setEnabled(false); // Optionally disable Game 1 button
        JOptionPane.showMessageDialog(this, "Game 4 Unlocked!"); // Notify the player

    }

    // Custom button class with oval shape, hover effect, and lock overlay
    class OvalButton extends JButton {
        private boolean isLocked = false; // Indicates if the button is locked
        private final Color defaultBackground = new Color(173, 216, 230); // Light blue
        private final Color hoverBackground = new Color(135, 206, 250); // Blue for hover

        public OvalButton(String text) {
            super(text);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setBackground(defaultBackground);
            setForeground(Color.BLACK); // Text color

            // Add hover effect
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!isLocked) {
                        setBackground(hoverBackground); // Change to blue on hover
                        repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(defaultBackground); // Reset to light blue
                    repaint();
                }
            });
        }

        public void setLocked(boolean locked) {
            this.isLocked = locked;
            setEnabled(!locked); // Disable button if locked
            repaint();
        }

        public boolean isLocked() {
            return isLocked;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Background oval
            g2.setColor(getBackground());
            g2.fillOval(0, 0, getWidth(), getHeight());

            // Border oval
            g2.setColor(new Color(139, 69, 19)); // Brown border
            g2.setStroke(new BasicStroke(3)); // Border thickness
            g2.drawOval(1, 1, getWidth() - 2, getHeight() - 2);

            // Draw text
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(getText());
            int textHeight = fm.getHeight();
            g2.setColor(getForeground());
            g2.drawString(getText(), (getWidth() - textWidth) / 2, (getHeight() + textHeight / 2) / 2);

            // Add lock overlay if locked
            if (isLocked) {
                g2.setColor(new Color(255, 255, 255, 150)); // Semi-transparent white overlay
                g2.fillOval(0, 0, getWidth(), getHeight());

                g2.setColor(Color.RED); // Red "X"
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(getWidth() / 2 - 8, getHeight() / 2 - 8, getWidth() / 2 + 8, getHeight() / 2 + 8);
                g2.drawLine(getWidth() / 2 + 8, getHeight() / 2 - 8, getWidth() / 2 - 8, getHeight() / 2 + 8);
            }

            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(130, 65); // Preferred size for the oval buttons
        }
    }
}