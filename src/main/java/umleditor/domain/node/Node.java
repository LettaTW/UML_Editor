package umleditor.domain.node;

import umleditor.config.EditorDefaults;
import umleditor.domain.model.Label;
import umleditor.domain.model.Port;
import umleditor.domain.model.PortDirection;

import java.awt.*;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public abstract class Node extends Block {
    // Define the Node's Bounds
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected Label label;

    protected final Map<PortDirection, Port> ports = new EnumMap<>(PortDirection.class);
    protected Node(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = new Label(EditorDefaults.DEFAULT_LABEL_TEXT, Color.WHITE);
        initPorts();
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
        return List.copyOf(ports.values());
    }

    public Port findPortAt(Point p) {
        for (Port port : ports.values()) {
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

    public Color getFillColor() { return label.getFillColor(); }

    public void setFillColor(Color fillColor) { this.label.setFillColor(fillColor); }

    public String getLabelText() { return this.label.getText(); }

    public void setLabelText(String labelText) { this.label.setText(labelText); }

    // any draw logic

    protected void drawCenteredLabel(Graphics2D g2, Rectangle bounds) {
        label.drawCentered(g2, bounds);
    }

    // draw poot for hover or selection state
    protected void drawPortsIfNeeded(Graphics2D g2) {
        if (!isSelected() && !isHovered()) {
            return;
        }

        Color portColor = isSelected() ? EditorDefaults.NODE_SELECTED_PORT_COLOR : EditorDefaults.NODE_HOVER_PORT_COLOR;
        for (Port port : ports.values()) {
            port.draw(g2, portColor);
        }
    }

    // draw outline for hover or selection state
    protected void drawInteractionOutlineIfNeeded(Graphics2D g2, Rectangle r) {
        if (!isSelected() && !isHovered()) {
            return;
        }

        Color oldColor = g2.getColor();
        g2.setColor(isSelected()
                ? EditorDefaults.NODE_SELECTED_OUTLINE_COLOR
                : EditorDefaults.NODE_HOVER_OUTLINE_COLOR);
        drawOutlineShape(g2, r);
        g2.setColor(oldColor);
    }

    protected abstract void drawOutlineShape(Graphics2D g2, Rectangle r);
    protected abstract void initPorts();
    public abstract void draw(Graphics2D g2);

    // update port's position
    public abstract void updatePorts();

}
