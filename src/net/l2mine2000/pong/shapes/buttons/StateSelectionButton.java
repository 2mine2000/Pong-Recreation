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

    protected StateSelectionButton(float pX, float pY, int pWidth, int pHeight, Color pColor, Color pTextColor, Pong.DiagonalDirection pLightSide, Pong.State pState, String pText, Font pFont, Function<Boolean, Integer> pPreviousButton, Function<Boolean, Integer> pNextButton, Pong.State... pAllowedStates) {
        super(pX, pY, pWidth, pHeight, pColor, pTextColor, pLightSide, pText, pFont, pPreviousButton, pNextButton, pAllowedStates);
        this.state = pState;
    }

    @Override
    public void tick(Pong pPong) {
        super.tick(pPong);
        if (this.pressed && pPong.fadeInCooldown <= 1) {
            this.pressed = false;
            switch (this.state) {
                case MAIN_MENU -> pPong.mainMenu();
                case PLAYING -> pPong.resume();
                default -> {
                    pPong.setState(this.state);
                    pPong.setFadeInCooldown(Pong.TPS/3);
                    deselectAll();
                }
            }
        }
    }

    @Override
    void run(Pong pPong) {
        if (!this.pressed) {
            if (!this.state.is(Pong.State.PLAYING)) {
                pPong.setFadeInCooldown(Pong.TPS/3, true);
            }
            this.pressed = true;
        }
    }

    public Pong.State getState() {
        return this.state;
    }

    public static void create(float pX, float pY, int pWidth, int pHeight, Color pColor, Pong.DiagonalDirection pLightSide, Pong.State pState, String pText, Font pFont, Function<Boolean, Integer> pPreviousButton, Function<Boolean, Integer> pNextButton, Pong.State... pStates) {
        create(pX, pY, pWidth, pHeight, pColor, pColor, pLightSide, pState, pText, pFont, pPreviousButton, pNextButton, pStates);
    }

    public static void create(float pX, float pY, int pWidth, int pHeight, Color pColor, Pong.DiagonalDirection pLightSide, Pong.State pState, Font pFont, Function<Boolean, Integer> pPreviousButton, Function<Boolean, Integer> pNextButton, Pong.State... pStates) {
        create(pX, pY, pWidth, pHeight, pColor, pColor, pLightSide, pState, pState.getName(), pFont, pPreviousButton, pNextButton, pStates);
    }

    public static void create(float pX, float pY, int pWidth, int pHeight, Color pColor, Color pTextColor, Pong.DiagonalDirection pLightSide, Pong.State pState, Font pFont, Function<Boolean, Integer> pPreviousButton, Function<Boolean, Integer> pNextButton, Pong.State... pStates) {
        create(pX, pY, pWidth, pHeight, pColor, pTextColor, pLightSide, pState, pState.getName(), pFont, pPreviousButton, pNextButton, pStates);
    }

    public static void create(float pX, float pY, int pWidth, int pHeight, Pong.DiagonalDirection pLightSide, Pong.State pState, Font pFont, Function<Boolean, Integer> pPreviousButton, Function<Boolean, Integer> pNextButton, Pong.State... pStates) {
        create(pX, pY, pWidth, pHeight, pState.getMainColor(), pState.getTextColor(), pLightSide, pState, pState.getName(), pFont, pPreviousButton, pNextButton, pStates);
    }

    public static void create(float pX, float pY, int pWidth, int pHeight, Color pColor, Color pTextColor, Pong.DiagonalDirection pLightSide, Pong.State pState, String pText, Font pFont, Function<Boolean, Integer> pPreviousButton, Function<Boolean, Integer> pNextButton, Pong.State... pStates) {
        register(new StateSelectionButton(pX, pY, pWidth, pHeight, pColor, pTextColor, pLightSide, pState, pText, pFont, pPreviousButton, pNextButton, pStates), INDEXES);
    }
}
