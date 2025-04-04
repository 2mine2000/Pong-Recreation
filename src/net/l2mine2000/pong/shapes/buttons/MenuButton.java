package net.l2mine2000.pong.shapes.buttons;

import net.l2mine2000.pong.Pong;
import net.l2mine2000.pong.PongGraphics;
import net.l2mine2000.pong.shapes.DynamicShape;

import java.awt.*;
import java.util.*;
import java.util.function.Function;

public abstract class MenuButton extends DynamicShape {
    public static final Color MAIN_RED = Color.RED;
    public static final Color TEXT_RED = new Color(255, 200, 200);
    public static final Color MAIN_GREEN = new Color(25, 255, 25);
    public static final Color TEXT_GREEN = new Color(200, 255, 200);
    public static final Color MAIN_BLUE = new Color(75, 75, 255);
    public static final Color TEXT_BLUE = new Color(220, 220, 255);
    public static final Color MAIN_YELLOW = Color.YELLOW;
    public static final Color TEXT_YELLOW = new Color(255, 255, 200);
    public static final float DEFAULT_THICKNESS = Pong.pixel(5);
    private int indexInList = -1;
    protected final Color textColor;
    protected final Color lightColor;
    protected final Color shadowColor;
    protected final Pong.DiagonalDirection lightSide;
    protected final float thickness = DEFAULT_THICKNESS;
    protected boolean active = false;
    protected final String text;
    protected final Font font;
    protected boolean selected = false;
    private final Function<Boolean, Integer> nextInMenu;
    private final Function<Boolean, Integer> previousInMenu;

    protected MenuButton(float pX, float pY, int pWidth, int pHeight, Color pColor, Color pTextColor, Pong.DiagonalDirection pLightSide, String pText, Font pFont, Function<Boolean, Integer> pNextButton, Function<Boolean, Integer> pPreviousButton, Pong.State... pAllowedStates) {
        super(pX, pY, pWidth, pHeight, pColor, pAllowedStates);
        this.textColor = pTextColor;
        this.lightColor = getLightEquivalent(pColor);
        this.shadowColor = getShadowEquivalent(pColor);
        this.lightSide = pLightSide;
        this.text = pText;
        this.font = pFont;
        this.nextInMenu = pNextButton;
        this.previousInMenu = pPreviousButton;
    }

    @Override
    public void tick(Pong pPong) {
        if (this.active && !this.shouldBeBright()) {
            this.active = false;
        }
    }

    @Override
    public void draw(PongGraphics pGraphics) {
        if (this.isVisible(Pong.getInstance().state)) {
            pGraphics.setFont(Objects.requireNonNullElseGet(this.font, () -> new Font(pGraphics.getFont().getFontName(), Font.BOLD, (int) Pong.pixel(20))));
            FontMetrics metrics = pGraphics.g.getFontMetrics();
            pGraphics.setThickness(this.thickness);
            pGraphics.drawSimpleButton(this);
            pGraphics.resetStroke();
            pGraphics.setColor(this.textColor);
            pGraphics.g.drawString(this.text, this.getCenterX() - metrics.stringWidth(this.text)/2f, this.getCenterY() + metrics.getHeight()/4f);
        }
    }

    abstract void run(Pong pPong);

    public void run() {
        if (Pong.getInstance().fadeInCooldown <= 0) {
            this.selected = false;
            this.active = false;
            this.run(Pong.getInstance());
        }
    }

    public boolean checkIfAbove() {
        return this.isMouseOver();
    }

    public String getText() {
        return this.text;
    }

    public Color getTextColor() {
        return this.textColor;
    }

    public Color getLightColor() {
        return this.lightColor;
    }

    public Color getShadowColor() {
        return this.shadowColor;
    }

    public Pong.DiagonalDirection getLightSide() {
        return this.lightSide;
    }

    public boolean isActive() {
        return this.active;
    }

    public float getThickness() {
        return this.thickness;
    }

    public boolean isMouseOver() {
        Point point = Pong.getInstance().getMousePosition();
        if (point != null) {
            float offset = this.thickness / 2f;
            return point.x >= this.getX() - offset && point.x < this.getEndX() + offset && point.y >= this.getY() - offset && point.y < this.getEndY() + offset;
        }
        return false;
    }

    public static boolean isMouseOver(Rectangle pZone) {
        return isMouseOver(pZone, DEFAULT_THICKNESS);
    }

    public static boolean isMouseOver(Rectangle pZone, float pBorderThickness) {
        Point point = Pong.getInstance().getMousePosition();
        if (point != null) {
            float offset = pBorderThickness / 2f;
            DynamicShape dynamicZone = DynamicShape.of(pZone);
            return point.x >= dynamicZone.getX() - offset && point.x < dynamicZone.getEndX() + offset && point.y >= dynamicZone.getY() - offset && point.y < dynamicZone.getEndY() + offset;
        }
        return false;
    }

    public void setActiveState(boolean pActive) {
        this.active = pActive;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean pSelected) {
        this.selected = pSelected;
    }

    public boolean shouldBeBright() {
        return this.selected || (this.isMouseOver() && Pong.getInstance().getCursor() != Pong.HIDDEN_CURSOR);
    }

    public int getIndex() {
        return this.indexInList;
    }

    protected void initIndex(int pIndex) {
        if (this.indexInList < 0) {
            this.indexInList = pIndex;
        }
    }

    public void moveToNextButtonInMenu(boolean pVertical) {
        MenuButton button = Pong.getInstance().buttons.get(this.nextInMenu.apply(pVertical));
        this.selected = false;
        button.selected = true;
        Pong.getInstance().updateCursor(Pong.HIDDEN_CURSOR);
    }

    public void moveToPreviousButtonInMenu(boolean pVertical) {
        MenuButton button = Pong.getInstance().buttons.get(this.previousInMenu.apply(pVertical));;
        this.selected = false;
        button.selected = true;
        Pong.getInstance().updateCursor(Pong.HIDDEN_CURSOR);
    }

    public static Color getLightEquivalent(Color pColor) {
        return new Color(Math.max(0, Math.min(pColor.getRed()/3*4, 255)), Math.max(0, Math.min(pColor.getGreen()/3*4, 255)), Math.max(0, Math.min(pColor.getBlue()/3*4, 255)));
    }

    public static Color getShadowEquivalent(Color pColor) {
        return new Color(Math.max(0, Math.min(pColor.getRed()/3*2, 255)), Math.max(0, Math.min(pColor.getGreen()/3*2, 255)), Math.max(0, Math.min(pColor.getBlue()/3*2, 255)));
    }

    public static <T extends MenuButton> int register(T pButton, HashMap<Integer, HashSet<Pong.State>> pTypeIndexes) {
        int index = Pong.getInstance().buttons.size();
        pButton.initIndex(index);
        pTypeIndexes.put(index, pButton.allowedStates);
        Pong.getInstance().buttons.add(pButton);
        return index;
    }
}
