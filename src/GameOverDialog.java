import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameOverDialog {

    private static int energy = 3; // Initial energy value

    // Getter for energy
    public static int getEnergy() { return energy;
    }

    // Setter for energy
    public static void setEnergy(int energyValue) {
        energy = energyValue;
    }

    // Decrement energy by 1
    public static void decrementEnergy() {
        if (energy > 0) {
            energy--; // Decrease energy
        }
    }

    // Check if energy is available
    public static boolean hasEnergy() {
        return energy > 0;
    }

    // Handle game-over scenario
    public static void handleGameOver(Window parentWindow, Runnable startGameAction) {
        // Decrement energy when game is over
        decrementEnergy();

        if (hasEnergy()) {
            JDialog gameOverDialog = new JDialog(parentWindow);
            gameOverDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            gameOverDialog.setUndecorated(true);

            // Load and scale the game over image
            ImageIcon icon = new ImageIcon(GameOverDialog.class.getResource("/img/gameover.png"));
            Image scaledImage = icon.getImage().getScaledInstance(305, 220, Image.SCALE_SMOOTH);

            // Create a custom panel for the image and buttons
            JPanel imagePanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(scaledImage, 0, 0, getWidth(), getHeight(), this);
                }
            };
            imagePanel.setLayout(null);
            imagePanel.setPreferredSize(new Dimension(305, 220));
            imagePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 5));

            // Create and style Play Again button
            JButton playAgainButton = new JButton("Play");
            playAgainButton.setBackground(new Color(165, 42, 42));
            playAgainButton.setForeground(Color.WHITE);
            playAgainButton.setFont(new Font("Arial", Font.BOLD, 14));
            playAgainButton.setFocusPainted(false);
            playAgainButton.setBorderPainted(false);
            playAgainButton.setOpaque(true);

            playAgainButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    playAgainButton.setBackground(playAgainButton.getBackground().darker());
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    playAgainButton.setBackground(new Color(165, 42, 42));
                }
            });

            // Create and style Exit button
            JButton exitButton = new JButton("Exit");
            exitButton.setBackground(new Color(165, 42, 42));
            exitButton.setForeground(Color.WHITE);
            exitButton.setFont(new Font("Arial", Font.BOLD, 14));
            exitButton.setFocusPainted(false);
            exitButton.setBorderPainted(false);
            exitButton.setOpaque(true);

            exitButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    exitButton.setBackground(exitButton.getBackground().darker());
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    exitButton.setBackground(new Color(165, 42, 42));
                }
            });

            // Position buttons
            int buttonWidth = 80;
            int buttonHeight = 25;
            int gap = 10;
            int totalButtonsWidth = (buttonWidth * 2) + gap;
            int xStart = (305 - totalButtonsWidth) / 2;
            int yPosition = 220 - buttonHeight - 10;

            playAgainButton.setBounds(xStart, yPosition, buttonWidth, buttonHeight);
            exitButton.setBounds(xStart + buttonWidth + gap, yPosition, buttonWidth, buttonHeight);

            // Add action listeners to buttons
            playAgainButton.addActionListener(e -> {
                gameOverDialog.dispose();
                startGameAction.run(); // Call the provided start game action
            });

            exitButton.addActionListener(e -> {
                gameOverDialog.dispose();
                System.exit(0);
            });

            // Add buttons to the image panel
            imagePanel.add(playAgainButton);
            imagePanel.add(exitButton);

            // Set the image panel as the dialog's content
            gameOverDialog.setContentPane(imagePanel);
            gameOverDialog.pack();
            gameOverDialog.setLocationRelativeTo(parentWindow);
            gameOverDialog.setVisible(true);
        } else {
            // Show Mission Failed dialog if no energy left
            MissionFailedDialog dialog = new MissionFailedDialog(null, parentWindow);
            dialog.showMissionFailed();
            parentWindow.dispose();
        }
    }
}