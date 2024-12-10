import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

class StartScreen extends JDialog {
    private String selectedGame;

    public StartScreen(JFrame parent, String selectedGame) {
        super(parent, true);
        this.selectedGame = selectedGame; // Store the selected game

        setUndecorated(true);
        getRootPane().setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        setSize(400, 380);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

                // Use if-else to load the correct mission image based on selectedGame
                try {
                    BufferedImage image = null;

                    if ("game1".equals(selectedGame)) {
                        image = ImageIO.read(getClass().getResource("/img/watermission.png"));
                    } else if ("game2".equals(selectedGame)) {
                        image = ImageIO.read(getClass().getResource("/img/earthmission.png"));
                    } else if ("game3".equals(selectedGame)) {
                        image = ImageIO.read(getClass().getResource("/img/firemission.png"));
                    } else {
                        System.out.println("Game4");
                    }

                    if (image != null) {
                        Image scaledImage = image.getScaledInstance(380, 310, Image.SCALE_SMOOTH);
                        g2.drawImage(scaledImage, 10, 10, null);
                    } else {
                        g2.setColor(Color.RED);
                        g2.drawString("Failed to load background image", 10, 20);
                    }
                } catch (IOException e) {
                    g2.setColor(Color.RED);
                    g2.drawString("Failed to load background image", 10, 20);
                }
            }
        };
        backgroundPanel.setBounds(0, 0, 400, 380);
        backgroundPanel.setLayout(null);
        backgroundPanel.setOpaque(false);
        add(backgroundPanel);

        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        startButton.setFocusPainted(false);
        startButton.setBackground(new Color(124, 15, 15));
        startButton.setForeground(Color.WHITE);
        startButton.setBounds((400 - 100) / 2, 330, 100, 40);

        startButton.setBorderPainted(false);
        startButton.setOpaque(true);

        Color originalColor = startButton.getBackground();
        Color hoverColor = originalColor.darker();

        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                startButton.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                startButton.setBackground(originalColor);
            }
        });

        startButton.addActionListener(e -> dispose());
        backgroundPanel.add(startButton);
    }
}
