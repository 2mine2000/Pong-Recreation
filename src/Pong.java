import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Pong extends JPanel implements Runnable {
    public static void main(String[] args) {
        new Pong();
    }
    public static final int TPS = 60;
    public static final int WALL_SICKNESS = 5;
    private static Pong instance;
    public final ArrayList<Wall> walls = new ArrayList<>();
    private final KeyHandler keyHandler;
    private Thread gameThread;
    public Ball ball;
    public final ArrayList<Player> players = new ArrayList<>();
    public JFrame window;

    private Pong() {
        instance = this;
        this.window = new JFrame("Pong");
        this.window.setLocationRelativeTo(null);
        this.window.setVisible(true);
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.window.setResizable(false);
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        screenDim = new Dimension((int) (screenDim.getHeight() * 1.5 * 3/4), (int) (screenDim.getHeight() * 3/4));
        this.window.setPreferredSize(screenDim);
        this.window.setLocation(this.window.getLocation().x - screenDim.width / 2, this.window.getLocation().y - screenDim.height / 2);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.window.add(this);
        this.window.pack();
        this.setBackground(Color.BLACK);
        this.keyHandler = new KeyHandler();
        this.addKeyListener(this.keyHandler);
        this.init();
        this.start();
    }

    public void init() {
        this.ball = Ball.spawn();
        Wall.create(0, 0, this.getWidth(), WALL_SICKNESS);
        Wall.create(0, this.getHeight() - WALL_SICKNESS, this.getWidth(), WALL_SICKNESS);
        Wall.create(0, 0, WALL_SICKNESS, this.getHeight()/5);
        Wall.create(0, (float) (this.getHeight() * 4) /5 , WALL_SICKNESS, this.getHeight()/5);
        Wall.create(this.getWidth() - WALL_SICKNESS, 0, WALL_SICKNESS, this.getHeight()/5);
        Wall.create(this.getWidth() - WALL_SICKNESS, (float) (this.getHeight() * 4) /5 , WALL_SICKNESS, this.getHeight()/5);
        //Wall.create(0, (float) Pong.getInstance().getHeight() /5, WALL_SICKNESS, Pong.getInstance().getHeight()/5 * 3, Color.ORANGE);
        //Wall.create(Pong.getInstance().getWidth() - WALL_SICKNESS, (float) Pong.getInstance().getHeight() /5, WALL_SICKNESS, Pong.getInstance().getHeight()/5 * 3, Color.ORANGE);
        Player.create((float) Pong.getInstance().getWidth() /8, 30, Color.WHITE, (key)-> key == KeyEvent.VK_Z || key == KeyEvent.VK_W || key == KeyEvent.VK_Q || key == KeyEvent.VK_A, (key)-> key == KeyEvent.VK_S || key == KeyEvent.VK_D);
        Player.create(this.getWidth()-20- (float) Pong.getInstance().getWidth() /8, 30, Color.WHITE, (key)-> key == KeyEvent.VK_UP || key == KeyEvent.VK_RIGHT, (key)-> key == KeyEvent.VK_DOWN || key == KeyEvent.VK_LEFT);
    }

    public void start() {
        this.gameThread = new Thread(this);
        this.gameThread.start();
    }

    private void tick() {
        for (Player player : this.players) {
            player.tick();
        }
        this.ball.tick();
    }

    private void paintTick(Graphics2D pGraphics) {
        this.drawScores(pGraphics);

        int pointille = 31;
        pGraphics.setColor(Color.GRAY);
        for (int i = 0; i < pointille; i++) {
            if (i % 2 == 0) {
                pGraphics.fillRect(this.getWidth()/2, i * getHeight() / pointille, 1, this.getHeight() / pointille);
            }
        }
        if (this.ball != null) {
            this.ball.draw(pGraphics);
        }
        for (Player player : this.players) {
            player.draw(pGraphics);
        }
        for (Wall wall : this.walls) {
            wall.draw(pGraphics);
        }
    }

    private void drawScores(Graphics2D pGraphics) {
        pGraphics.setColor(Color.GRAY);
        pGraphics.setFont(new Font(pGraphics.getFont().getFontName(), Font.BOLD, this.getHeight()/5));
        FontMetrics metrics = pGraphics.getFontMetrics();
        pGraphics.drawString(String.valueOf(this.players.getLast().getScore()), this.getWidth()/2 - this.getWidth()/16 - metrics.stringWidth(String.valueOf(this.players.getLast().getScore())), (int) (metrics.getHeight()/1.2));
        pGraphics.drawString(String.valueOf(this.players.getFirst().getScore()), this.getWidth()/2 + this.getWidth()/16, (int) (metrics.getHeight()/1.2));
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
        Graphics2D graphics2D = (Graphics2D) pGraphics;
        graphics2D.setColor(Color.WHITE);
        this.paintTick(graphics2D);
        graphics2D.dispose();
    }
}
