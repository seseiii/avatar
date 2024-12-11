import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Game4 extends JPanel implements ActionListener, KeyListener {
    private static final long serialVersionUID = 1L;

    private final RoadMapWindow roadMapWindow;

    int boardWidth;
    int boardHeight;

    // Images
    Image backgroundImg;
    Image birdImg;
    Image cloudImg;
    Image energyIcon;
    Image missionCompleteImg;
    Image missionFailedImg;

    // Bird dimensions
    int birdX;
    int birdY;
    int birdWidth = 75;
    int birdHeight = 55;

    // Cloud dimensions
    int cloudWidth;
    int cloudHeight;

    // Game entities
    Bird bird;
    ArrayList<Obstacle> obstacles;
    Random random = new Random();

    // Timers
    Timer gameLoop;
    Timer placeObstacleTimer;

    // Game state
    boolean gameOver = false;
    double score = 0;
    boolean gameStarted = false;

    // Buttons
    JButton playButton;

    // Physics
    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;

    // Energy management
    private int energy = 3; // Player starts with 3 energy points

    // Mission
    private final int MISSION_SCORE = 10;

    public Game4(RoadMapWindow roadMapWindow) {

        this.roadMapWindow = roadMapWindow;


        // Get screen dimensions
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        boardWidth = screenSize.width;
        boardHeight = screenSize.height;

        birdX = boardWidth / 8;
        birdY = boardHeight / 2;

        // Load images
        try {
            backgroundImg = new ImageIcon(getClass().getResource("/img/flappybirdbg.jpg")).getImage();
            birdImg = new ImageIcon(getClass().getResource("/img/flappybird.png")).getImage();
            cloudImg = new ImageIcon(getClass().getResource("/img/cloud.png")).getImage();
            energyIcon = new ImageIcon(getClass().getResource("/img/energy.png")).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            missionCompleteImg = new ImageIcon(getClass().getResource("/img/missioncomplete.png")).getImage().getScaledInstance(305, 220, Image.SCALE_SMOOTH);
            missionFailedImg = new ImageIcon(getClass().getResource("/img/failed.png")).getImage().getScaledInstance(305, 220, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading images: " + e.getMessage(),
                    "Image Load Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Adjust cloud dimensions
        cloudWidth = boardWidth / 12;
        cloudHeight = (int) ((double) cloudImg.getHeight(null) / cloudImg.getWidth(null) * cloudWidth);

        bird = new Bird(birdImg);
        obstacles = new ArrayList<>();

        // Set layout to null for absolute positioning
        setLayout(null);
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        // Initialize Timers
        gameLoop = new Timer(16, this); // Around 60 FPS
        placeObstacleTimer = new Timer(2000, e -> placeObstacles());

        SwingUtilities.invokeLater(() -> {
            // Create a temporary JFrame for the StartScreen parent
            JFrame tempFrame = new JFrame();
            tempFrame.setUndecorated(true); // Hide the frame decorations
            tempFrame.setSize(400, 380); // Match StartScreen dimensions
            tempFrame.setLocationRelativeTo(null); // Center on screen

            // Show the StartScreen dialog
            new StartScreen(
                    tempFrame,
                    "src/img/airmission1.png",
                    new Color(10, 180, 190),
                    null
            ).setVisible(true);

            tempFrame.dispose(); // Dispose of the temporary frame after StartScreen closes

            playbutton();

            // Set up the main game window
            JFrame frame = new JFrame("Flappy Bird Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setUndecorated(false); // Keep the title bar
            frame.setResizable(false); // Disable resizing
            frame.setSize(boardWidth, boardHeight); // Ensure window size matches game size

            frame.add(this);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

    }

    // Method to dispose of the play button
    private void disposePlayButton() {
        remove(playButton);
        repaint();
        requestFocusInWindow(); // Request focus so the panel is ready for key events
    }


    class Bird {
        int x, y, width, height;
        Image img;

        Bird(Image img) {
            this.img = img;
            this.x = birdX;
            this.y = birdY;
            this.width = birdWidth;
            this.height = birdHeight;
        }

        Rectangle getBounds() {
            int marginX = width / 8;
            int marginY = height / 8;
            return new Rectangle(x + marginX, y + marginY, width - 2 * marginX, height - 2 * marginY);
        }
    }

    class Obstacle {
        int x, y, width, height;
        Image img;

        Obstacle(Image img, int x, int y, int width, int height) {
            this.img = img;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        Rectangle getBounds() {
            int marginX = width / 8;
            int marginY = height / 8;
            return new Rectangle(x + marginX, y + marginY, width - 2 * marginX, height - 2 * marginY);
        }
    }

    void playbutton() {

        // Start Button
        playButton = new JButton("Play");
        playButton.setBounds((boardWidth - 80) / 2, (boardHeight / 2) + 200, 80, 40);

        playButton.setFocusable(false);
        playButton.setFont(new Font("Arial", Font.BOLD, 20));
        playButton.setBackground(new Color(30, 171, 205));
        playButton.setForeground(Color.WHITE);
        playButton.setBorderPainted(false);
        playButton.setOpaque(true);
        playButton.addActionListener(e -> {
            disposePlayButton(); // Dispose of the play button
            startGame(); // Start the game after disposing of the play button

        });

        add(playButton);

    }

    void startGame() {

        gameStarted = true;
        bird.y = birdY;
        velocityY = 0; // Set initial velocity to 0
        obstacles.clear();
        score = 0;
        gameOver = false;
        gameLoop.start();
        placeObstacleTimer.start();
    }

    void placeObstacles() {
        int numberOfClouds = 3;
        for (int i = 0; i < numberOfClouds; i++) {
            int cloudX = boardWidth + (i * 300) + random.nextInt(100);
            int cloudY = random.nextInt(boardHeight - cloudHeight);
            obstacles.add(new Obstacle(cloudImg, cloudX, cloudY, cloudWidth, cloudHeight));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);
        for (Obstacle obstacle : obstacles) {
            g.drawImage(obstacle.img, obstacle.x, obstacle.y, obstacle.width, obstacle.height, null);
        }

        // Draw the red effect for collision
        if (collisionArea != null) {
            g.setColor(new Color(255, 0, 0, 128)); // Semi-transparent red
            g.fillRect(collisionArea.x, collisionArea.y, collisionArea.width, collisionArea.height);
        }

        // Draw the score
        g.setColor(Color.ORANGE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.drawString(String.valueOf((int) score), boardWidth / 2 - 25, 60);

        // Draw energy icons at the top-left corner
        for (int i = 0; i < energy; i++) {
            g.drawImage(energyIcon, 10 + (i * 40), 10, 30, 30, this);
        }

    }


    public void move() {
        if (!gameStarted) return; // Exit if the game has not started

        velocityY += gravity;
        bird.y += velocityY;

        if (bird.y < 0) bird.y = 0; // Prevent bird from going above the screen

        for (int i = 0; i < obstacles.size(); i++) {
            Obstacle obstacle = obstacles.get(i);
            obstacle.x += velocityX;

            if (obstacle.x + obstacle.width < 0) {
                score += 5;
                obstacles.remove(i);
                i--;
                continue;
            }

            if (collision(bird, obstacle)) {
                gameOver = true;
                gameStarted = false;

                GameOverDialog.decrementEnergy();
                // Call GameOverDialog and pass a lambda for restarting the game
                GameOverDialog.handleGameOver(
                        SwingUtilities.getWindowAncestor(this), // Pass the parent window
                        this::startGame // Restart the game using the GamePanel instance
                );
                break;
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true;
            gameStarted = false;
            energy--;
            // Call GameOverDialog and pass a lambda for restarting the game
            GameOverDialog.handleGameOver(
                    SwingUtilities.getWindowAncestor(this), // Pass the parent window
                    this::startGame // Restart the game using the GamePanel instance
            );
        }

        if (score >= MISSION_SCORE) {
            gameLoop.stop();
            placeObstacleTimer.stop();

            SwingUtilities.invokeLater(() -> {

                // Show Mission Failed dialog
                MissionCompleteDialog dialog = new MissionCompleteDialog(null, roadMapWindow);
                dialog.showMissionComplete(); // Show the dialog

                // Make the window invisible first
                this.setVisible(false);

                // Now dispose the parent window (or Game3)
                Window parentWindow = (Window) SwingUtilities.getWindowAncestor(Game4.this);
                if (parentWindow != null) {
                    parentWindow.dispose();  // Dispose the parent window
                }

                roadMapWindow.dispose();


//            	// You can also hide Game3 here if it's not already done
//            	Game4.this.setVisible(false);

                new LastWindow();

            });

        }
    }

    Rectangle collisionArea = null;

    boolean collision(Bird bird, Obstacle obstacle) {
        BufferedImage birdImage = toBufferedImage(bird.img);
        BufferedImage obstacleImage = toBufferedImage(obstacle.img);

        // Get the intersection rectangle
        Rectangle birdBounds = new Rectangle(bird.x, bird.y, bird.width, bird.height);
        Rectangle obstacleBounds = new Rectangle(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
        Rectangle intersection = birdBounds.intersection(obstacleBounds);

        collisionArea = birdBounds.intersection(obstacleBounds);

        // If there is no intersection, return false
        if (intersection.isEmpty()) {
            return false;
        }

        if (collisionArea.isEmpty()) {
            collisionArea = null; // Reset collision area
            return false;
        }

        // Check each pixel in the intersection for non-transparent overlap
        for (int i = 0; i < intersection.width; i++) {
            for (int j = 0; j < intersection.height; j++) {
                int birdPixelX = intersection.x + i - bird.x;
                int birdPixelY = intersection.y + j - bird.y;

                int obstaclePixelX = intersection.x + i - obstacle.x;
                int obstaclePixelY = intersection.y + j - obstacle.y;

                // Get RGBA values of the pixels
                int birdPixel = birdImage.getRGB(birdPixelX * birdImage.getWidth() / bird.width, birdPixelY * birdImage.getHeight() / bird.height);
                int obstaclePixel = obstacleImage.getRGB(obstaclePixelX * obstacleImage.getWidth() / obstacle.width, obstaclePixelY * obstacleImage.getHeight() / obstacle.height);

                // Check if both pixels are not transparent
                if ((birdPixel >> 24) != 0x00 && (obstaclePixel >> 24) != 0x00) {
                    return true; // Collision detected
                }
            }
        }

        return false; // No collision
    }

    private BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image onto the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bimage;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            move();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Ignore key presses if the game is over
        if (gameOver) return;

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameStarted) {
                velocityY = -10; // Allow jumping only if the game has started
            }
        }
    }


    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}


}