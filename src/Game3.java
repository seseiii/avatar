import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;

public class Game3 extends JPanel implements ActionListener, KeyListener {

    private final JFrame parentFrame;
    private final RoadMapWindow roadMapWindow;
    private static final long serialVersionUID = 1L;

    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;
        char direction = 'U'; // U D L R
        int velocityX = 0;
        int velocityY = 0;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity() {
            if (this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -tileSize / 4;
            } else if (this.direction == 'D') {
                this.velocityX = 0;
                this.velocityY = tileSize / 4;
            } else if (this.direction == 'L') {
                this.velocityX = -tileSize / 4;
                this.velocityY = 0;
            } else if (this.direction == 'R') {
                this.velocityX = tileSize / 4;
                this.velocityY = 0;
            }
        }

        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }

    private final int rowCount = 21;
    private final int columnCount = 19;
    private final int tileSize = 32;

    private final Image wallImage;
    private final Image lordOzaiImage;
    private final Image zukoImage;
    private final Image princessAzulaImage;
    private final Image kuviraImage;
    private final Image goldCoinImage;

    private final Image avatarUpImage;
    private final Image avatarDownImage;
    private final Image avatarLeftImage;
    private final Image avatarRightImage;

    private final String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "X       bpo       X",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;

    private Image backgroundImage;

    Timer gameLoop;
    char[] directions = {'U', 'D', 'L', 'R'};
    Random random = new Random();
    int score = 0;
    int lives = 3;
    boolean gameOver = false;

    public Game3(JFrame parentFrame, RoadMapWindow roadMapWindow) {
        this.parentFrame = parentFrame;
        this.roadMapWindow = roadMapWindow;

        JFrame frame = new JFrame("Fire Element Challenge");
        //Game3 gamePanel = new Game3(frame, roadMapWindow);
        //frame.add(gamePanel);

        goldCoinImage = new ImageIcon("src/img/powerFood.png").getImage();
        backgroundImage = new ImageIcon("src/img/firebg.png").getImage();

        setLayout(new BorderLayout());
        addKeyListener(this);
        setFocusable(true);

        frame.setLayout(new BorderLayout());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);

        wallImage = new ImageIcon(getClass().getResource("/img/wall.jpg")).getImage();
        lordOzaiImage = new ImageIcon(getClass().getResource("/img/lordOzai.png")).getImage();
        zukoImage = new ImageIcon(getClass().getResource("/img/zuko.png")).getImage();
        princessAzulaImage = new ImageIcon(getClass().getResource("/img/princessAzula.png")).getImage();
        kuviraImage = new ImageIcon(getClass().getResource("/img/kuvira.png")).getImage();

        avatarUpImage = new ImageIcon(getClass().getResource("/img/up.png")).getImage();
        avatarDownImage = new ImageIcon(getClass().getResource("/img/down.png")).getImage();
        avatarLeftImage = new ImageIcon(getClass().getResource("/img/left.png")).getImage();
        avatarRightImage = new ImageIcon(getClass().getResource("/img/right.png")).getImage();

        loadMap();
        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
        gameLoop = new Timer(60, this); // 20fps
        gameLoop.start();
        frame.setVisible(true);
    }

    public void loadMap() {
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);

                int x = c*tileSize;
                int y = r*tileSize;

                if (tileMapChar == 'X') { //block wall
                    Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                    walls.add(wall);
                }
                else if (tileMapChar == 'b') { //blue ghost
                    Block ghost = new Block(lordOzaiImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'o') { //orange ghost
                    Block ghost = new Block(zukoImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'p') { //pink ghost
                    Block ghost = new Block(princessAzulaImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'r') { //red ghost
                    Block ghost = new Block(kuviraImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'P') { //pacman
                    pacman = new Block(avatarRightImage, x, y, tileSize, tileSize);
                }
                else if (tileMapChar == ' ') { //food
                    Block food = new Block(goldCoinImage, x + 14, y + 14, 6, 6);
                    foods.add(food);
                }
            }
        }
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        int mapWidth = columnCount * tileSize;
        int mapHeight = rowCount * tileSize;
        int xOffset = (getWidth() - mapWidth) / 7;
        int yOffset = (getHeight() - mapHeight) / 7;

        g.drawImage(pacman.image, pacman.x + xOffset, pacman.y + yOffset, pacman.width, pacman.height, null);

        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x + xOffset, ghost.y + yOffset, ghost.width, ghost.height, null);
        }

        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x + xOffset, wall.y + yOffset, wall.width, wall.height, null);
        }

        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.drawImage(food.image, food.x + xOffset, food.y + yOffset, food.width, food.height, null);
        }

        g.setFont(new Font("Arial", Font.PLAIN, 70));
        g.setColor(Color.WHITE); // Ensure the text is visible
        if (gameOver) {
            g.drawString("", 830, 100); // Adjusted position for score and lives
        } else {
            g.drawString("Lives: " + lives , 830, 100); // Adjusted position for score and lives
            g.drawString("Score: " + score, 830, 250); // Adjusted position for score and lives

        }
    }

    public void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        for (Block ghost : ghosts) {
            if (collision(ghost, pacman)) {
                gameOver = true;
                handleGameOver("");
                if (lives == 0) {
                    MissionFailedDialog dialog = new MissionFailedDialog(parentFrame, roadMapWindow);
                    dialog.showMissionFailed();
                    return;
                }
                resetPositions();
            }

            if (ghost.y == tileSize * 9 && ghost.direction != 'U' && ghost.direction != 'D') {
                ghost.updateDirection('U');
            }
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            for (Block wall : walls) {
                if (collision(ghost, wall)) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    ghost.updateDirection(directions[random.nextInt(4)]);
                }
            }
        }

        for (Block food : foods) {
            if (collision(food, pacman)) {
                foods.remove(food);
                score += 2;

                if (score == 50) {
                    gameLoop.stop();

                    SwingUtilities.invokeLater(() -> {
                        // Show the Mission Complete dialog on top of the Game3 panel\
                        MissionCompleteDialog missionDialog = new MissionCompleteDialog(parentFrame, roadMapWindow);
                        missionDialog.showMissionComplete();
                        Game3.this.setVisible(false); // Hide the current Game2 window
                        roadMapWindow.unlockGame4();
                    });

                }
                break;
            }
        }
    }
    public boolean collision(Block a, Block b) {
        return  a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        for (Block ghost : ghosts) {
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver) {
            loadMap();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            gameLoop.start();
        }
        // System.out.println("KeyEvent: " + e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection('U');
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.updateDirection('D');
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection('L');
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection('R');
        }

        if (pacman.direction == 'U') {
            pacman.image = avatarUpImage;
        }
        else if (pacman.direction == 'D') {
            pacman.image = avatarDownImage;
        }
        else if (pacman.direction == 'L') {
            pacman.image = avatarLeftImage;
        }
        else if (pacman.direction == 'R') {
            pacman.image = avatarRightImage;
        }
    }

    private void handleGameOver(String message) {
        lives--; // Decrease energy by 1

        if (lives > 0) {
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
                loadMap();
                resetPositions();
                //lives = 3;
                score = 0;
                gameOver = false;
                gameLoop.start();
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
            MissionFailedDialog dialog = new MissionFailedDialog(parentFrame, roadMapWindow);
            dialog.showMissionFailed();
            Game3.this.setVisible(false);
        }
    }

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

    /*class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            backgroundImage = new ImageIcon("src/bgfire.png").getImage();
            System.out.println("Backgroud panel test");
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }*/
}