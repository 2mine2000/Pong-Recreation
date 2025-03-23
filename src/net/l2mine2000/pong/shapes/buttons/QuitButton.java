package net.l2mine2000.pong.shapes.buttons;

import net.l2mine2000.pong.Pong;

import java.awt.*;

public class QuitButton extends MenuButton {
    protected QuitButton(float pX, float pY, int pWidth, int pHeight, Color pColor, Color pTextColor, Pong.LightSide pLightSide, String pText, Font pFont) {
        super(pX, pY, pWidth, pHeight, pColor, pTextColor, pLightSide, pText, pFont);
    }

    @Override
    public boolean isVisible(int pGameState) {
        return pGameState == Pong.MAIN_MENU;
    }

    @Override
    void run(Pong pPong) {
        pPong.quit();
    }

    public static void create(float pX, float pY, int pWidth, int pHeight, Color pColor, Pong.LightSide pLightSide, String pText, Font pFont) {
        register(new QuitButton(pX, pY, pWidth, pHeight, pColor, pColor, pLightSide, pText, pFont));
    }

    public static void create(float pX, float pY, int pWidth, int pHeight, Color pColor, Color pTextColor, Pong.LightSide pLightSide, String pText, Font pFont) {
        register(new QuitButton(pX, pY, pWidth, pHeight, pColor, pTextColor, pLightSide, pText, pFont));
    }
}
