package pong.shapes.buttons;

import pong.Pong;
import pong.PongGraphics;
import pong.shapes.DynamicShape;

import java.awt.*;
import java.util.Objects;

public abstract class MenuButton extends DynamicShape {
    protected final Color textColor;
    protected final Color lightColor;
    protected final Color shadowColor;
    protected final Pong.LightSide lightSide;
    protected final float thickness = Pong.pixel(5);
    protected boolean active = false;
    protected final String text;
    protected final Font font;

    protected MenuButton(float pX, float pY, int pWidth, int pHeight, Color pColor, Color pTextColor, Pong.LightSide pLightSide, String pText, Font pFont) {
        super(pX, pY, pWidth, pHeight, pColor);
        this.textColor = pTextColor;
        this.lightColor = new Color(Math.max(0, Math.min(pColor.getRed()/3*4, 255)), Math.max(0, Math.min(pColor.getGreen()/3*4, 255)), Math.max(0, Math.min(pColor.getBlue()/3*4, 255)));
        this.shadowColor = new Color(Math.max(0, Math.min(pColor.getRed()/3*2, 255)), Math.max(0, Math.min(pColor.getGreen()/3*2, 255)), Math.max(0, Math.min(pColor.getBlue()/3*2, 255)));
        this.lightSide = pLightSide;
        this.text = pText;
        this.font = pFont;
    }

    @Override
    public void tick() {
        if (this.active && !isMouseOver()) {
            this.active = false;
        }
    }

    public abstract boolean isVisible(int pGameState);

    @Override
    public void draw(PongGraphics pGraphics) {
        if (this.isVisible(Pong.getInstance().gameState)) {
            pGraphics.setFont(Objects.requireNonNullElseGet(this.font, () -> new Font(pGraphics.getFont().getFontName(), Font.BOLD, (int) Pong.pixel(20))));
            FontMetrics metrics = pGraphics.g.getFontMetrics();
            pGraphics.setThickness(this.thickness);
            pGraphics.drawButton(this);
            pGraphics.setColor(this.textColor);
            pGraphics.g.drawString(this.text, this.getCenterX() - metrics.stringWidth(this.text)/2f, this.getCenterY() + metrics.getHeight()/4f);
            pGraphics.resetStroke();
        }
    }

    abstract void run(Pong pPong);

    public void run() {
        this.run(Pong.getInstance());
    }

    public Color getTextColor() {
        return this.textColor;
    }

    public Color getLightColor() {
        return this.lightColor;
    }

    public Color getShadowColor() {
        return this.shadowColor;
    }

    public Pong.LightSide getLightSide() {
        return this.lightSide;
    }

    public boolean isActive() {
        return this.active;
    }

    public float getThickness() {
        return this.thickness;
    }

    public boolean isMouseOver() {
        Point point = Pong.getInstance().getMousePosition();
        if (point != null) {
            float offset = this.thickness / 2f;
            return point.x >= this.getX() - offset && point.x < this.getEndX() + offset && point.y >= this.getY() - offset && point.y < this.getEndY() + offset;
        }
        return false;
    }

    public void setActiveState(boolean pActive) {
        this.active = pActive;
    }

    public static <T extends MenuButton> void register(T pButton) {
        Pong.getInstance().buttons.add(pButton);
    }
}
