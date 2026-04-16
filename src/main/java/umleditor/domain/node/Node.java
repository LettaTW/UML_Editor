package umleditor.domain.node;

import umleditor.config.EditorDefaults;
import umleditor.domain.model.Port;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public abstract class Node extends Block {
    // Define the Node's Bounds
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    private Color fillColor = Color.WHITE;
    private String labelText = EditorDefaults.DEFAULT_LABEL_TEXT;

    protected final List<Port> ports = new ArrayList<>();
    protected Node(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        updatePorts();
    }

    @Override
    public void moveBy(int dx, int dy) {
        x += dx;
        y += dy;
        updatePorts();
    }

    public void setBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        updatePorts();
    }

    /**
     * Gets the bounding {@code Rectangle} of this {@code Node}.
     * @return a new {@code Rectangle}, equal the {@code Node}'s selected range (x, y, width, height)
     */
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public List<Port> getPorts() {
        return Collections.unmodifiableList(ports);
    }

    public Port findPortAt(Point p) {
        for (Port port : ports) {
            if (port.contains(p)) {
                return port;
            }
        }
        return null;
    }

    @Override
    public List<String> collectOwnedNodeIds() {
        return Collections.singletonList(getID());
    }

    public void resizeTo(Rectangle bounds) {
        if (bounds == null) {
            return;
        }
        setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
    }


    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public Color getFillColor() { return fillColor; }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public String getLabelText() { return labelText; }

    public void setLabelText(String labelText) {
        this.labelText = labelText;
    }

    protected void drawCenteredLabel(Graphics2D g2, Rectangle bounds) {
        if (labelText == null || labelText.isEmpty()) {
            return;
        }

        Font oldFont = g2.getFont();
        g2.setFont(oldFont.deriveFont((float) EditorDefaults.DEFAULT_LABEL_FONT_SIZE));
        FontMetrics fm = g2.getFontMetrics();

        int textX = bounds.x + (bounds.width - fm.stringWidth(labelText)) / 2;
        int textY = bounds.y + (bounds.height + fm.getAscent() - fm.getDescent()) / 2;

        g2.setColor(Color.BLACK);
        g2.drawString(labelText, textX, textY);
        g2.setFont(oldFont);
    }

    protected void drawPortsIfNeeded(Graphics2D g2) {
        if (!isSelected() && !isHovered()) {
            return;
        }

        g2.setColor(isSelected()
                ? EditorDefaults.NODE_SELECTED_PORT_COLOR
                : EditorDefaults.NODE_HOVER_PORT_COLOR);
        for (Port port : ports) {
            Rectangle b = port.getBounds();
            g2.fillRect(b.x, b.y, b.width, b.height);
        }
    }

    protected void drawRectInteractionOutline(Graphics2D g2, Rectangle r) {
        if (!isSelected() && !isHovered()) {
            return;
        }

        Color oldColor = g2.getColor();
        g2.setColor(isSelected()
                ? EditorDefaults.NODE_SELECTED_OUTLINE_COLOR
                : EditorDefaults.NODE_HOVER_OUTLINE_COLOR);
        g2.drawRect(r.x - 2, r.y - 2, r.width + 4, r.height + 4);
        g2.setColor(oldColor);
    }

    protected void drawOvalInteractionOutline(Graphics2D g2, Rectangle r) {
        if (!isSelected() && !isHovered()) {
            return;
        }

        Color oldColor = g2.getColor();
        g2.setColor(isSelected()
                ? EditorDefaults.NODE_SELECTED_OUTLINE_COLOR
                : EditorDefaults.NODE_HOVER_OUTLINE_COLOR);
        g2.drawOval(r.x - 2, r.y - 2, r.width + 4, r.height + 4);
        g2.setColor(oldColor);
    }

    public abstract void draw(Graphics2D g2);

    public abstract void updatePorts();

}
