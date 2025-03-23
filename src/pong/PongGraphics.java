package pong;

import pong.shapes.DynamicShape;
import pong.shapes.buttons.MenuButton;

import java.awt.*;
import java.util.ArrayList;

public class PongGraphics {
    public final Graphics2D g;
    private final Color defaultColor;
    private final Font defaultFont;
    private final Stroke defaultStroke;

    private PongGraphics(Graphics2D pGraphics) {
        this.defaultColor = pGraphics.getColor();
        this.defaultFont = pGraphics.getFont();
        this.defaultStroke = pGraphics.getStroke();
        this.g = pGraphics;
    }

    public <T extends DynamicShape> void drawAll(ArrayList<T> pShapes) {
        for (T shape : pShapes) {
            shape.draw(this);
        }
    }

    public void drawGradient(float pX, float pY, int pWidth, int pHeight, Color pStartColor, Color pEndColor) {
        this.drawGradient(pX, pY, pWidth, pHeight, pStartColor, pEndColor, false);
    }

    public void drawGradient(float pX, float pY, int pWidth, int pHeight, Color pStartColor, Color pEndColor, boolean pIsVertical) {
        if (pIsVertical) {
            for (int i = 0; i <= pHeight; i++) {
                this.g.setColor(getColorBetween(pStartColor, pEndColor, (float) i / pHeight));
                this.g.fillRect((int) pX, i+pHeight, pWidth, 1);
            }
        }else {
            for (int i = 0; i <= pWidth; i++) {
                this.g.setColor(getColorBetween(pStartColor, pEndColor, (float) i / pWidth));
                this.g.fillRect((int) (i + pX), (int) pY, 1, pHeight);
            }
        }
    }

    public Color getColorBetween(Color pFirst, Color pSecond, float pPosition) {
        int red = (int) (pFirst.getRed() + pPosition * (pSecond.getRed() - pFirst.getRed()));
        int green = (int) (pFirst.getGreen() + pPosition * (pSecond.getGreen() - pFirst.getGreen()));
        int blue = (int) (pFirst.getBlue() + pPosition * (pSecond.getBlue() - pFirst.getBlue()));
        return new Color(red, green, blue);
    }

    public void drawButton(MenuButton pButton) {
        /*if (Pong.getInstance().gameState == Pong.MAIN_MENU && Pong.getInstance().fadeInCooldown > 0) {
            this.setColor(new Color(pButton.getColor().getRed(), pButton.getColor().getGreen(), pButton.getColor().getBlue(), pButton.isActive()?20:25));
        }else */this.setColor(new Color(pButton.getColor().getRed(), pButton.getColor().getGreen(), pButton.getColor().getBlue(), pButton.shouldBeBright()?(pButton.isActive()?45:60):(pButton.isActive()?20:25)));
        this.g.fill(pButton.getCollisionBox());
        if (pButton.isActive()) {
            this.drawShadowedRectangle(pButton.getCollisionBox(), pButton.getShadowColor(), pButton.getLightColor(), pButton.getLightSide());
        }else this.drawShadowedRectangle(pButton.getCollisionBox(), pButton.getLightColor(), pButton.getShadowColor(), pButton.getLightSide());
    }

    public void drawShadowedRectangle(Rectangle pRectangle, Color pLight, Pong.LightSide pLightSide) {
        this.drawShadowedRectangle(pRectangle, pLight, new Color(pLight.getRed()/3*2, pLight.getGreen()/3*2, pLight.getBlue()/3*2), pLightSide);
    }

