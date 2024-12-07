import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MissionCompleteDialog {

    private final JFrame parentFrame; // Store the parent frame if needed
    private final Object roadMapWindow; // Optional reference to the roadmap or other objects

    public MissionCompleteDialog(JFrame parentFrame, Object roadMapWindow) {
        this.parentFrame = parentFrame;
        this.roadMapWindow = roadMapWindow;
    }

    public void showMissionComplete() {
        // Create the dialog and set its properties
        JDialog missionCompleteDialog = new JDialog(parentFrame, true); // Modal dialog
        missionCompleteDialog.setSize(400, 270); // Dialog size
        missionCompleteDialog.setLayout(null); // Absolute positioning
        missionCompleteDialog.setUndecorated(true); // Remove title bar
        missionCompleteDialog.setLocationRelativeTo(parentFrame); // Center on the parent frame

        // Create the label to hold the "Mission Complete" image
        JLabel missionCompleteLabel = new JLabel();
        missionCompleteLabel.setBounds(0, 0, 400, 260);

        // Optional border around the image
        int borderSize = 1; // Adjust this if needed
        missionCompleteLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, borderSize));

        // Load the "Mission Complete" image
        try {
            ImageIcon originalIcon = new ImageIcon("src/img/missioncomplete.png");

            // Scale the image to fit within the label
            int imageWidth = 400 - (2 * borderSize);
            int imageHeight = 270 - (2 * borderSize);
            Image scaledImage = originalIcon.getImage().getScaledInstance(imageWidth, imageHeight, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);

            // Set the scaled image as the label's icon
            missionCompleteLabel.setIcon(scaledIcon);
            missionCompleteLabel.setHorizontalAlignment(JLabel.CENTER);
            missionCompleteLabel.setVerticalAlignment(JLabel.CENTER);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to load 'missioncomplete.png': " + e.getMessage(),
                    "Image Load Error", JOptionPane.ERROR_MESSAGE);
        }

        missionCompleteDialog.add(missionCompleteLabel); // Add the label to the dialog

        // Create the "Continue" button
        JButton continueButton = new JButton("Continue");
        continueButton.setBounds(150, 235, 100, 25); // Position and size of the button
        continueButton.setFocusPainted(false); // Remove focus outline
        continueButton.setBackground(new Color(137, 95, 37)); // Button background color
        continueButton.setForeground(Color.WHITE); // Button text color
        continueButton.setFont(new Font("Arial", Font.BOLD, 14)); // Font styling
        continueButton.setBorderPainted(false); // Remove button border
        continueButton.setOpaque(true); // Ensure the button is opaqu


        // Add hover effect for the button
        Color normalColor = new Color(137, 95, 37);
        Color hoverColor = normalColor.darker();
        addHoverEffect(continueButton, normalColor, hoverColor);

        // Add an action listener for the "Continue" button
        continueButton.addActionListener(e -> {
            missionCompleteDialog.dispose(); // Close the dialog

            if (roadMapWindow != null) {
                // Ensure roadMapWindow is a JFrame or something that supports setVisible
                if (roadMapWindow instanceof JFrame) {
                    ((JFrame) roadMapWindow).setVisible(true); // Make the roadmap window visible
                } else {
                    System.out.println("roadMapWindow is not of type JFrame.");
                }
            }
        });

        // Add the button to the label (overlapping the image)
        missionCompleteLabel.setLayout(null);
        missionCompleteDialog.setLocationRelativeTo(null);
        missionCompleteLabel.add(continueButton);

        // Ensure the dialog disposes cleanly
        missionCompleteDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Display the dialog
        missionCompleteDialog.setVisible(true);
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


