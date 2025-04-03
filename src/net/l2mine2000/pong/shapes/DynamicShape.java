package net.l2mine2000.pong.shapes;

import net.l2mine2000.pong.Pong;
import net.l2mine2000.pong.PongGraphics;
import net.l2mine2000.pong.Tickable;
import org.w3c.dom.css.Rect;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public abstract class DynamicShape implements Tickable {
    protected final HashSet<Pong.State> allowedStates = new HashSet<>();
    private float x;
    private float y;
    private int width;
    private int height;
    private Color color;

    public DynamicShape(int pWidth, int pHeight, Pong.State... pAllowedStates) {
        this(pWidth, pHeight, Color.WHITE, pAllowedStates);
    }

    public DynamicShape(int pWidth, int pHeight, Color pColor, Pong.State... pAllowedStates) {
        this(((float) Pong.getInstance().getWidth()) /2 - ((float) pWidth)/2, ((float) Pong.getInstance().getHeight())/2 - ((float) pHeight)/2, pWidth, pHeight, pColor, pAllowedStates);
    }

    public DynamicShape(float pX, float pY, int pWidth, int pHeight, Pong.State... pAllowedStates) {
        this(pX, pY, pWidth, pHeight, Color.WHITE, pAllowedStates);
    }

    public DynamicShape(float pX, float pY, int pWidth, int pHeight, Color pColor, Pong.State... pAllowedStates) {
        this.resize(pWidth, pHeight);
        this.relocate(pX, pY);
        this.color = pColor;
        if (pAllowedStates.length == 0) {
            this.allowedStates.addAll(List.of(Pong.State.values()));
        }else this.allowedStates.addAll(Arrays.asList(pAllowedStates));
    }

    public void draw(PongGraphics pGraphics) {
        pGraphics.setColor(this.color);
    }

    public boolean isVisible(Pong.State pState) {
        return this.allowedStates.contains(pState);
    }

    public boolean isVisible() {
        return this.allowedStates.contains(Pong.getInstance().state);
    }

    public void relocate(float pX, float pY) {
        this.x = pX;
        this.y = pY;
    }

    public void move(float pOffsetX, float pOffsetY) {
        this.relocate(this.getX() + pOffsetX, this.getY() + pOffsetY);
    }

    public void resize(int pWidth, int pHeight) {
        this.width = pWidth;
        this.height = pHeight;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public float getEndX() {
        return this.x + this.width;
    }

    public float getEndY() {
        return this.y + this.height;
    }

    public float getCenterX() {
        return this.getX() + (float) (this.getWidth() / 2);
    }

    public float getCenterY() {
        return this.getY() + (float) (this.getHeight() / 2);
    }

    public void setColor(Color pColor) {
        this.color = pColor;
    }

    public Color getColor() {
        return this.color;
    }

    public boolean intersect(DynamicShape pOther) {
        return this.intersect(pOther.getCollisionBox());
    }

    public boolean intersect(Rectangle pOther) {
        return this.getCollisionBox().intersects(pOther);
    }

    public Rectangle getCollisionBox() {
        return new Rectangle((int) this.getX(), (int) this.getY(), this.getWidth(), this.getHeight());
    }

    public static DynamicShape of(Rectangle pRectangle) {
        return new DynamicShape((float) pRectangle.getX(), (float) pRectangle.getY(), pRectangle.width, pRectangle.height) {
            @Override
            public void tick(Pong pPong) {

            }
        };
    }

    public static <T extends DynamicShape> void tickAll(ArrayList<T> pShapes) {
        for (T shape : pShapes) {
            shape.tick();
        }
    }
}
