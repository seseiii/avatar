import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class StoryWindow extends JFrame {

    private int currentImageIndex = 0; // Track the current image index
    private final JLabel backgroundLabel;   // Label to display the background image
    private final String[] imagePaths = {   // Paths to the images
            "src/story/story1.png",
            "src/story/story2.png",
            "src/story/story3.png",
            "src/story/story4.png"
    };

    private final String[] soundPaths = {   // Corresponding sound files
            "src/img/1st.wav",
            "src/img/2nd.wav",
            "src/img/3rd.wav",
            "src/img/4th.wav"
    };

    private Clip clip; // Clip to play audio

    public StoryWindow() {
        setUndecorated(true); // Remove window decorations

        // Set fullscreen mode
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        gd.setFullScreenWindow(this);

        // Set JFrame properties
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // Add background label
        backgroundLabel = new JLabel();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        backgroundLabel.setBounds(0, 0, screenSize.width, screenSize.height);
        setBackgroundImage(currentImageIndex); // Set the initial background image
        add(backgroundLabel);

        // Add "Next" button
        JButton nextButton = new JButton("Next");
        nextButton.setBounds(screenSize.width - 150, screenSize.height - 80, 100, 30);
        nextButton.setBackground(new Color(173, 216, 230));
        nextButton.setOpaque(true);
        nextButton.setBorderPainted(false);

        // Add hover effect
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

        // Add action listener to the button
        nextButton.addActionListener(e -> {
            stopBackgroundMusic(); // Stop the current music
            if (currentImageIndex < imagePaths.length - 1) {
                currentImageIndex++;
                setBackgroundImage(currentImageIndex);
                playBackgroundMusic(currentImageIndex); // Play music for the new image
            } else {
                // Close the story window or navigate to the next window
                transitionToRoadMap();
            }
        });

        backgroundLabel.add(nextButton);
        setVisible(true);

        // Preload and play the first background music
        playBackgroundMusic(currentImageIndex);
    }

    // Method to set the background image
    private void setBackgroundImage(int index) {
        ImageIcon icon = new ImageIcon(imagePaths[index]);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Image scaledImage = icon.getImage().getScaledInstance(screenSize.width, screenSize.height, Image.SCALE_SMOOTH);
        backgroundLabel.setIcon(new ImageIcon(scaledImage));
    }

    // Method to play background music
    private void playBackgroundMusic(int index) {
        try {
            // Stop and close the previous clip before playing the new one
            stopBackgroundMusic();

            File musicPath = new File(soundPaths[index]);
            if (musicPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.start(); // Start the new music
            } else {
                System.out.println("Music file not found: " + soundPaths[index]);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // Method to stop background music
    private void stopBackgroundMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close(); // Release the resources
        }
    }

    // Transition to RoadMapWindow
    private void transitionToRoadMap() {
        stopBackgroundMusic(); // Ensure music stops before transition

        SwingUtilities.invokeLater(() -> {
            new RoadMapWindow().setVisible(true); // Launch RoadMapWindow
            dispose(); // Dispose of the StoryWindow
        });
    }
}