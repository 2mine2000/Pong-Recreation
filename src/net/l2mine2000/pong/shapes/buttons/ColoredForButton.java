package net.l2mine2000.pong.shapes.buttons;

import java.awt.*;
import java.util.function.Predicate;

public interface ColoredForButton {
    boolean useColor();

    Color getMainColor();

    Color getTextColor();
}
