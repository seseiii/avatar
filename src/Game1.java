import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Game1 extends JFrame {

    private final RoadMapWindow roadMapWindow;
    private int energy = 3; // Player starts with 3 energy points

    public Game1(RoadMapWindow roadMapWindow) {
        this.roadMapWindow = roadMapWindow;

        // Show Start Screen first
        StartScreen startScreen = new StartScreen(this); // Pass this Game1 instance to StartScreen
        startScreen.setVisible(true);

        // Configure the main game frame
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full-screen mode
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(new GamePanel());
        setResizable(false);
        setVisible(false); // Initially hidden until the StartScreen closes
    }


    static class StartScreen extends JDialog {
        public StartScreen(Game1 parent) {
            super(parent, true); // Make it modal to block interaction with the Game1 frame

            // Remove title bar
            setUndecorated(true);

            // Add custom border to mimic window frame without title bar
            getRootPane().setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

            setSize(400, 380); // Updated size
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null); // Center the dialog
            setResizable(false); // Disable resizing
            setLayout(null);

            // Custom panel for background and text rendering
            JPanel backgroundPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    // Do not call super.paintComponent(g); to prevent filling the background
                    Graphics2D g2 = (Graphics2D) g;

                    // Enable high-quality rendering for images and text
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

                    // Draw the main image without white background
                    try {
                        BufferedImage image = ImageIO.read(new File("src/img/watermission.png"));
                        Image scaledImage = image.getScaledInstance(380, 310, Image.SCALE_SMOOTH);

                        int imageX = 10;
                        int imageY = 10;

                        // Draw the image
                        g2.drawImage(scaledImage, imageX, imageY, null);

                    } catch (IOException e) {
                        g2.setColor(Color.RED);
                        g2.drawString("Failed to load background image", 10, 20);
                    }
                }
            };
            backgroundPanel.setBounds(0, 0, 400, 380);
            backgroundPanel.setLayout(null);
            backgroundPanel.setOpaque(false); // Make the panel transparent
            add(backgroundPanel);

            // Add Start Button
            JButton startButton = new JButton("Start");
            startButton.setFont(new Font("Arial", Font.BOLD, 14));
            startButton.setFocusPainted(false);
            startButton.setBackground(new Color(227, 141, 60));
            startButton.setForeground(Color.WHITE);
            startButton.setBounds((400 - 100) / 2, 330, 100, 40);

            startButton.setBorderPainted(false); // Remove button border
            startButton.setOpaque(true);

            // Add hover effect to the button
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

            startButton.addActionListener(e -> {
                dispose(); // Close the StartScreen dialog
                SwingUtilities.invokeLater(() -> parent.setVisible(true)); // Make the Game1 frame visible after disposing
            });
            backgroundPanel.add(startButton);
        }
    }

    // Main Game Panel
    class GamePanel extends JPanel implements ActionListener {
        private final int TILE_SIZE = 20;
        private final int AANG_SIZE = TILE_SIZE * 4;
        private final int BOAT_SIZE = TILE_SIZE * 3;
        private int WIDTH;
        private int HEIGHT;
        private LinkedList<Point> snake;
        private Point food;
        private char direction;
        private boolean gameOver;
        private Timer timer;

        private int foodCount = 0; // Track the number of foods eaten
        private int score = 0; // Player's score
        private boolean isSpecialFood = false; // Flag for showing "aang" food

        private Image snakeHeadImage;
        private Image snakeBodyImage;
        private Image foodImage;
        private Image backgroundImage;
        private Image energyIcon;
        private Image aangImage;

        public GamePanel() {
            loadImages();
            setFocusable(true);
            requestFocusInWindow();
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    if (getWidth() > 0 && getHeight() > 0 && (snake == null || food == null)) {
                        startGame();
                    }
                }
            });
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP -> {
                            if (direction != 'D') direction = 'U';
                        }
                        case KeyEvent.VK_DOWN -> {
                            if (direction != 'U') direction = 'D';
                        }
                        case KeyEvent.VK_LEFT -> {
                            if (direction != 'R') direction = 'L';
                        }
                        case KeyEvent.VK_RIGHT -> {
                            if (direction != 'L') direction = 'R';
                        }
                    }
                }
            });
        }

        private void loadImages() {
            try {
                snakeHeadImage = new ImageIcon("src/img/boat.png").getImage().getScaledInstance(BOAT_SIZE, BOAT_SIZE, Image.SCALE_SMOOTH);
                snakeBodyImage = new ImageIcon("src/img/fish.png").getImage();
                foodImage = new ImageIcon("src/img/fish.png").getImage();
                backgroundImage = new ImageIcon("src/img/waterbg.jpg").getImage();
                energyIcon = new ImageIcon("src/img/energy.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
                aangImage = new ImageIcon("src/img/aang.png").getImage().getScaledInstance(AANG_SIZE, AANG_SIZE, Image.SCALE_SMOOTH);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error loading images: " + e.getMessage(),
                        "Image Load Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }

        private void startGame() {
            WIDTH = getWidth() / TILE_SIZE;
            HEIGHT = getHeight() / TILE_SIZE;

            snake = new LinkedList<>();
            snake.add(new Point(WIDTH / 2, HEIGHT / 2));
            direction = 'R';
            spawnFood();
            gameOver = false;
            foodCount = 0;
            score = 0;
            isSpecialFood = false;

            if (timer != null) {
                timer.stop();
            }
            timer = new Timer(150, this);
            timer.start();
        }

        private void spawnFood() {
            Random rand = new Random();

            int xPadding = 2; // Padding from the left and right edges
            int yPaddingTop = HEIGHT / 3; // Exclude the top section
            int yPaddingBottom = 2; // Padding from the bottom edge

            if (WIDTH <= 0 || HEIGHT <= 0) return;

            do {
                int x = xPadding + rand.nextInt(WIDTH - 2 * xPadding); // Spawn away from edges
                int y = yPaddingTop + rand.nextInt(HEIGHT - yPaddingTop - yPaddingBottom); // Middle and bottom only
                food = new Point(x, y);
            } while (snake.contains(food));

            // Set the special food (Aang) if it's the 10th food
            if (foodCount == 9) {
                isSpecialFood = true;
            } else {
                isSpecialFood = false;
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!gameOver) {
                moveSnake();
                checkCollision();
                repaint();
            }
        }
        private void moveSnake() {
            Point head = snake.getFirst();
            Point newHead = new Point(head);

            // Move the head in the current direction
            switch (direction) {
                case 'U' -> newHead.y--;
                case 'D' -> newHead.y++;
                case 'L' -> newHead.x--;
                case 'R' -> newHead.x++;
            }

            snake.addFirst(newHead);

            // Check if the head overlaps the food
            if (isCollidingWithFood(newHead)) {
                foodCount++;
                score += 5; // Each food gives 5 points

                // If it's the 10th food, set the special food flag
                if (foodCount == 10) {
                    isSpecialFood = true;
                }

                if (score < 50) {
                    spawnFood(); // Spawn a new food
                }
            } else {
                snake.removeLast(); // If no food is eaten, remove the tail
            }
            if (score >= 5 && timer.isRunning()) {
                timer.stop(); // Stop the timer when the mission is complete

                // Use a small delay to ensure all UI updates are complete
                SwingUtilities.invokeLater(() -> {
                    // Call the MissionCompleteDialog class and pass necessary parameters
                    MissionCompleteDialog missionDialog = new MissionCompleteDialog(Game1.this, roadMapWindow);
                    missionDialog.showMissionComplete();
                    dispose();
                    roadMapWindow.unlockGame2();
                });
            }
        }


        private void checkCollision() {
            Point head = snake.getFirst();

            if (head.x < 0 || head.x >= WIDTH || head.y < 0 || head.y >= HEIGHT) {
                gameOver = true;
                timer.stop();
                handleGameOver("You hit the wall!");
            }

            for (int i = 1; i < snake.size(); i++) {
                if (head.equals(snake.get(i))) {
                    gameOver = true;
                    timer.stop();
                    handleGameOver("You ran into yourself!");
                    break;
                }
            }
        }

        private boolean isCollidingWithFood(Point head) {
            int headX = head.x * TILE_SIZE;
            int headY = head.y * TILE_SIZE;
            int foodX = food.x * TILE_SIZE;
            int foodY = food.y * TILE_SIZE;

            // Adjust size for boat and food dimensions
            int headSize = BOAT_SIZE;
            int foodSize = isSpecialFood ? AANG_SIZE : TILE_SIZE;

            return headX < foodX + foodSize && headX + headSize > foodX &&
                    headY < foodY + foodSize && headY + headSize > foodY;
        }

        private void handleGameOver(String message) {
            energy--; // Decrease energy by 1

            if (energy > 0) {
                // Get the parent window (if applicable)
                Window parentWindow = SwingUtilities.getWindowAncestor(this);
                JDialog gameOverDialog = new JDialog(parentWindow);
                gameOverDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                gameOverDialog.setUndecorated(true); // Remove the title bar and window decorations

                // Load and scale the game over image to 305x220 pixels
                ImageIcon icon = new ImageIcon("src/img/gameover.png");
                Image scaledImage = icon.getImage().getScaledInstance(305, 220, Image.SCALE_SMOOTH);

                // Create a custom panel that displays the image and overlays buttons
                JPanel imagePanel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        // Draw the image as the background
                        g.drawImage(scaledImage, 0, 0, getWidth(), getHeight(), this);
                    }
                };
                imagePanel.setLayout(null); // Use absolute positioning
                imagePanel.setPreferredSize(new Dimension(305, 220));

                // Magdagdag ng gray na gilid sa image panel
                imagePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 5));

                // Create the buttons
                JButton playAgainButton = new JButton("Play Again");
                JButton exitButton = new JButton("Exit");

                // Set buttons' background color to brown
                Color brown = new Color(165, 42, 42); // Brown color
                Color darkBrown = brown.darker();     // Darker shade of brown for hover effect

                playAgainButton.setBackground(brown);
                exitButton.setBackground(brown);

                // Set foreground color to white for contrast
                playAgainButton.setForeground(Color.WHITE);
                exitButton.setForeground(Color.WHITE);

                // Ensure buttons are opaque so the background color is visible
                playAgainButton.setOpaque(true);
                playAgainButton.setContentAreaFilled(true);
                exitButton.setOpaque(true);
                exitButton.setContentAreaFilled(true);

                // Remove border for a flat look
                playAgainButton.setBorderPainted(false);
                exitButton.setBorderPainted(false);

                // Set buttons' size and position them over the image
                int buttonWidth = 80;  // Smaller width
                int buttonHeight = 25; // Smaller height
                int gap = 10; // Gap between buttons

                // Calculate positions based on image size
                int totalButtonsWidth = (buttonWidth * 2) + gap;
                int xStart = (305 - totalButtonsWidth) / 2; // Center buttons horizontally
                int yPosition = 220 - buttonHeight - 10;     // Position buttons near the bottom (adjusted for border)

                playAgainButton.setBounds(xStart, yPosition, buttonWidth, buttonHeight);
                exitButton.setBounds(xStart + buttonWidth + gap, yPosition, buttonWidth, buttonHeight);

                // Add action listeners to the buttons
                playAgainButton.addActionListener(e -> {
                    gameOverDialog.dispose();
                    startGame(); // Restart the game
                });

                exitButton.addActionListener(e -> {
                    gameOverDialog.dispose();
                    System.exit(0); // Exit the application
                });

                // Add hover effect to buttons
                addHoverEffect(playAgainButton, brown, darkBrown);
                addHoverEffect(exitButton, brown, darkBrown);

                // Add buttons to the image panel
                imagePanel.add(playAgainButton);
                imagePanel.add(exitButton);

                // Set the image panel as the content pane of the dialog
                gameOverDialog.setContentPane(imagePanel);

                // Set the dialog properties
                gameOverDialog.pack();
                gameOverDialog.setLocationRelativeTo(this);
                gameOverDialog.setVisible(true);
            } else {
                // Force the GamePanel to repaint and update the energy icons
                repaint();
                paintImmediately(0, 0, getWidth(), getHeight());

                // Show failed image and transition to WelcomeWindow
                MissionFailedDialog dialog = new MissionFailedDialog(Game1.this, roadMapWindow);
                dialog.showMissionFailed();
                dispose();
            }
        }

        // Helper method to add hover effect to buttons
        private void addHoverEffect(JButton button, Color normalColor, Color hoverColor) {
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(hoverColor);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(normalColor);
                }
            });
        }





        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (snake == null || food == null) return;

            // Draw the background
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

            // Draw the score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + score, 10, 30);

            // Draw energy icons at the top
            for (int i = 0; i < energy; i++) {
                g.drawImage(energyIcon, 10 + (i * 40), 40, 30, 30, this);
            }

            // Draw the snake
            for (int i = 0; i < snake.size(); i++) {
                Point p = snake.get(i);
                if (i == 0) {
                    // Draw the head (boat)
                    g.drawImage(snakeHeadImage, p.x * TILE_SIZE, p.y * TILE_SIZE, BOAT_SIZE, BOAT_SIZE, this);
                } else {
                    // Draw the body (fish)
                    g.drawImage(snakeBodyImage, p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE, this);
                }
            }

            // Draw the food
            if (food != null) {
                if (isSpecialFood) {
                    // Draw Aang if special food
                    g.drawImage(aangImage, food.x * TILE_SIZE, food.y * TILE_SIZE, AANG_SIZE, AANG_SIZE, this);
                } else {
                    // Draw regular food
                    g.drawImage(foodImage, food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE, this);
                }
            }
        }

    }
}