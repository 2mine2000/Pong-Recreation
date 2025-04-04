package net.l2mine2000.pong;

import net.l2mine2000.pong.shapes.DynamicShape;
import net.l2mine2000.pong.shapes.buttons.MenuButton;

import java.awt.*;
import java.util.ArrayList;

public class PongGraphics {
    public final Graphics2D g;
    private final Color defaultColor;
    private final Font defaultFont;
    private final Stroke defaultStroke;

    private PongGraphics(Graphics2D pGraphics) {
        this.defaultColor = pGraphics.getColor();
        this.defaultFont = new Font(Pong.FONT_NAME, Font.BOLD, pGraphics.getFont().getSize());
        this.defaultStroke = pGraphics.getStroke();
        pGraphics.setFont(this.defaultFont);
        this.g = pGraphics;
    }

    public <T extends DynamicShape> void drawAll(ArrayList<T> pShapes) {
        for (T shape : pShapes) {
            if (shape.isVisible()) {
                shape.draw(this);
            }
        }
    }

    public void drawIfExist(DynamicShape pShape) {
        if (pShape != null) {
            pShape.draw(this);
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

    public static Color getColorBetween(Color pFirst, Color pSecond, float pPosition) {
        int red = (int) (pFirst.getRed() + pPosition * (pSecond.getRed() - pFirst.getRed()));
        int green = (int) (pFirst.getGreen() + pPosition * (pSecond.getGreen() - pFirst.getGreen()));
        int blue = (int) (pFirst.getBlue() + pPosition * (pSecond.getBlue() - pFirst.getBlue()));
        return new Color(red, green, blue);
    }

    public static Color getMeanColor(Color pFirst, Color pSecond) {
        return getColorBetween(pFirst, pSecond, 0.5f);
    }

    public void drawSimpleButton(MenuButton pButton) {
        this.drawSimpleButton(pButton, pButton.isActive());
    }

    public void drawSimpleButton(MenuButton pButton, boolean pActiveRule) {
        this.setColor(new Color(pButton.getColor().getRed(), pButton.getColor().getGreen(), pButton.getColor().getBlue(), pButton.shouldBeBright()?(pActiveRule?45:60):(pActiveRule?20:25)));
        this.g.fill(pButton.getCollisionBox());
        if (pActiveRule) {
            this.drawShadowedRectangle(pButton.getCollisionBox(), pButton.getShadowColor(), pButton.getLightColor(), pButton.getLightSide());
        }else this.drawShadowedRectangle(pButton.getCollisionBox(), pButton.getLightColor(), pButton.getShadowColor(), pButton.getLightSide());
    }

    public void drawShadowedRectangle(Rectangle pRectangle, Color pLight, Pong.DiagonalDirection pLightSide) {
        this.drawShadowedRectangle(pRectangle, pLight, new Color(pLight.getRed()/3*2, pLight.getGreen()/3*2, pLight.getBlue()/3*2), pLightSide);
    }

    public void drawShadowedRectangle(Rectangle pRectangle, Color pLight, Color pShadow, Pong.DiagonalDirection pLightSide) {
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

    public void drawCenteredString(String pString, int pFontSize) {
        this.drawCenteredString(pString, pFontSize, 0, 0);
    }

    public void drawCenteredString(String pString, int pFontSize, Color pColor) {
        this.drawCenteredString(pString, pFontSize, pColor, 0, 0);
    }

    public void drawCenteredString(String pString, int pFontSize, float pOffsetX, float pOffsetY) {
        this.drawCenteredString(pString, pFontSize, this.getColor(), pOffsetX, pOffsetY);
    }

    public void drawCenteredString(String pString, int pFontSize, Color pColor, float pOffsetX, float pOffsetY) {
        Pong pong = Pong.getInstance();
        this.setColor(pColor);
        this.setFont(new Font(this.defaultFont.getFontName(), Font.BOLD, pFontSize));
        FontMetrics metrics = this.g.getFontMetrics();
        this.g.drawString(pString, pong.getWidth()/2f - metrics.stringWidth(pString)/2f + pOffsetX, pong.getHeight()/2f - metrics.getHeight()/2f + pOffsetY);
    }

    protected void drawFading() {
        Pong pong = Pong.getInstance();
        if (pong.fadeInCooldown > 0) {
            if (pong.fadeInCooldown < pong.maxFadeInCooldown) {
                float ratio = ((float) pong.fadeInCooldown) / ((float) pong.maxFadeInCooldown);
                this.setColor(new Color(0, 0, 0, pong.fadeReverse ? 1-ratio : ratio));
            }else this.setColor(new Color(0f, 0f, 0f, pong.fadeReverse ? 0f : 1f));
            this.g.fillRect(0, 0, pong.getWidth(), pong.getHeight());
        }
    }

    protected void drawScores() {
        Pong pong = Pong.getInstance();
        this.setColor(Color.GRAY);
        this.setFont(new Font(this.getFont().getFontName(), Font.BOLD, pong.getHeight()/5));
        FontMetrics metrics = this.g.getFontMetrics();
        this.g.drawString(String.valueOf(pong.players.getLast().getScore()), pong.getWidth()/2 - pong.getWidth()/16 - metrics.stringWidth(String.valueOf(pong.players.getLast().getScore())), (int) (metrics.getHeight()/1.2));
        this.g.drawString(String.valueOf(pong.players.getFirst().getScore()), pong.getWidth()/2 + pong.getWidth()/16, (int) (metrics.getHeight()/1.2));
    }

    protected void drawTitle(float pBaseY, float pPixelCount) {
        Pong pong = Pong.getInstance();
        this.setFont(new Font(this.getFont().getFontName(), Font.BOLD, pong.getHeight()/5));
        FontMetrics metrics = this.g.getFontMetrics();
        this.setColor(Color.DARK_GRAY);
        this.g.drawString("P O N G", pong.getWidth()/2f - metrics.stringWidth("P O N G")/2f, pong.getHeight()/pBaseY - Pong.pixel(pPixelCount));
        this.setColor(Color.GRAY);
        this.g.drawString("P O N G", pong.getWidth()/2f - metrics.stringWidth("P O N G")/2f, pong.getHeight()/pBaseY - Pong.pixel(pPixelCount+10));
        this.setColor(Color.WHITE);
        this.g.drawString("P O N G", pong.getWidth()/2f - metrics.stringWidth("P O N G")/2f, pong.getHeight()/pBaseY - Pong.pixel(pPixelCount+20));
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

    public static Font setFontSize(Font pFont, int pSize) {
        return new Font(pFont.getFontName(), pFont.getStyle(), pSize);
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
