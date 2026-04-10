package umleditor.domain.node;

import umleditor.domain.BaseElement;
import umleditor.domain.model.Port;

import java.awt.*;
import java.util.ArrayList;

public abstract class Node extends BaseElement {
    // Define the Node's Bounds
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    protected ArrayList<Port> ports = new ArrayList<Port>();
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

    /**
     * Gets the bounding {@code Rectangle} of this {@code Node}.
     * @return a new {@code Rectangle}, equal the {@code Node}'s selected range (x, y, width, height)
     */

    public Point getOppositeAnchor(Port port) {
        int oppositeX = (2 * this.x + this.width) - port.getX();
        int oppositeY = (2 * this.y + this.height) - port.getY();

        return new Point(oppositeX, oppositeY);
    }

    public void resizeByCorner(Point anchor, Point current, int minNodeSize) {
        int newWidth = Math.max(minNodeSize, Math.abs(current.x - anchor.x));
        int newHeight = Math.max(minNodeSize, Math.abs(current.y - anchor.y));

        int newX = (current.x < anchor.x)? (anchor.x - newWidth) : anchor.x;
        int newY = (current.y < anchor.y)? (anchor.y - newHeight) : anchor.y;

        this.x = newX;
        this.y = newY;
        this.width = newWidth;
        this.height = newHeight;

        updatePorts();
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    @Override
    public Rectangle getBounds() { return new Rectangle(x, y, width, height); }

    public abstract void updatePorts();

}
