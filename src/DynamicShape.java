import java.awt.*;

public abstract class DynamicShape {
    private float x;
    private float y;
    private int width;
    private int height;
    private Color color;

    public DynamicShape(int pWidth, int pHeight) {
        this(pWidth, pHeight, Color.WHITE);
    }

    public DynamicShape(int pWidth, int pHeight, Color pColor) {
        this(((float) Pong.getInstance().getWidth()) /2 - ((float) pWidth)/2, ((float) Pong.getInstance().getHeight())/2 - ((float) pHeight)/2, pWidth, pHeight, pColor);
    }

    public DynamicShape(float pX, float pY, int pWidth, int pHeight) {
        this(pX, pY, pWidth, pHeight, Color.WHITE);
    }

    public DynamicShape(float pX, float pY, int pWidth, int pHeight, Color pColor) {
        this.resize(pWidth, pHeight);
        this.relocate(pX, pY);
        this.color = pColor;
    }

    abstract void tick();

    public void draw(Graphics2D pGraphics) {
        pGraphics.setColor(this.color);
    }

    public void relocate(float pX, float pY) {
        this.x = pX;
        this.y = pY;
    }

    public void move(float pOffsetX, float pOffsetY) {
        this.relocate(this.getX() + pOffsetX, this.getY() + pOffsetY);
    }

    public void resize(int pWidth, int pHeight) {
        this.width = pWidth;
        this.height = pHeight;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public float getEndX() {
        return this.x + this.width;
    }

    public float getEndY() {
        return this.y + this.height;
    }

    public float getCenterX() {
        return this.getX() + (float) this.getWidth() / 2;
    }

    public float getCenterY() {
        return this.getY() + (float) this.getHeight() / 2;
    }

    public void setColor(Color pColor) {
        this.color = pColor;
    }

    public Color getColor() {
        return this.color;
    }

    public boolean intersect(DynamicShape pOther) {
        return this.intersect(pOther.getCollisionBox());
    }

    public boolean intersect(Rectangle pOther) {
        return this.getCollisionBox().intersects(pOther);
    }

    public Rectangle getCollisionBox() {
        return new Rectangle((int) this.getX(), (int) this.getY(), this.getWidth(), this.getHeight());
    }
}
