package net.l2mine2000.pong.shapes.buttons;

import net.l2mine2000.pong.Pong;

import java.awt.*;

public class MainMenuButton extends MenuButton {
    protected MainMenuButton(float pX, float pY, int pWidth, int pHeight, Color pColor, Color pTextColor, Pong.LightSide pLightSide, String pText, Font pFont) {
        super(pX, pY, pWidth, pHeight, pColor, pTextColor, pLightSide, pText, pFont);
    }

    @Override
    public boolean isVisible(int pGameState) {
        return pGameState == Pong.PAUSED;
    }

    @Override
    void run(Pong pPong) {
        pPong.mainMenu();
    }

    public static void create(float pX, float pY, int pWidth, int pHeight, Color pColor, Pong.LightSide pLightSide, String pText, Font pFont) {
        register(new MainMenuButton(pX, pY, pWidth, pHeight, pColor, pColor, pLightSide, pText, pFont));
    }

    public static void create(float pX, float pY, int pWidth, int pHeight, Color pColor, Color pTextColor, Pong.LightSide pLightSide, String pText, Font pFont) {
        register(new MainMenuButton(pX, pY, pWidth, pHeight, pColor, pTextColor, pLightSide, pText, pFont));
    }
}
