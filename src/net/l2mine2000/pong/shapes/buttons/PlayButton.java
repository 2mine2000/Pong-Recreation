package net.l2mine2000.pong.shapes.buttons;

import net.l2mine2000.pong.Pong;

import java.awt.*;

public class PlayButton extends MenuButton{
    protected PlayButton(float pX, float pY, int pWidth, int pHeight, Color pColor, Color pTextColor, Pong.DiagonalDirection pLightSide, String pText, Font pFont) {
        super(pX, pY, pWidth, pHeight, pColor, pTextColor, pLightSide, pText, pFont);
    }

    @Override
    public boolean isVisible(int pGameState) {
        return pGameState == Pong.MAIN_MENU;
    }

    @Override
    void run(Pong pPong) {
        pPong.startNewGame();
    }

    public static void create(float pX, float pY, int pWidth, int pHeight, Color pColor, Pong.DiagonalDirection pLightSide, String pText, Font pFont) {
        register(new PlayButton(pX, pY, pWidth, pHeight, pColor, pColor, pLightSide, pText, pFont));
    }

    public static void create(float pX, float pY, int pWidth, int pHeight, Color pColor, Color pTextColor, Pong.DiagonalDirection pLightSide, String pText, Font pFont) {
        register(new PlayButton(pX, pY, pWidth, pHeight, pColor, pTextColor, pLightSide, pText, pFont));
    }
}
