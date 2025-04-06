package net.l2mine2000.pong.shapes.buttons;

import net.l2mine2000.pong.Pong;
import net.l2mine2000.pong.PongGraphics;

import java.awt.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DropDownListButton<T extends Enum<T>> extends MenuButton {
    public static final HashMap<Integer, HashSet<Pong.State>> INDEXES = new HashMap<>();
    private final Class<T> clazz;
    private final ArrayList<SelectionButton<T>> options = new ArrayList<>();
    private final Color titleColor;
    private final Font titleFont;
    private boolean opened = false;
    private T selection;

    public DropDownListButton(Class<T> pEnum, Predicate<T> pFilter, T pDefault, float pX, float pY, int pWidth, int pHeight, Color pColor, Color pTextColor, Color pTitleColor, Pong.DiagonalDirection pLightSide, String pText, Font pFont, Font pTitleFont, Function<Boolean, Integer> pPreviousButton, Function<Boolean, Integer> pNextButton, Pong.State... pAllowedStates) {
        super(pX, pY, pWidth, pHeight, pColor, pTextColor, pLightSide, pText, pFont, pPreviousButton, pNextButton, pAllowedStates);
        this.clazz = pEnum;
        ArrayList<T> values = Arrays.stream(this.clazz.getEnumConstants()).filter(pFilter).collect(Collectors.toCollection(ArrayList::new));
        float offset = this.getHeight()/2f + 1+Pong.pixel(5);
        for (int i = 0; i < values.size(); i++) {
            Color mainColor = this.getColor();
            Color textColor = this.getTextColor();
            if (values.get(i) instanceof ColoredForButton val && val.useColor()) {
                mainColor = val.getMainColor();
                textColor = val.getTextColor();
            }
            createOption(values.get(i), i, this.getX()+offset, this.getEndY()+i*this.getHeight(), this.getWidth(), this.getHeight(), mainColor, textColor, this.getLightSide(), this.getFont(), pNextButton);
        }
        this.titleColor = pTitleColor;
        this.titleFont = pTitleFont;
        this.selection = pDefault;
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
            pGraphics.setThickness(Pong.pixel(3));
            if (this.opened) {
                pGraphics.g.drawLine((int) (this.getX()+Pong.pixel(5)), (int) (this.getCenterY() - (this.getHeight()/6f) +Pong.pixel(2)), (int) (this.getX() + this.getHeight()/4f + 1 +Pong.pixel(5)), (int) (this.getCenterY() + this.getHeight()/6f +Pong.pixel(2)));
                pGraphics.g.drawLine((int) (this.getX() + this.getHeight()/4f + 1 +Pong.pixel(5)), (int) (this.getCenterY() + this.getHeight()/6f +Pong.pixel(2)), (int) (this.getX() + this.getHeight()/2f + 1+Pong.pixel(5)), (int) (this.getCenterY() - (this.getHeight()/6f)+Pong.pixel(2)));
            }else {
                pGraphics.g.drawLine((int) (this.getX() + Pong.pixel(10)), (int) (this.getCenterY() - this.getHeight()/4f + 1), (int) (this.getX() + Pong.pixel(10) + this.getHeight()/3f), (int) this.getCenterY());
                pGraphics.g.drawLine((int) (this.getX() + Pong.pixel(10)), (int) (this.getCenterY() + this.getHeight()/4f), (int) (this.getX() + Pong.pixel(10) + this.getHeight()/3f), (int) this.getCenterY());
            }
            pGraphics.resetStroke();
            pGraphics.g.drawString(this.selection.toString(), this.getX() + this.getHeight()/2f + 1+Pong.pixel(5) + Pong.pixel(10), this.getCenterY() + metrics.getHeight()/4f);
            pGraphics.setFont(this.titleFont);
            metrics = pGraphics.g.getFontMetrics();
            pGraphics.setColor(this.titleColor);
            pGraphics.g.drawString(this.text, this.getX() - metrics.stringWidth(this.text) - pGraphics.getFont().getSize()/4f - Pong.pixel(17), this.getCenterY() + metrics.getHeight()/4f);
        }
    }

    @Override
    void run(Pong pPong) {
        if (!open()) {
            close();
        }
    }

    @Override
    public void run() {
        if (Pong.getInstance().fadeInCooldown <= 0) {
            this.active = false;
            this.run(Pong.getInstance());
        }
    }

    @Override
    public int getNextIndex(boolean pVertical) {
        return this.opened ? this.getIndex() - 5 : super.getNextIndex(pVertical);
    }

    public boolean open() {
        if (!this.opened) {
            closeAll();
            this.opened = true;
            Pong.getInstance().inDropDownList = true;
            return true;
        }return false;
    }

    public boolean close() {
        if (this.opened) {
            this.opened = false;
            Pong.getInstance().inDropDownList = oneOpened();
            return true;
        }return false;
    }

    public Class<T> getClazz() {
        return this.clazz;
    }

    public ArrayList<SelectionButton<T>> getOptions() {
        return this.options;
    }

    public T getSelection() {
        return this.selection;
    }

    public static boolean oneOpened() {
        for (MenuButton button : Pong.getInstance().buttons) {
            if (button instanceof DropDownListButton<?> listButton && listButton.opened) {
                return true;
            }
        }return false;
    }

    public static void closeAll() {
        for (MenuButton button : Pong.getInstance().buttons) {
            if (button instanceof DropDownListButton<?> list) {
                list.close();
                Pong.getInstance().inDropDownList = false;
            }
        }
    }

    private void createOption(T pValue, int pOptionIndex, float pX, float pY, int pWidth, int pHeight, Color pColor, Color pTextColor, Pong.DiagonalDirection pLightSide, Font pFont, Function<Boolean, Integer> pNextButton) {
        SelectionButton<T> option = new SelectionButton<>(this, pValue, pOptionIndex, pX, pY, pWidth, pHeight, pColor, pTextColor, pLightSide, pFont, pNextButton);
        register(option, SelectionButton.INDEXES);
        this.options.add(option);
    }

    public static boolean goodToGo(MenuButton pButton) {
        if (!Pong.getInstance().inDropDownList) {
            return true;
        }else return pButton instanceof DropDownListButton<?> || pButton instanceof DropDownListButton.SelectionButton<?>;
    }

    public static <T extends Enum<T>> void create(Class<T> pEnum, Predicate<T> pFilter, T pDefault, float pX, float pY, int pWidth, int pHeight, Color pColor, Color pTextColor, Color pTitleColor, Pong.DiagonalDirection pLightSide, String pText, Font pFont, Font pTitleFont, Function<Boolean, Integer> pPreviousButton, Function<Boolean, Integer> pNextButton, Pong.State... pAllowedStates) {
        DropDownListButton<T> list = new DropDownListButton<>(pEnum, pFilter, pDefault,pX, pY, pWidth, pHeight, pColor, pTextColor, pTitleColor, pLightSide, pText, pFont, pTitleFont, pPreviousButton, pNextButton, pAllowedStates);
        register(list, INDEXES);

    }


    public static class SelectionButton<T extends Enum<T>> extends MenuButton {
        public static final HashMap<Integer, HashSet<Pong.State>> INDEXES = new HashMap<>();
        private final DropDownListButton<T> list;
        private final int optionIndex;
        private final T value;

        private SelectionButton(DropDownListButton<T> pList, T pValue, int pIndex, float pX, float pY, int pWidth, int pHeight, Color pColor, Color pTextColor, Pong.DiagonalDirection pLightSide, Font pFont, Function<Boolean, Integer> pNextButton) {
            super(pX, pY, pWidth, pHeight, pColor, pTextColor, pLightSide, pValue.toString(), pFont, null, pNextButton, (pList.allowedStates.toArray(new Pong.State[0])));
            this.list = pList;
            this.optionIndex = pIndex;
            this.value = pValue;
        }

        @Override
        void run(Pong pPong) {
            this.list.close();
            this.list.selection = this.value;
            this.list.setSelected(true);
        }

        @Override
        public boolean isVisible(Pong.State pState) {
            return this.list.isVisible(pState) && this.list.opened;
        }

        public T getValue() {
            return this.value;
        }

        @Override
        public boolean isMouseOver() {
            boolean f = this.optionIndex == 0;
            boolean l = this.optionIndex == this.list.options.size()-1;
            Point point = Pong.getInstance().getMousePosition();
            if (point != null) {
                float offset = this.thickness / 2f;
                if (this.optionIndex == 0) {
                    return point.x >= this.getX() - offset && point.x < this.getEndX() + offset && point.y >= this.getY() - offset && point.y < this.getEndY() && !this.list.isMouseOver();
                }else if (this.optionIndex == this.list.options.size()-1) {
                    return point.x >= this.getX() - offset && point.x < this.getEndX() + offset && point.y >= this.getY() && point.y < this.getEndY() + offset;
                }else {
                    return point.x >= this.getX() - offset && point.x < this.getEndX() + offset && point.y >= this.getY() && point.y < this.getEndY();
                }
            }
            return false;
        }

        @Override
        public int getPreviousIndex(boolean pVertical) {
            return (this.list.opened && this.optionIndex != 0) ? this.getIndex()-1 : this.list.getIndex();
        }

        @Override
        public int getNextIndex(boolean pVertical) {
            return (this.list.opened && this.optionIndex != this.list.options.size()-1) ? this.getIndex()+1 : super.getNextIndex(pVertical);
        }
    }
}
