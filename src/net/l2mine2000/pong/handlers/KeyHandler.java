package net.l2mine2000.pong.handlers;

import net.l2mine2000.pong.Pong;
import net.l2mine2000.pong.Tickable;
import net.l2mine2000.pong.shapes.Player;
import net.l2mine2000.pong.shapes.buttons.MenuButton;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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

        if (pong.isMenuOpen()) {
            if (nextButton(keyCode)) {
                boolean firstSelect = true;
                for (int i = 0; i < pong.buttons.size(); i++) {
                    if (pong.buttons.get(i).isSelected() && i < pong.buttons.size()-1 && pong.buttons.get(i).isVisible() && pong.buttons.get(i+1).isVisible()) {
                        pong.buttons.get(i).setSelected(false);
                        pong.buttons.get(i+1).setSelected(true);
                        firstSelect = false;
                        pong.updateCursor(Pong.HIDDEN_CURSOR);
                        break;
                    }
                }
                if (firstSelect) {
                    for (MenuButton button : pong.buttons) {
                        if (button.isVisible()) {
                            for (MenuButton button1 : pong.buttons) {
                                button1.setSelected(false);
                            }
                            button.setSelected(true);
                            pong.updateCursor(Pong.HIDDEN_CURSOR);
                            break;
                        }
                    }
                }
            }

            if (previousButton(keyCode)) {
                boolean lastSelect = true;
                for (int i = pong.buttons.size()-1; i >= 0; i--) {
                    if (pong.buttons.get(i).isSelected() && i > 0 && pong.buttons.get(i).isVisible() && pong.buttons.get(i-1).isVisible()) {
                        pong.buttons.get(i).setSelected(false);
                        pong.buttons.get(i-1).setSelected(true);
                        lastSelect = false;
                        pong.updateCursor(Pong.HIDDEN_CURSOR);
                        break;
                    }
                }
                if (lastSelect) {
                    for (MenuButton button : pong.buttons.reversed()) {
                        if (button.isVisible()) {
                            for (MenuButton button1 : pong.buttons) {
                                button1.setSelected(false);
                            }
                            button.setSelected(true);
                            pong.updateCursor(Pong.HIDDEN_CURSOR);
                            break;
                        }
                    }
                }
            }

            if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_SPACE) {
                for (MenuButton button : pong.buttons) {
                    if (button.isSelected()) {
                        pong.updateCursor(Pong.HIDDEN_CURSOR);
                        if (pong.fadeInCooldown <= 0) {
                            button.setActiveState(true);
                        }
                    }
                }
            }

            if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_BACK_SPACE) {
                boolean flag = pong.state.isOne(Pong.State.MULTIPLAYER_MENU, Pong.State.SINGLEPLAYER_MENU, Pong.State.SIMULATION_MENU);
                for (MenuButton button : pong.buttons) {
                    if (button.isSelected()) {
                        button.setSelected(false);
                        flag = false;
                    }
                }
                if (flag && pong.fadeInCooldown <= 0) {
                    this.escapingMenu = true;
                    pong.setFadeInCooldown(Pong.TPS/3, true);
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
        if (keyCode == KeyEvent.VK_ESCAPE) {
            if (!pong.resume()) {
                pong.pause();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        this.setGlobalVariables(keyCode, false);

        if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_SPACE) {
            for (MenuButton button : Pong.getInstance().buttons) {
                if (button.isActive()) {
                    button.setActiveState(false);
                    button.run();
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

    @Override
    public void tick(Pong pPong) {
        if (pPong.fadeInCooldown <= 1 && this.escapingMenu) {
            this.escapingMenu = false;
            pPong.mainMenu();
        }
    }
}
