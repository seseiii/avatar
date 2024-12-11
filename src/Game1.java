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

    public Game1(RoadMapWindow roadMapWindow) {

        this.roadMapWindow = roadMapWindow;

        // Show Start Screen first
        new StartScreen(
                this,
                "src/img/watermision1.png",
                new Color(0, 155, 155),
                null
        ).setVisible(true);

        // Configure the main game frame
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full-screen mode
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(new GamePanel());
        setResizable(false);
        setVisible(false); // Initially hidden until the StartScreen closes
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

        public void startGame() {
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

                if (score < 60) {
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

                // Call GameOverDialog and pass a lambda for restarting the game
                GameOverDialog.handleGameOver(
                        SwingUtilities.getWindowAncestor(this), // Pass the parent window
                        this::startGame // Restart the game using the GamePanel instance
                );
            }

            for (int i = 1; i < snake.size(); i++) {
                if (head.equals(snake.get(i))) {
                    gameOver = true;
                    timer.stop();

                    // Call GameOverDialog again
                    GameOverDialog.handleGameOver(
                            SwingUtilities.getWindowAncestor(this),
                            this::startGame
                    );
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
            for (int i = 0; i < GameOverDialog.getEnergy(); i++) {
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