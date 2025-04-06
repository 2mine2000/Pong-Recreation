package net.l2mine2000.pong.shapes;

import net.l2mine2000.pong.Direction;
import net.l2mine2000.pong.Pong;
import net.l2mine2000.pong.PongGraphics;
import net.l2mine2000.pong.shapes.buttons.ColoredForButton;

import java.awt.*;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

public class Player extends Wall {
    private static final float SPEED = Pong.pixel(20);
    private final Function<Integer ,Boolean> isUp;
    private final Function<Integer ,Boolean> isDown;
    private final boolean playable;
    private final Direction.AxisX frontSide;
    private final float spawnY;
    public boolean up = false;
    public boolean down = false;
    private String moving = "";
    private int score = 0;
    private final AIDifficulty aiDifficulty;

    private Player(float pX, float pY, Color pColor, Direction.AxisX pFrontSide, AIDifficulty pDifficulty) {
        this(false, pX, pY, pColor, pFrontSide, pDifficulty, (_)->false, (_)->false);
    }

    private Player(float pX, float pY, Color pColor, Direction.AxisX pFrontSide, Function<Integer ,Boolean> pIsUp, Function<Integer ,Boolean> pIsDown) {
        this(true, pX, pY, pColor, pFrontSide, AIDifficulty.EMPTY, pIsUp, pIsDown);
    }

    private Player(boolean pPlayable, float pX, float pY, Color pColor, Direction.AxisX pFrontSide, AIDifficulty pAIDifficulty, Function<Integer ,Boolean> pIsUp, Function<Integer ,Boolean> pIsDown) {
        super(pX, pY, (int) Pong.pixel(20), (int) Pong.pixel(150), pColor, Pong.State.PLAYING, Pong.State.PAUSED);
        this.playable = pPlayable;
        this.spawnY = pY;
        this.isUp = pIsUp;
        this.isDown = pIsDown;
        this.frontSide = pFrontSide;
        this.aiDifficulty = pAIDifficulty;
    }

    @Override
    public void tick(Pong pPong) {
        if (this.playable) {
            if (this.up != this.down) {
                if (this.up) {
                    if (!this.tryGoingUp(SPEED)) {this.moving = "";}
                }else if (!this.tryGoingDown(SPEED)) {this.moving = "";}
            }else this.moving = "";
        }else {
            Ball ball = Pong.getInstance().ball;
            float speedRatio = (ball.getTotalSpeed()) / (ball.getTotalSpeed() > Ball.MAX_SPEED ? Ball.MAX_SPEED + ball.getBoost() : Ball.MAX_SPEED);
            float focusDistance = Pong.getInstance().getWidth()/2f * speedRatio - Pong.pixel(this.aiDifficulty.getFocusDistancePixelRemoval() * (1-speedRatio));

            if (this.frontSide.equals(Direction.AxisX.RIGHT)) {
                if (ball.getCenterX() >= this.getCenterX()) {
                    this.runAfterTheBall(focusDistance);
                }else this.runAwayFromTheBall();

            }else {
                if (ball.getCenterX() <= this.getCenterX()) {
                    this.runAfterTheBall(focusDistance);
                }else this.runAwayFromTheBall();
            }
        }
    }

