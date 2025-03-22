package pong;

import pong.shapes.buttons.MenuButton;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseHandler implements MouseListener {
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (Pong.getInstance().gameState != Pong.MAIN_MENU || Pong.getInstance().fadeInCooldown <= 0) {
            for (MenuButton button : Pong.getInstance().buttons) {
                if (button.isVisible(Pong.getInstance().gameState)) {
                    button.setActiveState(button.isMouseOver());
                }
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
