package net.l2mine2000.pong.shapes.buttons;

import net.l2mine2000.pong.Pong;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Function;

public class StateSelectionButton extends MenuButton {
    public static final HashMap<Integer, HashSet<Pong.State>> INDEXES = new HashMap<>();
    private final Pong.State state;
    private boolean pressed = false;

    protected StateSelectionButton(float pX, float pY, int pWidth, int pHeight, Color pColor, Color pTextColor, Pong.DiagonalDirection pLightSide, Pong.State pState, String pText, Font pFont, Function<Pong.State, Integer> pNextButton, Function<Pong.State, Integer> pPreviousButton, Pong.State... pAllowedStates) {
        super(pX, pY, pWidth, pHeight, pColor, pTextColor, pLightSide, pText, pFont, pNextButton, pPreviousButton, pAllowedStates);
        this.state = pState;
    }

    @Override
    public void tick(Pong pPong) {
        super.tick(pPong);
        if (this.pressed && Pong.getInstance().fadeInCooldown <= 1) {
            this.pressed = false;
            switch (this.state) {
                case MAIN_MENU -> Pong.getInstance().mainMenu();
                case PLAYING -> Pong.getInstance().resume();
                default -> {
                    Pong.getInstance().setState(this.state);
                    Pong.getInstance().setFadeInCooldown(Pong.TPS/3);
                }
            }
        }
    }

    @Override
    void run(Pong pPong) {
        if (!this.pressed) {
            Pong.getInstance().setFadeInCooldown(Pong.TPS/3, true);
            this.pressed = true;
        }
    }

    public Pong.State getState() {
        return this.state;
    }

    public static void create(float pX, float pY, int pWidth, int pHeight, Color pColor, Pong.DiagonalDirection pLightSide, Pong.State pState, String pText, Font pFont, Pong.State... pStates) {
        create(pX, pY, pWidth, pHeight, pColor, pColor, pLightSide, pState, pText, pFont, pStates);
    }

    public static void create(float pX, float pY, int pWidth, int pHeight, Color pColor, Pong.DiagonalDirection pLightSide, Pong.State pState, Font pFont, Pong.State... pStates) {
        create(pX, pY, pWidth, pHeight, pColor, pColor, pLightSide, pState, pState.getName(), pFont, pStates);
    }

    public static void create(float pX, float pY, int pWidth, int pHeight, Color pColor, Color pTextColor, Pong.DiagonalDirection pLightSide, Pong.State pState, Font pFont, Pong.State... pStates) {
        create(pX, pY, pWidth, pHeight, pColor, pTextColor, pLightSide, pState, pState.getName(), pFont, pStates);
    }

    public static void create(float pX, float pY, int pWidth, int pHeight, Pong.DiagonalDirection pLightSide, Pong.State pState, Font pFont, Pong.State... pStates) {
        create(pX, pY, pWidth, pHeight, pState.getMainColor(), pState.getTextColor(), pLightSide, pState, pState.getName(), pFont, pStates);
    }

    public static void create(float pX, float pY, int pWidth, int pHeight, Color pColor, Color pTextColor, Pong.DiagonalDirection pLightSide, Pong.State pState, String pText, Font pFont, Pong.State... pStates) {
        register(new StateSelectionButton(pX, pY, pWidth, pHeight, pColor, pTextColor, pLightSide, pState, pText, pFont, pStates), INDEXES);
    }
}
