package pong.handlers;

import pong.Pong;
import pong.shapes.buttons.MenuButton;

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

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }


}
