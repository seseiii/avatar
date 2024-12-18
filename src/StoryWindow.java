
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class StoryWindow extends JFrame {

    private int currentImageIndex = 0; // Track the current image index
    private JLabel backgroundLabel;   // Label to display the background image
    private String[] imagePaths = {
            "src/story/story1.png",
            "src/story/story2.png",
            "src/story/story3.png",
            "src/story/story4.png",
            "src/story/missionbg.png" // Final image
    };

    private String[] soundPaths = {
            "src/wavfile/1st.wav",
            "src/wavfile/2nd.wav",
            "src/wavfile/3rd.wav",
            "src/wavfile/4th.wav"
    };

    private Clip clip;
    private JButton nextButton;
    private boolean isUserSkipped = false; // Flag to check if user manually skipped

    public StoryWindow() {
        setUndecorated(true);
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        gd.setFullScreenWindow(this);

        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        backgroundLabel = new JLabel();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        backgroundLabel.setBounds(0, 0, screenSize.width, screenSize.height);
        setBackgroundImage(currentImageIndex);
        add(backgroundLabel);

        nextButton = new JButton("Next");
        nextButton.setBounds(screenSize.width - 150, screenSize.height - 80, 100, 30);
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
            stopBackgroundMusic();
            isUserSkipped = true; // User manually skipped
            goToNextImage();
        });

        backgroundLabel.add(nextButton);
        setVisible(true);
        playBackgroundMusic(currentImageIndex);
    }

    private void setBackgroundImage(int index) {
        ImageIcon icon = new ImageIcon(imagePaths[index]);
        if (icon.getIconWidth() == -1) {
            System.out.println("Image not found: " + imagePaths[index]);
        } else {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Image scaledImage = icon.getImage().getScaledInstance(screenSize.width, screenSize.height, Image.SCALE_SMOOTH);
            backgroundLabel.setIcon(new ImageIcon(scaledImage));
        }
    }

    private void playBackgroundMusic(int index) {
        try {
            stopBackgroundMusic();
            if (index < soundPaths.length) { // Play music for pages with audio
                File musicPath = new File(soundPaths[index]);
                if (musicPath.exists()) {
                    AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                    clip = AudioSystem.getClip();
                    clip.open(audioInput);
                    clip.start();

                    // Auto-next kapag natapos ang music
                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            clip.close();
                            if (!isUserSkipped) { // Auto-next lang kapag hindi nag-manual skip
                                SwingUtilities.invokeLater(() -> goToNextImage());
                            }
                        }
                    });
                } else {
                    System.out.println("Music file not found: " + soundPaths[index]);
                }
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void stopBackgroundMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }

    private void goToNextImage() {
        if (currentImageIndex < imagePaths.length - 1) { // Move to the next page
            currentImageIndex++;
            setBackgroundImage(currentImageIndex);
            playBackgroundMusic(currentImageIndex);
        } else {
            transitionToRoadMap(); // Final transition
        }
        isUserSkipped = false; // Reset flag para sa susunod na page
    }

    private void transitionToRoadMap() {
        stopBackgroundMusic();
        SwingUtilities.invokeLater(() -> {
            new RoadMapWindow().setVisible(true);
            dispose();
        });
    }
}