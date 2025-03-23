package pong.handlers;

import pong.Pong;
import pong.shapes.buttons.MenuButton;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class MouseMotionHandler implements MouseMotionListener {
    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Pong pong = Pong.getInstance();
        if (pong.isMenuOpen()) {
            pong.updateCursor(Pong.DEFAULT_CURSOR);
            for (MenuButton button : pong.buttons) {
                if (button.isSelected()) {
                    button.setSelected(false);
                }
            }
        }
    }
}
