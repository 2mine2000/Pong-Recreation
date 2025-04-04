package net.l2mine2000.pong.shapes.buttons;

import net.l2mine2000.pong.Pong;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Function;

public class QuitButton extends MenuButton {
    public static final HashMap<Integer, HashSet<Pong.State>> INDEXES = new HashMap<>();

    protected QuitButton(float pX, float pY, int pWidth, int pHeight, Color pColor, Color pTextColor, Pong.DiagonalDirection pLightSide, String pText, Font pFont, Function<Boolean, Integer> pPreviousButton, Function<Boolean, Integer> pNextButton, Pong.State... pAllowedStates) {
        super(pX, pY, pWidth, pHeight, pColor, pTextColor, pLightSide, pText, pFont, pPreviousButton, pNextButton, pAllowedStates);
    }

    @Override
    void run(Pong pPong) {
        pPong.quit(true);
    }

    public static void create(float pX, float pY, int pWidth, int pHeight, Color pColor, Pong.DiagonalDirection pLightSide, String pText, Font pFont, Function<Boolean, Integer> pPreviousButton, Function<Boolean, Integer> pNextButton, Pong.State... pAllowedStates) {
        register(new QuitButton(pX, pY, pWidth, pHeight, pColor, pColor, pLightSide, pText, pFont, pPreviousButton, pNextButton, pAllowedStates), INDEXES);
    }

    public static void create(float pX, float pY, int pWidth, int pHeight, Color pColor, Color pTextColor, Pong.DiagonalDirection pLightSide, String pText, Font pFont, Function<Boolean, Integer> pPreviousButton, Function<Boolean, Integer> pNextButton, Pong.State... pAllowedStates) {
        register(new QuitButton(pX, pY, pWidth, pHeight, pColor, pTextColor, pLightSide, pText, pFont, pPreviousButton, pNextButton, pAllowedStates), INDEXES);
    }
}