    private void runAfterTheBall(float pFocusDistance) {
        Ball ball = Pong.getInstance().ball;
        float focusEndLocation = Pong.getInstance().getWidth()/2f + (this.frontSide.equals(Direction.AxisX.RIGHT) ? pFocusDistance : -pFocusDistance);
        double sin = Math.abs(Math.sin(Math.toRadians(ball.getAngle()) - Math.PI/2));

        if (this.frontSide.equals(Direction.AxisX.RIGHT) ? (ball.getX() < focusEndLocation) : (ball.getEndX() > focusEndLocation)) {
            if (ball.getCenterY() > this.getCenterY()+this.aiDifficulty.getYDetectionRange(this.getHeight())) {
                if (!this.tryGoingDown((float) (this.aiDifficulty.getGenericSpeed() * sin))) {this.moving = "";}
            }
            else if (ball.getCenterY() < this.getCenterY()-this.aiDifficulty.getYDetectionRange(this.getHeight())) {
                if (!this.tryGoingUp((float) (this.aiDifficulty.getGenericSpeed() * sin))) {this.moving = "";}
            }
        }else {
            float outsideFocusDistance = this.frontSide.equals(Direction.AxisX.RIGHT) ? (ball.getX() - focusEndLocation) : (focusEndLocation - ball.getEndX());
            if (outsideFocusDistance > Pong.pixel(450)) {
                outsideFocusDistance = Pong.pixel(450);
            }
            float dynamicSpeed = outsideFocusDistance <= 0 ? this.aiDifficulty.getGenericSpeed() : this.aiDifficulty.getGenericSpeed() * (1-outsideFocusDistance/Pong.pixel(450));

            if (ball.getCenterY() > this.getCenterY()+this.aiDifficulty.getYDetectionRange(this.getHeight())) {
                if (!this.tryGoingDown((float) ((dynamicSpeed) * sin))) {this.moving = "";}
            }
            else if (ball.getCenterY() < this.getCenterY()-this.aiDifficulty.getYDetectionRange(this.getHeight())) {
                if (!this.tryGoingUp((float) ((dynamicSpeed) * sin))) {this.moving = "";}
            }
        }
    }

    private void runAwayFromTheBall() {
        Ball ball = Pong.getInstance().ball;
        double sin = Math.abs(Math.sin(Math.toRadians(ball.getAngle()) - Math.PI/2));

        if (ball.getEndY() > this.getY() - this.aiDifficulty.getYRunAwayDistance(this.getHeight()) && ball.getY() < this.getEndY() + this.aiDifficulty.getYRunAwayDistance(this.getHeight())) {
            if (ball.getCenterY() > this.getCenterY()) {
                if (!this.tryGoingUp((float) (this.aiDifficulty.getGenericSpeed() * sin))) {this.moving = "";}
            }
            else if (ball.getCenterY() < this.getCenterY()) {
                if (!this.tryGoingDown((float) (this.aiDifficulty.getGenericSpeed() * sin))) {this.moving = "";}
            }
        }
    }

    @Override
    public void draw(PongGraphics pGraphics) {
        pGraphics.setColor(Color.DARK_GRAY);
        float startY = Pong.WALL_THICKNESS + Pong.getInstance().ball.getHeight()*1.2f + Pong.pixel(2);
        float endY = Pong.getInstance().getHeight() - (Pong.WALL_THICKNESS + Pong.getInstance().ball.getHeight()*1.2f + Pong.pixel(2));
        pGraphics.g.drawLine((int) this.getCenterX(), (int) startY, (int) this.getCenterX(), (int) endY);
        pGraphics.g.fillRect((int) this.getX(), (int) startY, this.getWidth(), 1);
        pGraphics.g.fillOval((int) (this.getCenterX() - this.getWidth()/4f), (int) (this.spawnY + this.getHeight()/2f-this.getWidth()/4f), (int) (this.getWidth()/2f), (int) (this.getWidth()/2f));
        pGraphics.g.fillRect((int) this.getX(), (int) endY, this.getWidth(), 1);
        super.draw(pGraphics);
    }

