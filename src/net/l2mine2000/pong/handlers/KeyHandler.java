package net.l2mine2000.pong.handlers;

import net.l2mine2000.pong.Pong;
import net.l2mine2000.pong.shapes.Player;
import net.l2mine2000.pong.shapes.buttons.MenuButton;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        Pong pong = Pong.getInstance();

        if (pong.isMenuOpen()) {
            if (nextButton(e.getKeyCode())) {
                boolean firstSelect = true;
                for (int i = 0; i < pong.buttons.size(); i++) {
                    if (pong.buttons.get(i).isSelected() && i < pong.buttons.size()-1 && pong.buttons.get(i).isVisible(pong.gameState) && pong.buttons.get(i+1).isVisible(pong.gameState)) {
                        pong.buttons.get(i).setSelected(false);
                        pong.buttons.get(i+1).setSelected(true);
                        firstSelect = false;
                        pong.updateCursor(Pong.HIDDEN_CURSOR);
                        break;
                    }
                }
                if (firstSelect) {
                    for (MenuButton button : pong.buttons) {
                        if (button.isVisible(pong.gameState)) {
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

            if (previousButton(e.getKeyCode())) {
                boolean lastSelect = true;
                for (int i = pong.buttons.size()-1; i >= 0; i--) {
                    if (pong.buttons.get(i).isSelected() && i > 0 && pong.buttons.get(i).isVisible(pong.gameState) && pong.buttons.get(i-1).isVisible(pong.gameState)) {
                        pong.buttons.get(i).setSelected(false);
                        pong.buttons.get(i-1).setSelected(true);
                        lastSelect = false;
                        pong.updateCursor(Pong.HIDDEN_CURSOR);
                        break;
                    }
                }
                if (lastSelect) {
                    for (MenuButton button : pong.buttons.reversed()) {
                        if (button.isVisible(pong.gameState)) {
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

            if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
                for (MenuButton button : pong.buttons) {
                    if (button.isSelected()) {
                        pong.updateCursor(Pong.HIDDEN_CURSOR);
                        button.setActiveState(true);
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
            if (player.isUp(e.getKeyCode())) {
                player.up = true;
            }
            if (player.isDown(e.getKeyCode())) {
                player.down = true;
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (!pong.resume()) {
                pong.pause();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
            for (MenuButton button : Pong.getInstance().buttons) {
                if (button.isActive()) {
                    button.setActiveState(false);
                    button.run();
                }
            }
        }
        for (Player player : Pong.getInstance().players) {
            if (player.isUp(e.getKeyCode())) {
                player.up = false;
            }
            if (player.isDown(e.getKeyCode())) {
                player.down = false;
            }
        }
    }

    public static boolean previousButton(int pKeyCode) {
        return pKeyCode == KeyEvent.VK_LEFT || pKeyCode == KeyEvent.VK_UP || pKeyCode == KeyEvent.VK_Z || pKeyCode == KeyEvent.VK_W || pKeyCode == KeyEvent.VK_Q || pKeyCode == KeyEvent.VK_A;
    }

    public static boolean nextButton(int pKeyCode) {
        return pKeyCode == KeyEvent.VK_RIGHT || pKeyCode == KeyEvent.VK_DOWN || pKeyCode == KeyEvent.VK_S || pKeyCode == KeyEvent.VK_D;
    }
}
