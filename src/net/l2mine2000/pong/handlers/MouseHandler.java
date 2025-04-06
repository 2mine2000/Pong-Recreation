package net.l2mine2000.pong.handlers;

import net.l2mine2000.pong.Pong;
import net.l2mine2000.pong.shapes.buttons.DropDownListButton;
import net.l2mine2000.pong.shapes.buttons.MenuButton;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public class MouseHandler implements MouseListener {
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        Pong pong = Pong.getInstance();
        List<MenuButton> buttons = MenuButton.getButtonsUnderMouse();
        if (pong.isMenuOpen()) {
            pong.updateCursor(Pong.DEFAULT_CURSOR);
            MenuButton.deselectAll();
            if (pong.inDropDownList) {
                boolean flag = true;
                for (MenuButton button : buttons) {
                    if (DropDownListButton.goodToGo(button)) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    DropDownListButton.closeAll();
                }
            }


        }
        for (MenuButton button : buttons.reversed()) {
            if (button.isVisible() && pong.fadeInCooldown <= 0 && DropDownListButton.goodToGo(button)) {
                button.setActiveState(true);
                break;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        for (MenuButton button : Pong.getInstance().buttons) {
            if (button.isActive() && button.isVisible() && DropDownListButton.goodToGo(button)) {
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
