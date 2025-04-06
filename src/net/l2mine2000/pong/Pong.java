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
import java.util.function.Function;

public class Pong extends JPanel implements Runnable {
    public static void main(String[] args) {
        new Pong();
    }

    public static float PIXEL;
    public static String FONT_NAME = "Dialog.plain";
    public static final Color SLIGHTLY_RED = new Color(255, 200, 200);
    public static final Color SLIGHTLY_GREEN = new Color(200, 255, 200);
    public static final Color SLIGHTLY_BLUE = new Color(220, 220, 255);
    public static final Color SLIGHTLY_YELLOW = new Color(255, 255, 200);
    public static final Cursor HIDDEN_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TRANSLUCENT), new Point(0, 0), "hidden_cursor");
    public static Cursor DEFAULT_CURSOR;
    public static final int TPS = 60;
    public static int WALL_THICKNESS;
    private static Pong instance;
    public final ArrayList<Wall> walls = new ArrayList<>();
    public final ArrayList<MenuButton> buttons = new ArrayList<>();
    public final MouseHandler mouseHandler;
    public final MouseMotionHandler mouseMotionHandler;
    public final KeyHandler keyHandler;
    private Thread gameThread;
    public Ball ball;
    public final ArrayList<Player> players = new ArrayList<>();
    public JFrame window;
    public Robot robot;
    public State state = State.EMPTINESS;
    public int fadeInCooldown = 0;
    public int maxFadeInCooldown = 0;
    public float fadeRatio = 0f;
    public boolean fadeReverse = false;
    private boolean leavingTheGame = false;

    public boolean inDropDownList = false;

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
        this.registerButtons();
        this.setOrCreatePlayers();
        this.mainMenu();
    }

    private void registerButtons() {
        //MultiStates :

        //Play button 0 - MultiState(MULTIPLAYER_MENU, SINGLEPLAYER_MENU, SIMULATION_MENU)
        PlayButton.create(this.getWidth()/2f - pixel(200), this.getHeight()/4f*3f, (int) pixel(400), (int) pixel(90), Color.GREEN, SLIGHTLY_GREEN, DiagonalDirection.TOP_LEFT, "Play", new Font(FONT_NAME, Font.BOLD, (int) pixel(40)),
                (pV)-> switch (this.state) {
                    case MULTIPLAYER_MENU -> 1;
                    case SINGLEPLAYER_MENU -> 15;
                    case SIMULATION_MENU -> 21;
                    default -> 1;
                }, (nV)->1, State.MULTIPLAYER_MENU, State.SINGLEPLAYER_MENU, State.SIMULATION_MENU);
        //<- Back button 1 - MultiState
        StateSelectionButton.create(pixel(20), pixel(20), (int) pixel(100), (int) pixel(50), Color.RED, SLIGHTLY_RED, DiagonalDirection.TOP_LEFT, State.MAIN_MENU, "< Back", null, (pV)->0, (nV)->switch (this.state) {
            case MULTIPLAYER_MENU -> 0;
            case SINGLEPLAYER_MENU -> 8;
            case SIMULATION_MENU -> 27;
            default -> 0;
        }, State.MULTIPLAYER_MENU, State.SINGLEPLAYER_MENU, State.SIMULATION_MENU);

        //SingleState :
        Font font = new Font(FONT_NAME, Font.BOLD, (int) pixel(35));
        //Multiplayer mode button 2 - MAIN_MENU State
        StateSelectionButton.create(this.getWidth()/2f - pixel(200), this.getHeight()/2f - pixel(110), (int) pixel(400), (int) pixel(90), new Color(25, 255, 25), SLIGHTLY_GREEN, DiagonalDirection.TOP_LEFT, State.MULTIPLAYER_MENU, font, (pV)->5, (nV)->3, State.MAIN_MENU);
        //Singleplayer mode button 3 - MAIN_MENU State
        StateSelectionButton.create(this.getWidth()/2f - pixel(200), this.getHeight()/2f, (int) pixel(400), (int) pixel(90), Color.YELLOW, SLIGHTLY_YELLOW, DiagonalDirection.TOP_LEFT, State.SINGLEPLAYER_MENU, font, (pV)->2, (nV)->4, State.MAIN_MENU);
        //Simulation mode button 4 - MAIN_MENU State
        StateSelectionButton.create(this.getWidth()/2f - pixel(200), this.getHeight()/2f + pixel(110), (int) pixel(400), (int) pixel(90), new Color(75, 75, 255), SLIGHTLY_BLUE, DiagonalDirection.TOP_LEFT, State.SIMULATION_MENU, font, (pV)->3, (nV)->5, State.MAIN_MENU);
        //Quit button 5 - MAIN_MENU State
        QuitButton.create(this.getWidth()/2f - pixel(100), this.getHeight()/2f + pixel(220), (int) pixel(200), (int) pixel(75), Color.RED, SLIGHTLY_RED, DiagonalDirection.TOP_LEFT, "Quit", new Font(FONT_NAME, Font.BOLD, (int) pixel(30)), (pV)->4, (nV)->2, State.MAIN_MENU);


        //Resume button 6 - PAUSED State
        StateSelectionButton.create(this.getWidth()/2f - pixel(150), this.getHeight()/2f + pixel(37.5f), (int) pixel(300), (int) pixel(75), Color.GREEN, SLIGHTLY_GREEN, DiagonalDirection.TOP_LEFT, State.PLAYING, "Resume", null, (pV)->7, (nV)->7, State.PAUSED);
        //Main menu button 7 - PAUSED State
        StateSelectionButton.create(this.getWidth()/2f - pixel(150), this.getHeight()/2f + pixel(125), (int) pixel(300), (int) pixel(75), Color.RED, SLIGHTLY_RED, DiagonalDirection.TOP_LEFT, State.MAIN_MENU, null, (pV)->6, (nV)->6, State.PAUSED);

        font = PongGraphics.setFontSize(font, (int) pixel(35));
        //Playing side switch 8 - a_1/2 (ON) - SINGLEPLAYER_MENU State
        SwitchButton.create((int) pixel(100), (int) pixel(50), MenuButton.MAIN_RED, MenuButton.TEXT_RED, DiagonalDirection.TOP_LEFT, true, "Left", null, (pV)->1, (nV)->nV?15:9, State.SINGLEPLAYER_MENU);
        //Playing side switch 9 - a_2/2 (OFF) - SINGLEPLAYER_MENU State
        SwitchButton.create((int) pixel(100), (int) pixel(50), MenuButton.MAIN_BLUE, MenuButton.TEXT_BLUE, DiagonalDirection.TOP_LEFT, false, "Right", null, (pV)->pV?1:8, (nV)->15, State.SINGLEPLAYER_MENU);
        SwitchButton.link((SwitchButton) this.buttons.get(8), (SwitchButton) this.buttons.get(9), true, "Your side :", Color.WHITE, font, this.getWidth()/2f, this.getHeight()/2f-Pong.pixel(7));

        //AI difficulty 15 (opt 10-14) - SINGLEPLAYER_MENU State
        DropDownListButton.create(Player.AIDifficulty.class, (t)->!t.equals(Player.AIDifficulty.EMPTY), Player.AIDifficulty.NORMAL, (int) (this.getWidth()/2f + font.getSize()/2f), (int) (this.getHeight()/2f+pixel(43)), ((int) pixel(217)), ((int) pixel(50)), MenuButton.MAIN_GREEN, MenuButton.TEXT_GREEN, Color.WHITE, DiagonalDirection.TOP_LEFT, "Bot difficulty :", null, font, (pV)->pV?8:9, (nV)->0, State.SINGLEPLAYER_MENU);
        //AI difficulty 21 (opt 16-20) - Right bot - SIMULATION_MENU State
        DropDownListButton.create(Player.AIDifficulty.class, (t)->!t.equals(Player.AIDifficulty.EMPTY), Player.AIDifficulty.NORMAL, (int) (this.getWidth()/2f + font.getSize()/2f), (int) (this.getHeight()/2f+pixel(43)), ((int) pixel(217)), ((int) pixel(50)), MenuButton.MAIN_BLUE, MenuButton.TEXT_BLUE, Color.WHITE, DiagonalDirection.TOP_LEFT, "Right bot level :", null, font, (pV)->27, (nV)->0, State.SIMULATION_MENU);
        //AI difficulty 27 (opt 22-26) - Left bot - SIMULATION_MENU State
        DropDownListButton.create(Player.AIDifficulty.class, (t)->!t.equals(Player.AIDifficulty.EMPTY), Player.AIDifficulty.NORMAL, (int) (this.getWidth()/2f + font.getSize()/2f), (int) (this.getHeight()/2f-Pong.pixel(32)), ((int) pixel(217)), ((int) pixel(50)), MenuButton.MAIN_RED, MenuButton.TEXT_RED, Color.WHITE, DiagonalDirection.TOP_LEFT, "Left bot level :", null, font, (pV)->1, (nV)->21, State.SIMULATION_MENU);
    }

    public void start() {
        this.gameThread = new Thread(this);
        this.gameThread.start();
    }

    private void tick() {
        if (this.leavingTheGame && this.fadeInCooldown <= 1) {
            this.quit();
        }

        this.keyHandler.tick();

        if (!this.isMenuOpen()) {
            DynamicShape.tickAll(this.players);
            if (this.ball != null) {
                this.ball.tick();
            }
        }else {
            DynamicShape.tickAll(this.buttons);
        }

        if (this.fadeInCooldown > 0) {
            this.fadeInCooldown--;
        } else if (this.fadeInCooldown < 0) {
            this.fadeInCooldown++;
        } else {
            this.maxFadeInCooldown = 0;
        }
    }

    private void paintTick(PongGraphics pGraphics) {
        if (this.state.isOne(State.PLAYING, State.PAUSED)) {
            pGraphics.drawScores();
            int pointille = 31;
            pGraphics.setColor(Color.GRAY);
            for (int i = 0; i < pointille; i++) {
                if (i % 2 == 0) {pGraphics.g.fillRect(this.getWidth()/2, i * getHeight() / pointille, 1, this.getHeight() / pointille);}
            }
        }

        pGraphics.drawAll(this.players);
        pGraphics.drawIfExist(this.ball);
        pGraphics.drawAll(this.walls);

        if (this.state.is(State.PAUSED)) {
            pGraphics.setColor(new Color(0, 0, 0, 175));
            pGraphics.g.fillRect(0, 0, this.getWidth(), this.getHeight());
            pGraphics.drawTitle(3f, -20);
            pGraphics.drawCenteredString("Paused", (int) (this.getHeight()/20f), Color.WHITE, 0, pixel(12.5f));
        }

        pGraphics.drawAll(this.buttons);

        if (this.state.isMenu() && !this.state.is(State.PAUSED)) {
            if (this.state.isOne(State.MULTIPLAYER_MENU, State.SINGLEPLAYER_MENU, State.SIMULATION_MENU)) {
                pGraphics.drawTitle(3.25f, 20, this.state.getTextColor());
            }else pGraphics.drawTitle(3.25f, 20);

            if (!this.state.is(State.MAIN_MENU)) {
                pGraphics.drawCenteredString(this.state.getName(), (int) (this.getHeight()/20f), this.state.getMainColor(), 0, -pixel(90));
            }
        }

        if (this.state.is(State.MULTIPLAYER_MENU)) {
            pGraphics.drawCenteredString("No parameters for Multiplayer mode", (int) pixel(35), Color.WHITE, 0, pixel(50));
            //pGraphics.g.drawString("No parameters for Multiplayer mode", this.getWidth()/2f-metrics.stringWidth("No parameters for Multiplayer mode")/2f, 0);
        }

        pGraphics.drawFading();
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
            this.moveMouse(this.getWidth()/2f, this.getHeight()/2f-pixel(5));
        }
        if (!this.hasFocus()) {
            this.requestFocusInWindow();
        }
    }

    public void setOrCreatePlayers(Player.AIDifficulty pFirstDifficulty, Player.AIDifficulty pSecondDifficulty) {
        this.players.clear();
        this.createBotPlayer(1, pFirstDifficulty);
        this.createBotPlayer(2, pSecondDifficulty);
    }

    public void setOrCreatePlayers(Player.AIDifficulty pOpponentDifficulty) {
        this.setOrCreatePlayers(pOpponentDifficulty, false);
    }

    public void setOrCreatePlayers(Player.AIDifficulty pOpponentDifficulty, boolean pInvert) {
        this.players.clear();
        if (pInvert) {
            this.createBotPlayer(1, pOpponentDifficulty);
            this.createPlayer(2, true);
        }else {
            this.createPlayer(1, true);
            this.createBotPlayer(2, pOpponentDifficulty);
        }
    }

    public void setOrCreatePlayers() {
        this.players.clear();
        this.createPlayer(1);
        this.createPlayer(2);
    }

    public void createPlayer(int pPlayer) {
        if (pPlayer < 2) {
            Player.create((float) Pong.getInstance().getWidth() /8, 30, Color.WHITE, Direction.AxisX.RIGHT, (key)-> key == KeyEvent.VK_Z || key == KeyEvent.VK_W || key == KeyEvent.VK_Q || key == KeyEvent.VK_A, (key)-> key == KeyEvent.VK_S || key == KeyEvent.VK_D);
        }else Player.create(this.getWidth()-20- (float) Pong.getInstance().getWidth() /8, 30, Color.WHITE, Direction.AxisX.LEFT, (key)-> key == KeyEvent.VK_UP || key == KeyEvent.VK_RIGHT, (key)-> key == KeyEvent.VK_DOWN || key == KeyEvent.VK_LEFT);
    }

    public void createPlayer(int pPlayer, boolean pSinglePlayer) {
        if (pSinglePlayer) {
            Function<Integer, Boolean> allUp = (key)-> key == KeyEvent.VK_Z || key == KeyEvent.VK_W || key == KeyEvent.VK_Q || key == KeyEvent.VK_A || key == KeyEvent.VK_UP || key == KeyEvent.VK_RIGHT;
            Function<Integer, Boolean> allDown = (key)-> key == KeyEvent.VK_S || key == KeyEvent.VK_D || key == KeyEvent.VK_DOWN || key == KeyEvent.VK_LEFT;
            if (pPlayer < 2) {
                Player.create((float) Pong.getInstance().getWidth() /8, 30, Color.WHITE, Direction.AxisX.RIGHT, allUp, allDown);
            }else Player.create(this.getWidth()-20- (float) Pong.getInstance().getWidth() /8, 30, Color.WHITE, Direction.AxisX.LEFT, allUp, allDown);
        }else createPlayer(pPlayer);
    }

    public void createBotPlayer(int pPlayer, Player.AIDifficulty pDifficulty) {
        if (pPlayer < 2) {
            Player.create((float) Pong.getInstance().getWidth() /8, 30, Color.WHITE, Direction.AxisX.RIGHT, pDifficulty);
        }else Player.create(this.getWidth()-20- (float) Pong.getInstance().getWidth() /8, 30, Color.WHITE, Direction.AxisX.LEFT, pDifficulty);
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
        this.setFadeInCooldown(pTicks, false);
    }

    public void setFadeInCooldown(int pTicks, boolean pReverse) {
        this.fadeReverse = pReverse;
        this.fadeInCooldown = pTicks;
        this.maxFadeInCooldown = (int) (pTicks/10f*9);
    }

    public void setState(State pState) {
        this.state = pState;
        this.setFadeInCooldown(0);
    }

    public boolean isMenuOpen() {
        return this.state.isMenu();
    }

    public boolean resume() {
        if (this.state.is(State.PAUSED)) {
            this.setState(State.PLAYING);
            this.setCursor(HIDDEN_CURSOR);
            MenuButton.deselectAll();
            return true;
        }return false;
    }

    public boolean pause() {
        if (this.state.is(State.PLAYING)) {
            this.setState(State.PAUSED);
            this.moveMouse(this.getWidth()/2f, this.getHeight()/2f-pixel(5));
            this.setCursor(DEFAULT_CURSOR);
            MenuButton.deselectAll();
            return true;
        }return false;
    }

    public boolean mainMenu() {
        if (!this.state.is(State.MAIN_MENU)) {
            this.setState(State.MAIN_MENU);
            this.setFadeInCooldown((int) (TPS*1.5f), false);
            this.ball = null;
            MenuButton.deselectAll();
            return true;
        }return false;
    }

    public boolean startNewGame() {
        if (!this.state.isOne(State.PAUSED, State.PLAYING)) {
            this.ball = Ball.spawn();
            this.setState(State.PLAYING);
            this.setFadeInCooldown((int) (Pong.TPS*0.5f));
            this.setCursor(HIDDEN_CURSOR);
            MenuButton.deselectAll();
            return true;
        }return false;
    }

    public void quit(boolean pFadeOut) {
        if (pFadeOut) {
            this.leavingTheGame = true;
            this.setFadeInCooldown((int) (TPS*0.5f), true);
        }else this.quit();
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

    public enum DiagonalDirection {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT;

        public DiagonalDirection getOpposite() {
            return switch (this) {
                case TOP_LEFT -> BOTTOM_RIGHT;
                case TOP_RIGHT -> BOTTOM_LEFT;
                case BOTTOM_LEFT -> TOP_RIGHT;
                case BOTTOM_RIGHT -> TOP_LEFT;
            };
        }

        public DiagonalDirection getClockwise() {
            return switch (this) {
                case TOP_LEFT -> TOP_RIGHT;
                case TOP_RIGHT -> BOTTOM_RIGHT;
                case BOTTOM_LEFT -> TOP_LEFT;
                case BOTTOM_RIGHT -> BOTTOM_LEFT;
            };
        }

        public DiagonalDirection getCounterClockwise() {
            return switch (this) {
                case TOP_LEFT -> BOTTOM_LEFT;
                case TOP_RIGHT -> TOP_LEFT;
                case BOTTOM_LEFT -> BOTTOM_RIGHT;
                case BOTTOM_RIGHT -> TOP_RIGHT;
            };
        }
    }

    public enum State {
        EMPTINESS("Limbo", false),
        MAIN_MENU("Main menu"),
        MULTIPLAYER_MENU("Multiplayer"),
        SINGLEPLAYER_MENU("Singleplayer"),
        SIMULATION_MENU("Simulation"),
        PLAYING("Playing", false),
        PAUSED("Paused");

        private final String name;
        private final boolean isMenu;

        State(String pName) {
            this(pName, true);
        }

        State(String pName, boolean pIsMenu) {
            this.name = pName;
            this.isMenu = pIsMenu;
        }

        public String getName() {
            return this.name;
        }

        public boolean isMenu() {
            return this.isMenu;
        }

        public boolean is(State pState) {
            return pState.equals(this);
        }

        public boolean isOne(State... pStates) {
            for (State state : pStates) {
                if (state.equals(this)) {
                    return true;
                }
            }return false;
        }

        public Color getMainColor() {
            return switch (this) {
                case EMPTINESS -> new Color((float) Math.random(), (float) Math.random(), (float) Math.random(), 0);
                case MAIN_MENU -> MenuButton.MAIN_RED;
                case MULTIPLAYER_MENU, PLAYING -> MenuButton.MAIN_GREEN;
                case SINGLEPLAYER_MENU -> MenuButton.MAIN_YELLOW;
                case SIMULATION_MENU -> MenuButton.MAIN_BLUE;
                default -> Color.WHITE;
            };
        }

        public Color getTextColor() {
            return switch (this) {
                case EMPTINESS -> new Color((float) Math.random(), (float) Math.random(), (float) Math.random(), 0);
                case MAIN_MENU -> MenuButton.TEXT_RED;
                case MULTIPLAYER_MENU, PLAYING -> MenuButton.TEXT_GREEN;
                case SINGLEPLAYER_MENU -> MenuButton.TEXT_YELLOW;
                case SIMULATION_MENU -> MenuButton.TEXT_BLUE;
                default -> Color.WHITE;
            };
        }

        public MenuButton getFirstButton() {
            int index = switch (this) {
                case MAIN_MENU -> 2;
                case MULTIPLAYER_MENU -> 0;
                case SINGLEPLAYER_MENU -> 8;
                case SIMULATION_MENU -> 27;
                case PAUSED -> 6;
                default -> -1;
            };
            if (index < 0) {
                throw new RuntimeException("State : " + this + ", is not a menu");
            }return Pong.getInstance().buttons.get(index);
        }

        public MenuButton getLastButton() {
            int index = switch (this) {
                case MAIN_MENU -> 5;
                case MULTIPLAYER_MENU, SIMULATION_MENU, SINGLEPLAYER_MENU -> 0;
                case PAUSED -> 7;
                default -> -1;
            };
            if (index < 0) {
                throw new RuntimeException("State : " + this + ", is not a menu");
            }return Pong.getInstance().buttons.get(index);
        }
    }
}