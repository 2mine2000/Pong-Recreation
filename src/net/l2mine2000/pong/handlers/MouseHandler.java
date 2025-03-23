package net.l2mine2000.pong.handlers;

import net.l2mine2000.pong.Pong;
import net.l2mine2000.pong.shapes.buttons.MenuButton;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseHandler implements MouseListener {
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        Pong pong = Pong.getInstance();
        if (pong.isMenuOpen()) {
            pong.updateCursor(Pong.DEFAULT_CURSOR);
            for (MenuButton button : pong.buttons) {
                if (button.isSelected()) {
                    button.setSelected(false);
                }
            }
        }
        for (MenuButton button : pong.buttons) {
            if (button.isVisible(pong.gameState)) {
                button.setActiveState(button.isMouseOver());
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        for (MenuButton button : Pong.getInstance().buttons) {
            if (button.isActive() && button.isVisible(Pong.getInstance().gameState)) {
                button.setActiveState(false);
                button.run();
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        Pong pong = Pong.getInstance();
    }

    @Override
    public void mouseExited(MouseEvent e) {

    }


}
