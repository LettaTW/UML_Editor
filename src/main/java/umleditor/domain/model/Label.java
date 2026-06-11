package umleditor.domain.model;

import umleditor.config.EditorDefaults;

import java.awt.*;

public class Label {
    private String text;
    private Color fillColor;

    public Label(String text, Color fillColor) {
        this.text = text;
        this.fillColor = fillColor;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public void drawCentered(Graphics2D g2, Rectangle bounds) {
        if (text == null || text.isEmpty()) {
            return;
        }

        Font oldFont = g2.getFont();
        g2.setFont(oldFont.deriveFont((float) EditorDefaults.DEFAULT_LABEL_FONT_SIZE));
        FontMetrics fm = g2.getFontMetrics();

        int textX = bounds.x + (bounds.width - fm.stringWidth(text)) / 2;
        int textY = bounds.y + (bounds.height + fm.getAscent() - fm.getDescent()) / 2;

        g2.setColor(Color.BLACK);
        g2.drawString(text, textX, textY);
        g2.setFont(oldFont);
    }

}
