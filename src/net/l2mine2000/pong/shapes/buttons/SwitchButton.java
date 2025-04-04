package net.l2mine2000.pong.shapes.buttons;

import net.l2mine2000.pong.Pong;
import net.l2mine2000.pong.PongGraphics;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.Function;

public class SwitchButton extends MenuButton {
    public static final HashMap<Integer, HashSet<Pong.State>> INDEXES = new HashMap<>();
    private final boolean isOnSwitch;
    private LinkParameters link;
    private boolean isSelectedSwitch = false;

    protected SwitchButton(float pX, float pY, int pWidth, int pHeight, Color pColor, Color pTextColor, Pong.DiagonalDirection pLightSide, boolean pIsOnButton, String pText, Font pFont, Function<Pong.State, Integer> pNextButton, Function<Pong.State, Integer> pPreviousButton, Pong.State... pAllowedStates) {
        super(pX, pY, pWidth, pHeight, pColor, pTextColor, pLightSide, pText, pFont, pNextButton, pPreviousButton, pAllowedStates);
        this.isOnSwitch = pIsOnButton;
    }

    @Override
    public void tick(Pong pPong) {
        super.tick(pPong);
    }

    @Override
    public void draw(PongGraphics pGraphics) {
        if (this.isOnSwitch && this.hasLink() && this.link.oneButtonVisible()) {
            this.link.draw(pGraphics);
        }

        if (this.isVisible(Pong.getInstance().state)) {
            pGraphics.setFont(Objects.requireNonNullElseGet(this.font, () -> new Font(pGraphics.getFont().getFontName(), Font.BOLD, (int) Pong.pixel(20))));
            FontMetrics metrics = pGraphics.g.getFontMetrics();
            pGraphics.setThickness(this.thickness);
            if (this.isSelectedSwitch) {
                pGraphics.setColor(new Color(this.getColor().getRed(), this.getColor().getGreen(), this.getColor().getBlue(), 45));
                pGraphics.g.fill(this.getCollisionBox());
            }
            pGraphics.drawSimpleButton(this, this.isSelectedSwitch);
            pGraphics.setColor(this.textColor);
            pGraphics.g.drawString(this.text, this.getCenterX() - metrics.stringWidth(this.text)/2f, this.getCenterY() + metrics.getHeight()/4f);
            pGraphics.resetStroke();
        }
    }

    @Override
    void run(Pong pPong) {
        if (this.hasLink() && !this.isSelectedSwitch) {
            this.link.toggle();
        }
    }

    @Override
    public void run() {
        if (Pong.getInstance().fadeInCooldown <= 0) {
            this.active = false;
            this.run(Pong.getInstance());
        }
    }

    public boolean isSelectedSwitch() {
        return this.isSelectedSwitch;
    }

    public boolean isOnButton() {
        return this.isOnSwitch;
    }

    public LinkParameters getLink() {
        return this.link;
    }

    public boolean hasLink() {
        return this.link != null;
    }

    private void initLink(LinkParameters pLink) {
        if (!this.hasLink()) {
            this.link = pLink;
        }else throw new RuntimeException("LinkParameters already defined for " + this);
    }

    public static void create(int pWidth, int pHeight, Color pColor, Color pTextColor, Pong.DiagonalDirection pLightSide, boolean pIsOnButton, Font pFont, Pong.State... pAllowedStates) {
        create(0, 0, pWidth, pHeight, pColor, pTextColor, pLightSide, pIsOnButton, pIsOnButton ? "On" : "Off", pFont, pAllowedStates);
    }

    public static void create(float pX, float pY, int pWidth, int pHeight, Color pColor, Color pTextColor, Pong.DiagonalDirection pLightSide, boolean pIsOnButton, Font pFont, Pong.State... pAllowedStates) {
        create(pX, pY, pWidth, pHeight, pColor, pTextColor, pLightSide, pIsOnButton, pIsOnButton ? "On" : "Off", pFont, pAllowedStates);
    }

    public static void create(int pWidth, int pHeight, Color pColor, Color pTextColor, Pong.DiagonalDirection pLightSide, boolean pIsOnButton, String pText, Font pFont, Pong.State... pAllowedStates) {
        create(0, 0, pWidth, pHeight, pColor, pTextColor, pLightSide, pIsOnButton, pText, pFont, pAllowedStates);
    }

    public static void create(float pX, float pY, int pWidth, int pHeight, Color pColor, Color pTextColor, Pong.DiagonalDirection pLightSide, boolean pIsOnButton, String pText, Font pFont, Pong.State... pAllowedStates) {
        register(new SwitchButton(pX, pY, pWidth, pHeight, pColor, pTextColor, pLightSide, pIsOnButton, pText, pFont, pAllowedStates), INDEXES);
    }

    public static void link(SwitchButton pOnButton, SwitchButton pOffButton, boolean pStartOn, String pTitle, Font pTitleFont, float pCenterX, float pCenterY) {
        link(pOnButton, pOffButton, pStartOn, pTitle, Color.WHITE, pTitleFont, pCenterX, pCenterY, LinkParameters.CoordReading.IGNORE);
    }

    public static void link(SwitchButton pOnButton, SwitchButton pOffButton, boolean pStartOn, String pTitle, Color pTitleColor, Font pTitleFont, float pCenterX, float pCenterY) {
        link(pOnButton, pOffButton, pStartOn, pTitle, pTitleColor, pTitleFont, pCenterX, pCenterY, LinkParameters.CoordReading.IGNORE);
    }

