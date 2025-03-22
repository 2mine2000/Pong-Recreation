package pong.shapes.buttons;

import pong.Pong;

import java.awt.*;

public class ResumeButton extends MenuButton {
    private ResumeButton(float pX, float pY, int pWidth, int pHeight, Color pColor, Color pTextColor, Pong.LightSide pLightSide, String pText, Font pFont) {
        super(pX, pY, pWidth, pHeight, pColor, pTextColor, pLightSide, pText, pFont);
    }

    @Override
    public boolean isVisible(int pGameState) {
        return pGameState == Pong.PAUSED;
    }

    @Override
    void run(Pong pPong) {
        pPong.resume();
    }

    public static void create(float pX, float pY, int pWidth, int pHeight, Color pColor, Pong.LightSide pLightSide, String pText, Font pFont) {
        register(new ResumeButton(pX, pY, pWidth, pHeight, pColor, pColor, pLightSide, pText, pFont));
    }

    public static void create(float pX, float pY, int pWidth, int pHeight, Color pColor, Color pTextColor, Pong.LightSide pLightSide, String pText, Font pFont) {
        register(new ResumeButton(pX, pY, pWidth, pHeight, pColor, pTextColor, pLightSide, pText, pFont));
    }

}
