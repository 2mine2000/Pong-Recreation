import java.awt.*;
import java.util.function.Function;

public class Player extends Wall {
    private static final float SPEED = Pong.getInstance().getHeight() / 38.55f;
    private final Function<Integer ,Boolean> isUp;
    private final Function<Integer ,Boolean> isDown;
    private final boolean playable;
    private final float spawnY;
    public boolean up = false;
    public boolean down = false;
    private String moving = "";
    private int score = 0;

    private Player(float pX, float pY, Color pColor) {
        this(false, pX, pY, pColor, (key)->false, (key)->false);
    }

    private Player(float pX, float pY, Color pColor, Function<Integer ,Boolean> pIsUp, Function<Integer ,Boolean> pIsDown) {
        this(true, pX, pY, pColor, pIsUp, pIsDown);
    }

    private Player(boolean pPlayable, float pX, float pY, Color pColor, Function<Integer ,Boolean> pIsUp, Function<Integer ,Boolean> pIsDown) {
        super(pX, pY, (int) (Pong.getInstance().getHeight() / 77.1f * 2), (int) (Pong.getInstance().getHeight() / 77.1f * 15), pColor);
        this.playable = pPlayable;
        this.spawnY = pY;
        this.isUp = pIsUp;
        this.isDown = pIsDown;
    }

    @Override
    void tick() {
        if (this.playable) {
            if (this.up != this.down) {
                if (this.up) {
                    this.tryGoingUp();
                }else this.tryGoingDown();
            }
        }
    }

    @Override
    public void draw(Graphics2D pGraphics) {
        pGraphics.setColor(Color.DARK_GRAY);
        float startY = Pong.WALL_THICKNESS + Pong.getInstance().ball.getHeight() + Pong.getInstance().getHeight() / 771f*2;
        float endY = Pong.getInstance().getHeight() - (Pong.WALL_THICKNESS + Pong.getInstance().ball.getHeight() + Pong.getInstance().getHeight() / 771f*2);
        pGraphics.drawLine((int) this.getCenterX(), (int) startY, (int) this.getCenterX(), (int) endY);
        pGraphics.fillRect((int) this.getX(), (int) startY, this.getWidth(), 1);
        pGraphics.fillOval((int) (this.getCenterX() - this.getWidth()/4f), (int) (this.spawnY + this.getHeight()/2f-this.getWidth()/4f), (int) (this.getWidth()/2f), (int) (this.getWidth()/2f));
        pGraphics.fillRect((int) this.getX(), (int) endY, this.getWidth(), 1);
        super.draw(pGraphics);
    }

    public void tryGoingUp() {
        if (this.getY() - SPEED > Pong.WALL_THICKNESS + Pong.getInstance().ball.getHeight() + Pong.getInstance().getHeight() / 771f*2) {
            this.moving = "Up";
            this.move(0, -SPEED);
            Ball ball = Pong.getInstance().ball;
            if (ball.intersect(this)) {
                Pong.getInstance().ball.relocate(ball.getX(), this.getY() - (float) ball.getHeight());
                if (ball.getAngle() == 180){
                    Pong.getInstance().ball.setAngle(Ball.simplifyAngle(ball.getDirectOpposite() + (Math.random() - 0.49999) * Ball.ANGLE_MODIFIER));
                }else if (ball.getAngle() < 0) {
                    Pong.getInstance().ball.setAngle(Ball.simplifyAngle((-Math.random() * 45) + (Math.random() - 0.49999) * Ball.ANGLE_MODIFIER));
                }else {
                    Pong.getInstance().ball.setAngle(Ball.simplifyAngle((Math.random() * 45) + (Math.random() - 0.49999) * Ball.ANGLE_MODIFIER));
                }
                Pong.getInstance().ball.setBoost(Pong.getInstance().ball.getSpeed()/2, Pong.TPS);
            }
        }
    }

    public void tryGoingDown() {
        if (this.getEndY() + SPEED < Pong.getInstance().getHeight() - Pong.WALL_THICKNESS - Pong.getInstance().ball.getHeight() - Pong.getInstance().getHeight() / 771f*2) {
            this.moving = "Down";
            this.move(0, SPEED);
            Ball ball = Pong.getInstance().ball;
            if (ball.intersect(this)) {
                Pong.getInstance().ball.relocate(ball.getX(), this.getEndY());
                if (ball.getAngle() == 0){
                    Pong.getInstance().ball.setAngle(Ball.simplifyAngle(ball.getDirectOpposite() + (Math.random() - 0.49999) * Ball.ANGLE_MODIFIER));
                }else if (ball.getAngle() < 0) {
                    Pong.getInstance().ball.setAngle(Ball.simplifyAngle((180 + Math.random() * 45) + (Math.random() - 0.49999) * Ball.ANGLE_MODIFIER));
                }else {
                    Pong.getInstance().ball.setAngle(Ball.simplifyAngle((180 - Math.random() * 45) + (Math.random() - 0.49999) * Ball.ANGLE_MODIFIER));
                }
                Pong.getInstance().ball.setBoost(Pong.getInstance().ball.getSpeed()/2, Pong.TPS);
            }
        }
    }

    public void setMoving(String pMoving){
        if (pMoving.isEmpty() || pMoving.equals("Up") || pMoving.equals("Down")) {
            this.moving = pMoving;
        }
    }

    public String getMoving() {
        return this.moving;
    }

    public boolean isMoving() {
        return this.moving.equals("Up") || this.moving.equals("Down");
    }

    public void addScore(int pAddition) {
        this.setScore(this.getScore() + pAddition);
    }

    public void setScore(int pScore) {
        this.score = pScore;
    }

    public int getScore() {
        return this.score;
    }

    public boolean isUp(int pKey) {
        return this.isUp.apply(pKey);
    }

    public boolean isDown(int pKey) {
        return this.isDown.apply(pKey);
    }

    public static void create(float pX, float pY, Color pColor) {
        Pong.getInstance().players.add(new Player(pX, (float) Pong.getInstance().getHeight() /2 - 75, pColor));
    }

    public static void create(float pX, float pY, Color pColor, Function<Integer ,Boolean> pIsUp, Function<Integer ,Boolean> pIsDown) {
        Pong.getInstance().players.add(new Player(pX, (float) Pong.getInstance().getHeight() /2 - 75, pColor, pIsUp, pIsDown));
    }

}
