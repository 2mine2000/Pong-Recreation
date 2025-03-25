package net.l2mine2000.pong;

import net.l2mine2000.pong.handlers.KeyHandler;
import net.l2mine2000.pong.handlers.MouseHandler;
import net.l2mine2000.pong.handlers.MouseMotionHandler;
import net.l2mine2000.pong.shapes.Ball;
import net.l2mine2000.pong.shapes.DynamicShape;
import net.l2mine2000.pong.shapes.Player;
import net.l2mine2000.pong.shapes.Wall;
import net.l2mine2000.pong.shapes.buttons.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class Pong extends JPanel implements Runnable {
    public static void main(String[] args) {
        new Pong();
    }

    public static float PIXEL;
    public static final int MAIN_MENU = 0;
    public static final int PLAYING = 1;
    public static final int PAUSED = 2;
    public static final Cursor HIDDEN_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TRANSLUCENT), new Point(0, 0), "hidden_cursor");
    public static Cursor DEFAULT_CURSOR;
    public static final int TPS = 60;
    public static int WALL_THICKNESS;
    private static Pong instance;
    public final ArrayList<Wall> walls = new ArrayList<>();
    public final ArrayList<MenuButton> buttons = new ArrayList<>();
    private final MouseHandler mouseHandler;
    private final MouseMotionHandler mouseMotionHandler;
    private final KeyHandler keyHandler;
    private Thread gameThread;
    public Ball ball;
    public final ArrayList<Player> players = new ArrayList<>();
    public JFrame window;
    public Robot robot;
    public int gameState = -1;
    public int fadeInCooldown = 0;
    public int maxFadeInCooldown = 0;

    private Pong() {
        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            this.robot = null;
        }
        instance = this;
        this.window = new JFrame("Pong Remake");
        this.window.setLocationRelativeTo(null);
        this.window.setVisible(true);
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.window.setResizable(false);
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        screenDim = new Dimension((int) (screenDim.getHeight() * 1.5 * 3/4), (int) (screenDim.getHeight() * 3/4));
        this.window.setPreferredSize(screenDim);
        this.setPreferredSize(screenDim);
        this.window.setLocation(this.window.getLocation().x - screenDim.width / 2, this.window.getLocation().y - screenDim.height / 2);
        this.setLocation(this.window.getLocation().x - screenDim.width / 2, this.window.getLocation().y - screenDim.height / 2);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.requestFocusInWindow();
        this.window.add(this);
        this.window.pack();
        this.setBackground(Color.BLACK);
        this.keyHandler = new KeyHandler();
        this.mouseHandler = new MouseHandler();
        this.mouseMotionHandler = new MouseMotionHandler();
        DEFAULT_CURSOR = this.getCursor();
        this.setCursor(DEFAULT_CURSOR);
        this.addMouseListener(this.mouseHandler);
        this.addMouseMotionListener(this.mouseMotionHandler);
        this.addKeyListener(this.keyHandler);
        this.init();
        this.start();
    }

    public void init() {
        PIXEL = this.getHeight() / 771f;
        WALL_THICKNESS = (int) pixel(5);
        Wall.create(0, 0, this.getWidth(), WALL_THICKNESS);
        Wall.create(0, this.getHeight() - WALL_THICKNESS, this.getWidth(), WALL_THICKNESS);
        Wall.create(0, 0, WALL_THICKNESS, this.getHeight()/5);
        Wall.create(0, (float) (this.getHeight() * 4) /5 , WALL_THICKNESS, this.getHeight()/5);
        Wall.create(this.getWidth() - WALL_THICKNESS, 0, WALL_THICKNESS, this.getHeight()/5);
        Wall.create(this.getWidth() - WALL_THICKNESS, (float) (this.getHeight() * 4) /5 , WALL_THICKNESS, this.getHeight()/5);
        PlayButton.create(this.getWidth()/2f - pixel(200), this.getHeight()/2f + pixel(5), (int) pixel(400), (int) pixel(90), Color.GREEN, new Color(200, 255, 200), LightSide.TOP_LEFT, "Play", new Font("Dialog.plain", Font.BOLD, (int) pixel(40)));
        QuitButton.create(this.getWidth()/2f - pixel(100), this.getHeight()/2f + pixel(125), (int) pixel(200), (int) pixel(75), Color.RED, new Color(255, 200, 200), LightSide.TOP_LEFT, "Quit", null);
        ResumeButton.create(this.getWidth()/2f - pixel(150), this.getHeight()/2f + pixel(37.5f), (int) pixel(300), (int) pixel(75), Color.GREEN, new Color(200, 255, 200), LightSide.TOP_LEFT, "Resume", null);
        MainMenuButton.create(this.getWidth()/2f - pixel(150), this.getHeight()/2f + pixel(125), (int) pixel(300), (int) pixel(75), Color.RED, new Color(255, 200, 200), LightSide.TOP_LEFT, "Main menu", null);
        this.recreatePlayers();
        this.mainMenu();
    }

    public void start() {
        this.gameThread = new Thread(this);
        this.gameThread.start();
    }

    private void tick() {
        if (!this.isMenuOpen()) {
            DynamicShape.tickAll(this.players);
            if (this.ball != null) {
                this.ball.tick();
            }
        }else {
            DynamicShape.tickAll(this.buttons);
        }
        if (this.gameState == MAIN_MENU) {
            if (this.fadeInCooldown > 0) {
                this.fadeInCooldown--;
            }else {
                this.maxFadeInCooldown = 0;
            }
        }else this.setFadeInCooldown(0);
    }

    private void paintTick(PongGraphics pGraphics) {
        if (this.gameState == PLAYING || this.gameState == PAUSED) {
            this.drawScores(pGraphics);
            int pointille = 31;
            pGraphics.setColor(Color.GRAY);
            for (int i = 0; i < pointille; i++) {
                if (i % 2 == 0) {pGraphics.g.fillRect(this.getWidth()/2, i * getHeight() / pointille, 1, this.getHeight() / pointille);}
            }

            pGraphics.drawAll(this.players);
            if (this.ball != null) {
                this.ball.draw(pGraphics);
            }
            pGraphics.drawAll(this.walls);
        }

        if (this.gameState == PAUSED) {
            pGraphics.setColor(new Color(0, 0, 0, 175));
            pGraphics.g.fillRect(0, 0, this.getWidth(), this.getHeight());
            pGraphics.setColor(Color.WHITE);
            pGraphics.setFont(new Font(pGraphics.getFont().getFontName(), Font.BOLD, this.getHeight()/20));
            FontMetrics metrics = pGraphics.g.getFontMetrics();
            pGraphics.g.drawString("Paused", this.getWidth()/2f - metrics.stringWidth("Paused")/2f, this.getHeight()/2f - pixel(37.5f));

            pGraphics.setFont(new Font(pGraphics.getFont().getFontName(), Font.BOLD, this.getHeight()/5));
            metrics = pGraphics.g.getFontMetrics();
            pGraphics.setColor(Color.DARK_GRAY);
            pGraphics.g.drawString("P O N G", this.getWidth()/2f - metrics.stringWidth("P O N G")/2f, this.getHeight()/3f+pixel(20));
            pGraphics.setColor(Color.GRAY);
            pGraphics.g.drawString("P O N G", this.getWidth()/2f - metrics.stringWidth("P O N G")/2f, this.getHeight()/3f+pixel(10));
            pGraphics.setColor(Color.WHITE);
            pGraphics.g.drawString("P O N G", this.getWidth()/2f - metrics.stringWidth("P O N G")/2f, this.getHeight()/3f);
        }

        if (this.gameState == MAIN_MENU) {
            pGraphics.setFont(new Font(pGraphics.getFont().getFontName(), Font.BOLD, this.getHeight()/5));
            FontMetrics metrics = pGraphics.g.getFontMetrics();
            pGraphics.setColor(Color.DARK_GRAY);
            pGraphics.g.drawString("P O N G", this.getWidth()/2f - metrics.stringWidth("P O N G")/2f, this.getHeight()/3.25f + pixel(20));
            pGraphics.setColor(Color.GRAY);
            pGraphics.g.drawString("P O N G", this.getWidth()/2f - metrics.stringWidth("P O N G")/2f, this.getHeight()/3.25f + pixel(10));
            pGraphics.setColor(Color.WHITE);
            pGraphics.g.drawString("P O N G", this.getWidth()/2f - metrics.stringWidth("P O N G")/2f, this.getHeight()/3.25f);
        }

        if (this.gameState == PAUSED || this.gameState == MAIN_MENU) {
            pGraphics.drawAll(this.buttons);
        }

        if (this.fadeInCooldown > 0) {
            if (this.fadeInCooldown < this.maxFadeInCooldown) {
                float ratio = ((float) this.fadeInCooldown) / ((float) this.maxFadeInCooldown);
                pGraphics.setColor(new Color(0, 0, 0, ratio));
            }else pGraphics.setColor(Color.BLACK);
            pGraphics.g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
    }

    private void drawScores(PongGraphics pGraphics) {
        pGraphics.setColor(Color.GRAY);
        pGraphics.setFont(new Font(pGraphics.getFont().getFontName(), Font.BOLD, this.getHeight()/5));
        FontMetrics metrics = pGraphics.g.getFontMetrics();
        pGraphics.g.drawString(String.valueOf(this.players.getLast().getScore()), this.getWidth()/2 - this.getWidth()/16 - metrics.stringWidth(String.valueOf(this.players.getLast().getScore())), (int) (metrics.getHeight()/1.2));
        pGraphics.g.drawString(String.valueOf(this.players.getFirst().getScore()), this.getWidth()/2 + this.getWidth()/16, (int) (metrics.getHeight()/1.2));
    }

    public static Pong getInstance() {
        return instance;
    }

    @Override
    public void run() {
        double drawInterval = 1000000000d / TPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (this.gameThread != null) {
            this.megaTick();
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                this.tick();
                this.repaint();
                delta--;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics pGraphics) {
        super.paintComponent(pGraphics);
        PongGraphics pongGraphics = PongGraphics.create(pGraphics);
        this.paintTick(pongGraphics);
        pongGraphics.dispose();
    }

    protected void megaTick() {
        PIXEL = this.getHeight() / 771f;
        WALL_THICKNESS = (int) pixel(5);
        if (!this.isMenuOpen()) {
            this.moveMouse(this.getWidth()/2f, this.getHeight()/2f);
        }
        if (!this.hasFocus()) {
            this.requestFocusInWindow();
        }
    }

    public void recreatePlayers() {
        this.players.clear();
        Player.create((float) Pong.getInstance().getWidth() /8, 30, Color.WHITE, (key)-> key == KeyEvent.VK_Z || key == KeyEvent.VK_W || key == KeyEvent.VK_Q || key == KeyEvent.VK_A, (key)-> key == KeyEvent.VK_S || key == KeyEvent.VK_D);
        Player.create(this.getWidth()-20- (float) Pong.getInstance().getWidth() /8, 30, Color.WHITE, (key)-> key == KeyEvent.VK_UP || key == KeyEvent.VK_RIGHT, (key)-> key == KeyEvent.VK_DOWN || key == KeyEvent.VK_LEFT);
    }

    public void moveMouse(float pX, float pY) {
        if (this.robot != null) {
            Point point = this.getLocationOnScreen();
            this.robot.mouseMove((int) (point.getX() + pX), (int) (point.getY() + pY));
        }
    }

    public void moveMouse(Point2D.Float pPoint) {
        this.moveMouse(pPoint.x, pPoint.y);
    }

    public void setFadeInCooldown(int pTicks) {
        this.fadeInCooldown = pTicks;
        this.maxFadeInCooldown = (int) (pTicks/10f*9);
    }

    public boolean isMenuOpen() {
        return this.gameState == PAUSED || this.gameState == MAIN_MENU;
    }

    public boolean resume() {
        if (this.gameState == PAUSED) {
            this.gameState = PLAYING;
            this.setCursor(HIDDEN_CURSOR);
            return true;
        }return false;
    }

    public boolean pause() {
        if (this.gameState == PLAYING) {
            this.gameState = PAUSED;
            this.moveMouse(this.getWidth()/2f, this.getHeight()/2f);
            this.setCursor(DEFAULT_CURSOR);
            return true;
        }return false;
    }

    public boolean mainMenu() {
        if (this.gameState != MAIN_MENU) {
            this.setFadeInCooldown((int) (TPS*1.5f));
            this.gameState = MAIN_MENU;
            this.ball = null;
            this.setCursor(DEFAULT_CURSOR);
            return true;
        }return false;
    }

    public boolean startNewGame() {
        if (this.gameState == MAIN_MENU) {
            this.setFadeInCooldown(0);
            this.ball = Ball.spawn();
            this.gameState = PLAYING;
            this.setCursor(HIDDEN_CURSOR);
            return true;
        }return false;
    }

    public void quit() {
        this.window.dispose();
        System.exit(0);
    }

    public boolean updateCursor(Cursor pCursor) {
        if (this.getCursor() != pCursor) {
            this.setCursor(pCursor);
            return true;
        }return false;
    }

    public static float pixel() {
        return pixel(1f);
    }

    public static float pixel(float pCount) {
        return PIXEL * pCount;
    }


    public enum LightSide {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT;

        public LightSide getOpposite() {
            return switch (this) {
                case TOP_LEFT -> BOTTOM_RIGHT;
                case TOP_RIGHT -> BOTTOM_LEFT;
                case BOTTOM_LEFT -> TOP_RIGHT;
                case BOTTOM_RIGHT -> TOP_LEFT;
            };
        }

        public LightSide getClockwise() {
            return switch (this) {
                case TOP_LEFT -> TOP_RIGHT;
                case TOP_RIGHT -> BOTTOM_RIGHT;
                case BOTTOM_LEFT -> TOP_LEFT;
                case BOTTOM_RIGHT -> BOTTOM_LEFT;
            };
        }

        public LightSide getCounterClockwise() {
            return switch (this) {
                case TOP_LEFT -> BOTTOM_LEFT;
                case TOP_RIGHT -> TOP_LEFT;
                case BOTTOM_LEFT -> BOTTOM_RIGHT;
                case BOTTOM_RIGHT -> TOP_RIGHT;
            };
        }
    }
}