    public static void link(SwitchButton pOnButton, SwitchButton pOffButton, boolean pStartOn, String pTitle, Font pTitleFont, float pCenterX, float pCenterY, LinkParameters.CoordReading pCoordReading) {
        link(pOnButton, pOffButton, pStartOn, pTitle, Color.WHITE, pTitleFont, pCenterX, pCenterY, pCoordReading);
    }

    public static void link(SwitchButton pOnButton, SwitchButton pOffButton, boolean pStartOn, String pTitle, Color pTitleColor, Font pTitleFont, float pCenterX, float pCenterY, LinkParameters.CoordReading pCoordReading) {
        LinkParameters link = new LinkParameters(pOnButton, pOffButton, pStartOn, pTitle, pTitleColor, pTitleFont, pCenterX, pCenterY, pCoordReading);
        pOnButton.initLink(link);
        pOffButton.initLink(link);
    }

    public static class LinkParameters {
        private final SwitchButton onButton;
        private final SwitchButton offButton;
        private final String title;
        private final Color titleColor;
        private final Font titleFont;
        private final float x;
        private final float y;
        private final CoordReading coordReading;

        protected LinkParameters(SwitchButton pOnButton, SwitchButton pOffButton, boolean pStartOn, String pTitle, Color pTitleColor, Font pTitleFont, float pX, float pY, CoordReading pCoordReading) {
            if (!pOnButton.isOnSwitch) {
                throw new RuntimeException("link() first parameter must be the \"On\" button");
            }
            if (pOffButton.isOnSwitch) {
                throw new RuntimeException("link() second parameter must be the \"Off\" button");
            }
            this.onButton = pOnButton;
            this.offButton = pOffButton;
            this.title = pTitle;
            this.titleColor = pTitleColor;
            this.titleFont = pTitleFont;
            this.x = pX;
            this.y = pY;
            this.onButton.isSelectedSwitch = pStartOn;
            this.offButton.isSelectedSwitch = !pStartOn;
            this.coordReading = pCoordReading;
            this.updateButtonsPos();
        }

        private void updateButtonsPos() {
            switch (this.coordReading) {
                case IGNORE -> {
                    Pong pong = Pong.getInstance();
                    float offset = titleFont == null ? Pong.pixel(20) : this.titleFont.getSize();
                    this.onButton.relocate(pong.getWidth()/2f + offset/2f,  this.y - this.onButton.getHeight()/2f);
                    this.offButton.relocate(pong.getWidth()/2f + offset*1.5f + this.onButton.getWidth(), this.y - this.offButton.getHeight()/2f);
                }
                case OFFSET -> {
                    Pong pong = Pong.getInstance();
                    float offset = titleFont == null ? Pong.pixel(20) : this.titleFont.getSize();
                    this.onButton.relocate(pong.getWidth()/2f + offset/2f + this.onButton.getX(),  this.y - this.onButton.getHeight()/2f + this.onButton.getY());
                    this.offButton.relocate(pong.getWidth()/2f + offset*1.5f + this.onButton.getWidth() + this.offButton.getX(), this.y - this.offButton.getHeight()/2f + this.offButton.getY());
                }
            }
        }

        public SwitchButton getOnButton() {
            return this.onButton;
        }

        public SwitchButton getOffButton() {
            return this.offButton;
        }

        public boolean oneButtonVisible() {
            return this.onButton.isVisible() || this.offButton.isVisible();
        }

        public boolean oneButtonVisible(Pong.State pState) {
            return this.onButton.isVisible(pState) || this.offButton.isVisible(pState);
        }

        public String getTitle() {
            return this.title;
        }

        public Color getTitleColor() {
            return this.titleColor;
        }

        public Font getTitleFont() {
            return this.titleFont;
        }

        public float getX() {
            return this.x;
        }

        public float getY() {
            return this.y;
        }

        public void toggle() {
            this.onButton.isSelectedSwitch = !this.onButton.isSelectedSwitch;
            this.offButton.isSelectedSwitch = !this.offButton.isSelectedSwitch;
        }

        public void draw(PongGraphics pGraphics) {
            Font font = Objects.requireNonNullElseGet(this.titleFont, () -> new Font(pGraphics.getFont().getFontName(), Font.BOLD, (int) Pong.pixel(20)));
            pGraphics.setFont(font);
            FontMetrics metrics = pGraphics.g.getFontMetrics();
            pGraphics.setColor(this.titleColor);
            if (this.coordReading.equals(CoordReading.IGNORE)) {
                pGraphics.g.drawString(this.title, this.x - metrics.stringWidth(this.title) - font.getSize()/4f, this.y + metrics.getHeight()/4f);
            }else {
                if (this.onButton.isVisible()) {
                    pGraphics.g.drawString(this.title, this.onButton.getX() - metrics.stringWidth(this.title) - font.getSize()/2f, this.onButton.getCenterY() + metrics.getHeight()/4f);
                }
                if (this.offButton.isVisible()) {
                    pGraphics.g.drawString(this.title, this.offButton.getX() - metrics.stringWidth(this.title) - font.getSize()/2f, this.offButton.getCenterY() + metrics.getHeight()/4f);
                }
            }

        }

        public enum CoordReading {
            IGNORE,
            OFFSET,
            OVERRIDE
        }
    }
}