    public void drawShadowedRectangle(Rectangle pRectangle, Color pLight, Color pShadow, Pong.LightSide pLightSide) {
        switch (pLightSide) {
            case TOP_LEFT -> {
                this.g.setColor(pShadow);
                //Right
                this.g.drawLine(pRectangle.x + pRectangle.width, pRectangle.y, pRectangle.x + pRectangle.width, pRectangle.y + pRectangle.height);
                //Bottom
                this.g.drawLine(pRectangle.x, pRectangle.y + pRectangle.height, pRectangle.x + pRectangle.width, pRectangle.y + pRectangle.height);
                this.g.setColor(pLight);
                //Top
                this.g.drawLine(pRectangle.x, pRectangle.y, pRectangle.x + pRectangle.width, pRectangle.y);
                //Left
                this.g.drawLine(pRectangle.x, pRectangle.y, pRectangle.x, pRectangle.y + pRectangle.height);
            }
            case TOP_RIGHT -> {
                this.g.setColor(pShadow);
                //Left
                this.g.drawLine(pRectangle.x, pRectangle.y, pRectangle.x, pRectangle.y + pRectangle.height);
                //Bottom
                this.g.drawLine(pRectangle.x, pRectangle.y + pRectangle.height, pRectangle.x + pRectangle.width, pRectangle.y + pRectangle.height);
                this.g.setColor(pLight);
                //Top
                this.g.drawLine(pRectangle.x, pRectangle.y, pRectangle.x + pRectangle.width, pRectangle.y);
                //Right
                this.g.drawLine(pRectangle.x + pRectangle.width, pRectangle.y, pRectangle.x + pRectangle.width, pRectangle.y + pRectangle.height);
            }
            case BOTTOM_LEFT -> {
                this.g.setColor(pShadow);
                //Top
                this.g.drawLine(pRectangle.x, pRectangle.y, pRectangle.x + pRectangle.width, pRectangle.y);
                //Right
                this.g.drawLine(pRectangle.x + pRectangle.width, pRectangle.y, pRectangle.x + pRectangle.width, pRectangle.y + pRectangle.height);
                this.g.setColor(pLight);
                //Bottom
                this.g.drawLine(pRectangle.x, pRectangle.y + pRectangle.height, pRectangle.x + pRectangle.width, pRectangle.y + pRectangle.height);
                //Left
                this.g.drawLine(pRectangle.x, pRectangle.y, pRectangle.x, pRectangle.y + pRectangle.height);
            }
            case BOTTOM_RIGHT -> {
                this.g.setColor(pShadow);
                //Top
                this.g.drawLine(pRectangle.x, pRectangle.y, pRectangle.x + pRectangle.width, pRectangle.y);
                //Left
                this.g.drawLine(pRectangle.x, pRectangle.y, pRectangle.x, pRectangle.y + pRectangle.height);
                this.g.setColor(pLight);
                //Right
                this.g.drawLine(pRectangle.x + pRectangle.width, pRectangle.y, pRectangle.x + pRectangle.width, pRectangle.y + pRectangle.height);
                //Bottom
                this.g.drawLine(pRectangle.x, pRectangle.y + pRectangle.height, pRectangle.x + pRectangle.width, pRectangle.y + pRectangle.height);
            }
        }
    }

    public void dispose() {
        this.g.dispose();
    }

    public void setColor(Color pColor) {
        this.g.setColor(pColor);
    }

    public void setFont(Font pFont) {
        this.g.setFont(pFont);
    }

    public void setFontSize(int pSize) {
        this.setFont(new Font(this.getFont().getFontName(), this.getFont().getStyle(), pSize));
    }

    public void setThickness(float pThickness) {
        this.setStroke(new BasicStroke(pThickness));
    }

    public void setStroke(Stroke pStroke) {
        this.g.setStroke(pStroke);
    }

    public Color getColor() {
        return this.g.getColor();
    }

    public Font getFont() {
        return this.g.getFont();
    }

    public Stroke getStroke() {
        return this.g.getStroke();
    }

    public void resetColor() {
        this.setColor(this.defaultColor);
    }

    public void resetFont() {
        this.setFont(this.defaultFont);
    }

    public void resetFontSize() {
        this.setFontSize(this.defaultFont.getSize());
    }

    public void resetStroke() {
        this.setStroke(this.defaultStroke);
    }

    public void reset() {
        this.resetColor();
        this.resetFont();
        this.resetStroke();
    }
    
    public static PongGraphics create(Graphics2D pBase) {
        return new PongGraphics(pBase);
    }

    public static PongGraphics create(Graphics pBase) {
        return create((Graphics2D) pBase);
    }
}
