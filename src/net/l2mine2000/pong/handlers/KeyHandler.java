package net.l2mine2000.pong.handlers;

import net.l2mine2000.pong.Pong;
import net.l2mine2000.pong.Tickable;
import net.l2mine2000.pong.shapes.Player;
import net.l2mine2000.pong.shapes.buttons.DropDownListButton;
import net.l2mine2000.pong.shapes.buttons.MenuButton;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.Objects;

public class KeyHandler implements KeyListener, Tickable {
    private boolean escapingMenu = false;
    private boolean shift = false;
    private boolean control = false;
    private boolean alt = false;

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        Pong pong = Pong.getInstance();
        int keyCode = e.getKeyCode();
        this.setGlobalVariables(keyCode, true);

        boolean pauseMenuFlag = true;

        if (pong.isMenuOpen()) {
            if (nextButton(keyCode)) {
                if (!MenuButton.oneSelected()) {
                    MenuButton button;
                    try {
                        button = MenuButton.getButtonsUnderMouse().getFirst();
                    }catch (Exception ex) {
                        button = pong.state.getFirstButton();
                    }
                    if (button.isVisible()) {
                        pong.setCursor(Pong.HIDDEN_CURSOR);
                        MenuButton.deselectAll();
                        button.setSelected(true);
                    }
                }else {
                    for (MenuButton button : pong.buttons) {
                        if (button.isSelected()) {
                            button.moveToNextButtonInMenu(verticalButton(keyCode));
                            break;
                        }
                    }

                }
            }
            if (previousButton(keyCode)) {
                if (!MenuButton.oneSelected()) {
                    MenuButton button;
                    try {
                        button = MenuButton.getButtonsUnderMouse().getLast();
                    }catch (Exception ex) {
                        button = pong.state.getLastButton();
                    }
                    if (button.isVisible()) {
                        pong.setCursor(Pong.HIDDEN_CURSOR);
                        MenuButton.deselectAll();
                        button.setSelected(true);
                    }
                }else {
                    for (MenuButton button : pong.buttons) {
                        if (button.isSelected()) {
                            button.moveToPreviousButtonInMenu(verticalButton(keyCode));
                            break;
                        }
                    }

                }
            }

            if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_SPACE) {
                if (pong.fadeInCooldown <= 1) {
                    for (MenuButton button : pong.buttons) {
                        if (button.isSelected() && button.isVisible() && DropDownListButton.goodToGo(button)) {
                            pong.updateCursor(Pong.HIDDEN_CURSOR);
                            button.setActiveState(true);
                        }
                    }

                }
            }

            if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_BACK_SPACE) {
                boolean flag = pong.state.isOne(Pong.State.MULTIPLAYER_MENU, Pong.State.SINGLEPLAYER_MENU, Pong.State.SIMULATION_MENU, Pong.State.PAUSED);
                if (MenuButton.oneSelected()) {
                    MenuButton.deselectAll();
                    flag = false;
                }
                if (flag && pong.fadeInCooldown <= 0) {
                    if (pong.state.is(Pong.State.PAUSED)) {
                        pauseMenuFlag = !pong.resume();
                    }else {
                        this.escapingMenu = true;
                        pong.setFadeInCooldown(Pong.TPS/3, true);
                    }
                }
            }
        }else {
            for (MenuButton button : pong.buttons) {
                if (button.isSelected()) {
                    button.setSelected(false);
                }
            }
        }

        for (Player player : pong.players) {
            if (player.isUp(keyCode)) {
                player.up = true;
            }
            if (player.isDown(keyCode)) {
                player.down = true;
            }
        }

        if (pauseMenuFlag && pong.state.is(Pong.State.PLAYING) && (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_BACK_SPACE)) {
            pong.pause();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        this.setGlobalVariables(keyCode, false);

        if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_SPACE) {
            for (MenuButton button : Pong.getInstance().buttons) {
                if (button.isActive() && button.isVisible() && DropDownListButton.goodToGo(button)) {
                    button.run();
                    if (button instanceof DropDownListButton.SelectionButton<?> selectionButton) {
                        selectionButton.getList().setSelected(true);
                    }
                }
            }
        }
        for (Player player : Pong.getInstance().players) {
            if (player.isUp(keyCode)) {
                player.up = false;
            }
            if (player.isDown(keyCode)) {
                player.down = false;
            }
        }
    }

    public void setGlobalVariables(int pKeyCode, boolean pValue) {
        switch (pKeyCode) {
            case KeyEvent.VK_SHIFT -> this.shift = pValue;
            case KeyEvent.VK_CONTROL -> this.control = pValue;
            case KeyEvent.VK_ALT -> this.alt = pValue;
        }
    }

    public boolean shiftDown() {
        return this.shift;
    }

    public boolean controlDown() {
        return this.control;
    }

    public boolean altDown() {
        return this.alt;
    }

    public static boolean previousButton(int pKeyCode) {
        return pKeyCode == KeyEvent.VK_LEFT || pKeyCode == KeyEvent.VK_UP || pKeyCode == KeyEvent.VK_Z || pKeyCode == KeyEvent.VK_W || pKeyCode == KeyEvent.VK_Q || pKeyCode == KeyEvent.VK_A;
    }

    public static boolean nextButton(int pKeyCode) {
        return pKeyCode == KeyEvent.VK_RIGHT || pKeyCode == KeyEvent.VK_DOWN || pKeyCode == KeyEvent.VK_S || pKeyCode == KeyEvent.VK_D;
    }

    public static boolean verticalButton(int pKeyCode) {
        return pKeyCode == KeyEvent.VK_UP || pKeyCode == KeyEvent.VK_Z || pKeyCode == KeyEvent.VK_W || pKeyCode == KeyEvent.VK_DOWN || pKeyCode == KeyEvent.VK_S;
    }

    @Override
    public void tick(Pong pPong) {
        if (pPong.fadeInCooldown <= 1 && this.escapingMenu) {
            this.escapingMenu = false;
            pPong.mainMenu();
        }
    }
}