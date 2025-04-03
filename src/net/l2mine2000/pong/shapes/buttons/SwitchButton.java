package net.l2mine2000.pong.shapes.buttons;

import net.l2mine2000.pong.Pong;
import net.l2mine2000.pong.PongGraphics;
import net.l2mine2000.pong.shapes.Ball;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;

public class SwitchButton extends MenuButton{
    public static final HashMap<Integer, HashSet<Pong.State>> INDEXES = new HashMap<>();
    private final Rectangle[] collisionShapes = new Rectangle[2];
    private final String[] texts = new String[2];
    private final Color[] colors = new Color[8];
    private boolean on = false;


    protected SwitchButton(float pX, float pY, int pWidth, int pHeight, Color pOnColor, Color pOnTextColor, Color pOffColor, Color pOffTextColor, Pong.DiagonalDirection pLightSide, String pOnText, String pOffText, Font pFont, Pong.State... pAllowedStates) {
        super(pX, pY, pWidth, pHeight, PongGraphics.getMeanColor(pOnColor, pOffColor), PongGraphics.getMeanColor(pOnTextColor, pOffTextColor), pLightSide, String.format("%s/%s", pOnText, pOffText), pFont, pAllowedStates);
        this.texts[0] = pOnText;
        this.texts[1] = pOffText;

        this.colors[0] = pOnTextColor;
        this.colors[1] = pOffTextColor;
        this.colors[2] = getLightEquivalent(pOnColor);
        this.colors[3] = getLightEquivalent(pOffTextColor);
        this.colors[4] = pOnColor;
        this.colors[5] = pOffColor;
        this.colors[6] = getShadowEquivalent(pOnColor);
        this.colors[7] = getShadowEquivalent(pOffColor);

        this.collisionShapes[0] = new Rectangle(0, 0, (int) (pWidth/10f*4.5f), pHeight);
        this.collisionShapes[1] = new Rectangle((int) (pWidth/10f*5.5f), 0, (int) (pWidth/10f*4.5f), pHeight);
    }

    @Override
    void run(Pong pPong) {
        super.run();
        if (!this.on && isMouseOver(this.collisionShapes[0], this.thickness)) {
            this.on = true;
        }else if (this.on && isMouseOver(this.collisionShapes[1], this.thickness)) {
            this.on = false;
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void draw(PongGraphics pGraphics) {
        super.draw(pGraphics);
    }

    public String getText(boolean pOn) {
        return pOn ? this.texts[0] : this.texts[1];
    }

    public Color getTextColor(boolean pOn) {
        return pOn ? this.colors[0] : this.colors[1];
    }

    public Color getLightColor(boolean pOn) {
        return pOn ? this.colors[2] : this.colors[3];
    }

    public Color getColor(boolean pOn) {
        return pOn ? this.colors[4] : this.colors[5];
    }

    public Color getShadowColor(boolean pOn) {
        return pOn ? this.colors[6] : this.colors[7];
    }

    public boolean isOn() {
        return this.on;
    }

    public static void create(float pX, float pY, int pWidth, int pHeight, Color pOnColor, Color pOnTextColor, Color pOffColor, Color pOffTextColor, Pong.DiagonalDirection pLightSide, String pOnText, String pOffText, Font pFont, Pong.State... pStates) {
        //register(new StateSelectionButton(pX, pY, pWidth, pHeight, pOnColor, pOnTextColor, pOffColor, pOffTextColor, pLightSide, pOnText, pOffText, pFont, pStates), INDEXES);
    }
}