    public boolean tryGoingUp(float pSpeed, boolean pOverrideLimit) {
        float pLimit = Pong.WALL_THICKNESS + Pong.getInstance().ball.getHeight()*1.2f + Pong.pixel(2);
        if (pOverrideLimit || this.getY() - pSpeed > pLimit) {
            this.moving = "Up";
            this.move(0, -pSpeed);
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
            }return true;
        }else if (this.getY() > pLimit) {
            return this.tryGoingUp(this.getY() - pLimit, true);
        }return false;
    }

    public boolean tryGoingUp(float pSpeed) {
        return this.tryGoingUp(pSpeed, false);
    }

    public boolean tryGoingDown(float pSpeed, boolean pOverrideLimit) {
        float pLimit = Pong.getInstance().getHeight() - Pong.WALL_THICKNESS - Pong.getInstance().ball.getHeight()*1.2f - Pong.pixel(2);
        if (pOverrideLimit || this.getEndY() + pSpeed < pLimit) {
            this.moving = "Down";
            this.move(0, pSpeed);
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
            }return true;
        }else if (this.getEndY() < pLimit) {
            return this.tryGoingDown(pLimit - this.getEndY(), true);
        }return false;
    }

    public boolean tryGoingDown(float pSpeed) {
        return this.tryGoingDown(pSpeed, false);
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

    public static void create(float pX, float pY, Color pColor, Direction.AxisX pFrontSide, AIDifficulty pAIDifficulty) {
        Pong.getInstance().players.add(new Player(pX, (float) Pong.getInstance().getHeight() /2 - 75, pColor, pFrontSide, pAIDifficulty));
    }

    public static void create(float pX, float pY, Color pColor, Direction.AxisX pFrontSide, Function<Integer ,Boolean> pIsUp, Function<Integer ,Boolean> pIsDown) {
        Pong.getInstance().players.add(new Player(pX, (float) Pong.getInstance().getHeight() /2 - 75, pColor, pFrontSide, pIsUp, pIsDown));
    }

    public enum AIDifficulty implements ColoredForButton {
        EMPTY("Empty", (_)->0f, (_)->0f, 0f, (_)->0f),
        BABY("Super easy", (height)->height/2f, (height)->height/20f, 500f, (defaultSpeed)->defaultSpeed*0.6f),
        EASY("Easy", (height)->height/4f, (height)->height*2f, 450f, (defaultSpeed)->defaultSpeed*0.8f),
        NORMAL("Normal", (height)->height/8f, Float::valueOf, 300f, (defaultSpeed)->defaultSpeed),
        HARD("Hard", (height)->height/16f, (height)->height/2f, 150f, (defaultSpeed)->defaultSpeed*1.2f),
        IMPOSSIBLE("Super hard", (height)->height/100f, (_)->Pong.getInstance().ball.getHeight()+Pong.pixel(5), 0f, (defaultSpeed)->defaultSpeed/2 + Pong.getInstance().ball.getTotalSpeed()*0.9f);

        private final String name;
        private final Function<Integer, Float> yDetectionRange;
        private final Function<Integer, Float> yRunAwayDistance;
        private final float focusDistancePixelRemoval;
        private final Function<Float, Float> genericSpeed;

        AIDifficulty(String pName, Function<Integer, Float> pYDetectionRange, Function<Integer, Float> pYRunAwayDistance, float pFocusDistancePixelRemoval, Function<Float, Float> pGenericSpeed) {
            this.name = pName;
            this.yDetectionRange = pYDetectionRange;
            this.yRunAwayDistance = pYRunAwayDistance;
            this.focusDistancePixelRemoval = pFocusDistancePixelRemoval;
            this.genericSpeed = pGenericSpeed;
        }

        public float getYDetectionRange(int pHeight) {
            return this.yDetectionRange.apply(pHeight);
        }

        public float getYRunAwayDistance(int pHeight) {
            return this.yRunAwayDistance.apply(pHeight);
        }

        public float getFocusDistancePixelRemoval() {
            return this.focusDistancePixelRemoval;
        }

        public float getGenericSpeed() {
            return this.genericSpeed.apply(SPEED);
        }

        @Override
        public String toString() {
            return this.name;
        }

        @Override
        public boolean useColor() {
            return false;
        }

        @Override
        public Color getMainColor() {
            return null;
        }

        @Override
        public Color getTextColor() {
            return null;
        }
    }

}