package net.l2mine2000.pong.shapes;

import net.l2mine2000.pong.Pong;
import net.l2mine2000.pong.PongGraphics;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Ball extends DynamicShape {
    public static final double ANGLE_MODIFIER = 12.5;
    private static final int GRADIENT_LIMITER = 7;
    public static final float BASE_SPEED = Pong.pixel(10);
    public static final float MAX_SPEED = Pong.pixel(50);
    public static final float SPEED_RISING_TIME = 2.5f * 60;
    private float speed = 0f;
    private float speedBoost = 0f;
    private int boostTime = 0;
    private final Color lastColor;
    private double angle = 0;
    private int maxCooldown = 0;
    private int cooldown = 0;
    private final boolean chaotic;
    private String goal = "";
    private final int power;

    private Ball() {
        this(Color.WHITE, Color.WHITE, 1);
    }
    private Ball(Color pColor, Color pLastColor, int pPower) {
        this((int) Pong.pixel(20), pColor, pLastColor, pPower, false);
    }

    private Ball(int pSize, Color pColor, Color pLastColor, int pPower, boolean pChaotic) {
        super(pSize, pSize, pColor);
        this.lastColor = pLastColor;
        this.power = pPower;
        this.chaotic = pChaotic;
    }

    @Override
    public void tick() {
        if (this.boostTime - 1 > 0) {
            this.boostTime--;
        } else {
            this.boostTime = 0;
            if (this.speedBoost - 0.1f >= 0){
                this.speedBoost -= 0.1f;
            }else if (this.speedBoost + 0.1f <= 0) {
                this.speedBoost += 0.1f;
            }else this.speedBoost = 0;
        }
        if (this.cooldown > 0) {
            this.cooldown--;
        }else {
            this.maxCooldown = 0;
            if (this.chaotic && Math.random() < Math.random()/25) {
                this.setAngle(this.getAngle() + Math.random() * 360);
                this.setBoost((float) (Math.random() - (this.getSpeed() > Pong.pixel(20) ? 0.2 : 0) * Pong.pixel(20)), (int) (Math.random() * Pong.TPS / (Math.random()+0.00001)*2));
            }
            if (!this.isColliding(Pong.getInstance().walls) && !this.isColliding(Pong.getInstance().players)) {
                this.updatePosition();
                if (this.getSpeed() < BASE_SPEED) {
                    this.setSpeed(this.getSpeed() + 0.05f);
                }
                else if (this.getSpeed() < MAX_SPEED) {
                    this.setSpeed(this.getSpeed() + (MAX_SPEED-BASE_SPEED)/(SPEED_RISING_TIME*Pong.TPS));
                }else this.setSpeed(MAX_SPEED);
            }
        }
        if (this.getEndY() < 0 || this.getY() > Pong.getInstance().getHeight()) {
            Pong.getInstance().ball = spawn(0, "", this.getColor());
        }
        else if (this.getEndX() - (float) this.getWidth() /2 < 0) {
            if (this.power < 0) {
                Pong.getInstance().players.getLast().addScore(this.power);
            }else Pong.getInstance().players.getFirst().addScore(this.power);
            Pong.getInstance().ball = spawn(Pong.TPS, "left", this.getColor());
        }
        else if (this.getX() + (float) this.getWidth() /2 > Pong.getInstance().getWidth()) {
            if (this.power < 0) {
                Pong.getInstance().players.getFirst().addScore(this.power);
            }else Pong.getInstance().players.getLast().addScore(this.power);
            Pong.getInstance().ball = spawn(Pong.TPS, "right", this.getColor());
        }
    }

    @Override
    public void draw(PongGraphics pGraphics) {
        if (this.cooldown > this.maxCooldown/GRADIENT_LIMITER) {
            float ratio = (float) (this.cooldown - this.maxCooldown / GRADIENT_LIMITER) / (this.maxCooldown - (float) this.maxCooldown / GRADIENT_LIMITER);
            Color background = Pong.getInstance().getBackground();
            Color gradientColor = pGraphics.getColorBetween(this.lastColor, background, 1 - Math.max(0, Math.min(1, ratio)));

            switch (this.goal) {
                case "left" -> pGraphics.drawGradient(0, (float) Pong.getInstance().getHeight() /5, Pong.getInstance().getWidth()/10, Pong.getInstance().getHeight()/5 * 3, gradientColor, background);
                case "right" -> pGraphics.drawGradient(Pong.getInstance().getWidth() - (float) Pong.getInstance().getWidth() /10, (float) Pong.getInstance().getHeight() /5, Pong.getInstance().getWidth()/10, Pong.getInstance().getHeight()/5 * 3, background, gradientColor);
            }
        }
        super.draw(pGraphics);
        if (this.cooldown > 0) {
            double cos = Math.cos(Math.toRadians(this.angle) + Math.PI/2);
            double sin = Math.sin(Math.toRadians(this.angle) + Math.PI/2);
            float ratio = (1f - (float) this.cooldown /this.maxCooldown);
            pGraphics.g.drawLine((int) this.getCenterX(), (int) this.getCenterY(), ((int) ((this.getWidth() * 3 * ratio) * -cos + this.getCenterX())), ((int) ((this.getWidth() * 3 * ratio) * -sin + this.getCenterY())));
        }
        pGraphics.g.fillOval(((int) this.getX()), ((int) this.getY()), this.getWidth(), this.getHeight());
    }

    public double getAngle() {
        return simplifyAngle(this.angle);
    }

    public void setAngle(double pAngle) {
        this.angle = simplifyAngle(pAngle);
    }

    public void updatePosition() {
        if (this.cooldown <= 0) {
            double cos = Math.cos(Math.toRadians(this.angle) - Math.PI/2);
            double sin = Math.sin(Math.toRadians(this.angle) - Math.PI/2);
            this.move(((float) ((this.speed+this.speedBoost) * cos)), ((float) ((this.speed+this.speedBoost) * sin)));
        }
    }

    public float getBoost() {
        return this.speedBoost;
    }

    public void setBoost(float pSpeedBoost, int pBoostTime) {
        this.speedBoost = pSpeedBoost;
        this.boostTime = pBoostTime;
    }

    public float getSpeed() {
        return this.speed;
    }

    public void setSpeed(float pSpeed) {
        this.speed = pSpeed;
    }

    public float getTotalSpeed() {
        return this.speed + this.speedBoost;
    }

    public float getCooldown() {
        return this.cooldown;
    }

    public float getMaxCooldown() {
        return this.maxCooldown;
    }

    public void setCooldown(int pCooldown) {
        this.cooldown = pCooldown;
        this.maxCooldown = pCooldown;
    }

    public Ball getDuplicate() {
        Ball ball = new Ball(this.getWidth(), this.getColor(), this.lastColor, this.power, this.chaotic);
        ball.relocate(this.getX(), this.getY());
        ball.setAngle(this.angle);
        ball.setSpeed(this.speed);
        ball.setCooldown(this.cooldown);
        return ball;
    }

    public <T extends DynamicShape> boolean isColliding(ArrayList<T> pShapes) {
        float distance = this.speed + this.speedBoost;
        Ball fake = this.getDuplicate();
        for (int i = 0; i <= distance; i++) {
            double cos = Math.cos(Math.toRadians(fake.angle) + Math.PI/2);
            double sin = Math.sin(Math.toRadians(fake.angle) + Math.PI/2);
            fake.move((float) -cos, (float) -sin);
            for (DynamicShape shape : pShapes) {
                if (fake.intersect(shape)) {
                    Rectangle intersection = shape.getCollisionBox().intersection(fake.getCollisionBox());
                    if (shape instanceof Player player && player.isMoving()) {
                        switch (player.getMoving()) {
                            case "Down": {
                                if (this.getAngle() > -67.5 && this.getAngle() < 67.5) {
                                    this.setBoost(-this.getSpeed() / 3, Pong.TPS*2);
                                }else if (this.getAngle() < -157.5 || this.getAngle() > 157.5) {
                                    this.setBoost(this.getSpeed() / 3, Pong.TPS);
                                }
                                if (this.getAngle() > 0 && this.getAngle() < 180) {
                                    this.setAngle(this.getAngle() + Math.random() * ANGLE_MODIFIER);
                                    if (this.getAngle() <= 0) {
                                        this.setAngle(1);
                                    }
                                    if (this.getAngle() >= 180) {
                                        this.setAngle(179);
                                    }
                                }else if (this.getAngle() < 0) {
                                    this.setAngle(this.getAngle() - Math.random() * ANGLE_MODIFIER);
                                    if (this.getAngle() >= 0) {
                                        this.setAngle(-1);
                                    }
                                    if (this.getAngle() <= -180) {
                                        this.setAngle(-179);
                                    }
                                }
                                break;
                            }
                            case "Up": {
                                if (this.getAngle() > -67.5 && this.getAngle() < 67.5) {
                                    this.setBoost(this.getSpeed() / 3, Pong.TPS);
                                }else if (this.getAngle() < -157.5 || this.getAngle() > 157.5) {
                                    this.setBoost(-this.getSpeed() / 3, Pong.TPS*2);
                                }
                                if (this.getAngle() > 0 && this.getAngle() < 180) {
                                    this.setAngle(this.getAngle() - Math.random() * ANGLE_MODIFIER);
                                    if (this.getAngle() <= 0) {
                                        this.setAngle(1);
                                    }
                                    if (this.getAngle() >= 180) {
                                        this.setAngle(179);
                                    }
                                }else if (this.getAngle() < 0) {
                                    this.setAngle(this.getAngle() + Math.random() * ANGLE_MODIFIER);
                                    if (this.getAngle() >= 0) {
                                        this.setAngle(-1);
                                    }
                                    if (this.getAngle() <= -180) {
                                        this.setAngle(-179);
                                    }
                                }
                                break;
                            }
                        }
                    }
                    if (intersection.width >= intersection.height) {
                        this.modifyAngleOnHorizontal();
                    }else this.modifyAngleOnVertical();
                    this.move((float) (i * -cos), (float) (i * -sin));
                    this.setSpeed(this.getSpeed() + (MAX_SPEED-BASE_SPEED)/(SPEED_RISING_TIME*Pong.TPS) * 15);
                    return true;
                }
            }
        }
        return false;
    }

    private void modifyAngleOnVertical() {
        if (this.angle == 0 || this.angle == 180) {
            this.setAngle(this.getDirectOpposite() + (Math.random() - 0.49999) * ANGLE_MODIFIER);
        }
        this.setAngle((-this.angle) + (Math.random() - 0.49999) * ANGLE_MODIFIER);
    }

    private void modifyAngleOnHorizontal() {
        if (Math.abs(this.angle) == 90) {
            this.setAngle(this.getDirectOpposite() + (Math.random() - 0.49999) * ANGLE_MODIFIER);
        }
        this.setAngle(simplifyAngle((-this.angle + 180) + (Math.random() - 0.49999) * ANGLE_MODIFIER));
    }

    public double getDirectOpposite() {
        return simplifyAngle(this.angle + 180);
    }

    public static double simplifyAngle(double pAngle) {
        while (pAngle > 180) {
            pAngle -= 360;
        }
        while (pAngle <= -180) {
            pAngle += 360;
        }
        return pAngle;
    }

    public static Ball spawn(int pCooldown, String pGoal, Color pLastColor) {
        double val = Math.random();
        boolean enragedBall = val < 0.16;
        boolean outragedBall = val < 0.04;
        boolean chaoticBall = val < 0.01;
        Ball ball;
        if (chaoticBall) {
            ball = new Ball((int) Pong.pixel(100), new Color(0, 255, 0), pLastColor, -1, true);
        }else if (outragedBall) {
            ball = new Ball(new Color(200, 0, 255), pLastColor, 3);
        }else if (enragedBall) {
            ball = new Ball(Color.RED, pLastColor, 2);
        }else {
            ball = new Ball(Color.WHITE, pLastColor, 1);
        }
        Random random = new Random();
        if (random.nextBoolean()) {
            ball.setAngle(-160 + (random.nextDouble() * 140));
        } else {
            ball.setAngle(20 + (random.nextDouble() * 140));
        }
        ball.goal = pGoal;
        ball.setCooldown(pCooldown);
        if (chaoticBall) {
            ball.setSpeed(-BASE_SPEED/1.75f);
            ball.setCooldown((int) (pCooldown*0.5));
        } else if (outragedBall) {
            ball.setSpeed(MAX_SPEED - BASE_SPEED);
            ball.setCooldown(pCooldown*4);
        } else if (enragedBall) {
            ball.setSpeed((MAX_SPEED - BASE_SPEED)/2 );
            ball.setCooldown((int) (pCooldown * 2.5));
        }
        return ball;
    }

    public static Ball spawn() {
        Ball ball = new Ball();
        Random random = new Random();
        if (random.nextBoolean()) {
            ball.setAngle(-160 + (random.nextDouble() * 140));
        } else {
            ball.setAngle(20 + (random.nextDouble() * 140));
        }
        ball.setCooldown(Pong.TPS*3);
        return ball;
    }
}
