import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MissionFailedDialog {

    private final JFrame parentFrame; // Store the parent frame if needed
    private final Object welcomeWindow; // Optional reference to the welcome window or other objects

    // Constructor with correct name
    public MissionFailedDialog(JFrame parentFrame, Object welcomeWindow) {
        this.parentFrame = parentFrame;
        this.welcomeWindow = welcomeWindow;
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

            if (welcomeWindow != null) {
                // Ensure welcomeWindow is a JFrame or something that supports setVisible
                if (welcomeWindow instanceof JFrame) {
                    ((JFrame) welcomeWindow).setVisible(true);  // Make the welcomeWindow window visible
                } else {
                    System.out.println("welcomeWindow is not of type JFrame.");
                }
            }
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
}
