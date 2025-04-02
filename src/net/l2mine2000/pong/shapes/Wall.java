package net.l2mine2000.pong.shapes;

import net.l2mine2000.pong.Pong;
import net.l2mine2000.pong.PongGraphics;

import java.awt.*;
import java.sql.Array;
import java.util.Arrays;

public class Wall extends DynamicShape {

    protected Wall(float pX, float pY, int pWidth, int pHeight, Color pColor) {
        super(pX, pY, pWidth, pHeight, pColor, Pong.State.PLAYING, Pong.State.PAUSED);
    }

    protected Wall(float pX, float pY, int pWidth, int pHeight, Color pColor, Pong.State... pAllowedStates) {
        super(pX, pY, pWidth, pHeight, pColor, pAllowedStates);
    }

    @Override
    public void tick(Pong pPong) {

    }



    @Override
    public void draw(PongGraphics pGraphics) {
        super.draw(pGraphics);
        pGraphics.g.fillRect((int) this.getX(), (int) this.getY(), this.getWidth(), this.getHeight());
    }

    public static void create(int pWidth, int pHeight, Pong.State... pAllowedStates) {
        create(pWidth, pHeight, Color.WHITE, pAllowedStates);
    }

    public static void create(int pWidth, int pHeight, Color pColor, Pong.State... pAllowedStates) {
        create(((float) Pong.getInstance().getWidth()) /2 - ((float) pWidth)/2, ((float) Pong.getInstance().getHeight())/2 - ((float) pHeight)/2, pWidth, pHeight, pColor, pAllowedStates);
    }

    public static void create(float pX, float pY, int pWidth, int pHeight, Pong.State... pAllowedStates) {
        create(pX, pY, pWidth, pHeight, Color.WHITE, pAllowedStates);
    }

    public static void create(float pX, float pY, int pWidth, int pHeight, Color pColor, Pong.State... pAllowedStates) {
        if (pAllowedStates.length == 0) {
            Pong.getInstance().walls.add(new Wall(pX, pY, pWidth, pHeight, pColor));
        }else Pong.getInstance().walls.add(new Wall(pX, pY, pWidth, pHeight, pColor, pAllowedStates));
    }
}
