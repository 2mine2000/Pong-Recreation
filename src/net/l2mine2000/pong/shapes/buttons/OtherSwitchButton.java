package net.l2mine2000.pong.shapes.buttons;

import net.l2mine2000.pong.Pong;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;

public class OtherSwitchButton extends MenuButton {
    public static final HashMap<Integer, HashSet<Pong.State>> INDEXES = new HashMap<>();
    private final boolean isOnSwitch;
    private int brosIndex = -1;

    protected OtherSwitchButton(float pX, float pY, int pWidth, int pHeight, Color pColor, Color pTextColor, Pong.DiagonalDirection pLightSide, boolean pIsOnButton, String pText, Font pFont, Pong.State... pAllowedStates) {
        super(pX, pY, pWidth, pHeight, pColor, pTextColor, pLightSide, pText, pFont, pAllowedStates);
        this.isOnSwitch = pIsOnButton;
    }

    @Override
    void run(Pong pPong) {

    }

    public boolean isOnButton() {
        return this.isOnSwitch;
    }

    public int getBrosIndex() {
        return this.brosIndex;
    }

    public boolean hasBro() {
        return this.brosIndex >= 0;
    }

    private void initBro(int pBro) {
        if (this.brosIndex < 0) {
            this.brosIndex = pBro;
        }
    }

    public static void create(float pX, float pY, int pWidth, int pHeight, Color pColor, Color pTextColor, Pong.DiagonalDirection pLightSide, boolean pIsOnButton, int pBroIndex, String pText, Font pFont, Pong.State... pAllowedStates) {
        OtherSwitchButton button = new OtherSwitchButton(pX, pY, pWidth, pHeight, pColor, pTextColor, pLightSide, pIsOnButton, pText, pFont, pAllowedStates);
        int index = register(button, INDEXES);

        String exception = "Somehow, the switch button " + button + " didn't got his bro";
        try {
            OtherSwitchButton bro = (OtherSwitchButton) Pong.getInstance().buttons.get(pBroIndex);
            if (bro.hasBro()) {
                if (bro.brosIndex == index) {
                    if (bro.isOnSwitch != pIsOnButton) {
                        button.initBro(pBroIndex);
                    }else exception = "Specified Bro is the same type of " + button + "(" + pIsOnButton + ")";
                } else exception = "Specified Bro already has his bro";
            }else {
                bro.initBro(index);
                button.initBro(pBroIndex);
            }
        } catch (RuntimeException e) {
            button.initBro(pBroIndex);
        }

        if (!button.hasBro()) {
            throw new RuntimeException(exception);
        }
    }
}
