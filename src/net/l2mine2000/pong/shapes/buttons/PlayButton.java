package net.l2mine2000.pong.shapes.buttons;

import net.l2mine2000.pong.Pong;
import net.l2mine2000.pong.shapes.Player;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Function;

import static net.l2mine2000.pong.Pong.State.SIMULATION_MENU;
import static net.l2mine2000.pong.Pong.State.SINGLEPLAYER_MENU;

public class PlayButton extends MenuButton{
    public static final HashMap<Integer, HashSet<Pong.State>> INDEXES = new HashMap<>();
    private boolean pressed = false;

    protected PlayButton(float pX, float pY, int pWidth, int pHeight, Color pColor, Color pTextColor, Pong.DiagonalDirection pLightSide, String pText, Font pFont, Function<Boolean, Integer> pPreviousButton, Function<Boolean, Integer> pNextButton, Pong.State... pAllowedStates) {
        super(pX, pY, pWidth, pHeight, pColor, pTextColor, pLightSide, pText, pFont, pPreviousButton, pNextButton, pAllowedStates);
    }

    @Override
    public void tick(Pong pPong) {
        super.tick(pPong);
        if (this.pressed && Pong.getInstance().fadeInCooldown <= 1) {
            this.pressed = false;
            switch (pPong.state) {
                case SINGLEPLAYER_MENU -> pPong.setOrCreatePlayers(Player.AIDifficulty.BABY, !((SwitchButton) pPong.buttons.get(8)).isSelectedSwitch());
                case SIMULATION_MENU -> pPong.setOrCreatePlayers(Player.AIDifficulty.NORMAL, Player.AIDifficulty.NORMAL);
                default -> pPong.setOrCreatePlayers();
            }
            Pong.getInstance().startNewGame();
        }
    }

    @Override
    void run(Pong pPong) {
        if (!this.pressed) {
            pPong.setFadeInCooldown((int) (Pong.TPS*0.5f), true);
            this.pressed = true;
        }
    }

    public static void create(float pX, float pY, int pWidth, int pHeight, Color pColor, Pong.DiagonalDirection pLightSide, String pText, Font pFont, Function<Boolean, Integer> pPreviousButton, Function<Boolean, Integer> pNextButton, Pong.State... pAllowedStates) {
        register(new PlayButton(pX, pY, pWidth, pHeight, pColor, pColor, pLightSide, pText, pFont, pPreviousButton, pNextButton, pAllowedStates), INDEXES);
    }

    public static void create(float pX, float pY, int pWidth, int pHeight, Color pColor, Color pTextColor, Pong.DiagonalDirection pLightSide, String pText, Font pFont, Function<Boolean, Integer> pPreviousButton, Function<Boolean, Integer> pNextButton, Pong.State... pAllowedStates) {
        register(new PlayButton(pX, pY, pWidth, pHeight, pColor, pTextColor, pLightSide, pText, pFont, pPreviousButton, pNextButton, pAllowedStates), INDEXES);
    }
}
