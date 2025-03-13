import java.awt.*;

public class Wall extends DynamicShape {

    protected Wall(float pX, float pY, int pWidth, int pHeight, Color pColor) {
        super(pX, pY, pWidth, pHeight, pColor);
    }

    @Override
    void tick() {

    }



    @Override
    public void draw(Graphics2D pGraphics) {
        super.draw(pGraphics);
        pGraphics.fillRect((int) this.getX(), (int) this.getY(), this.getWidth(), this.getHeight());
    }

    public static void create(int pWidth, int pHeight) {
        create(pWidth, pHeight, Color.WHITE);
    }

    public static void create(int pWidth, int pHeight, Color pColor) {
        create(((float) Pong.getInstance().getWidth()) /2 - ((float) pWidth)/2, ((float) Pong.getInstance().getHeight())/2 - ((float) pHeight)/2, pWidth, pHeight, pColor);
    }

    public static void create(float pX, float pY, int pWidth, int pHeight) {
        create(pX, pY, pWidth, pHeight, Color.WHITE);
    }

    public static void create(float pX, float pY, int pWidth, int pHeight, Color pColor) {
        Pong.getInstance().walls.add(new Wall(pX, pY, pWidth, pHeight, pColor));
    }
}
