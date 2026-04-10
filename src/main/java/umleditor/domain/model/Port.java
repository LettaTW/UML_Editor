package umleditor.domain.model;

import umleditor.domain.node.Node;

import static umleditor.config.EditorDefaults.MIN_NODE_SIZE;
import static umleditor.config.EditorDefaults.PORT_SIZE;

import java.awt.*;


public class Port implements Handle{
    private final Node ownerNode;
    private int x;
    private int y;
    private int size = PORT_SIZE;

    public Port(String ownerNodeId, Node ownerNode, int x, int y) {
        this.ownerNode = ownerNode;
        this.x = x;
        this.y = y;
    }

    public Node getOwnerNodeId() { return ownerNode; }

    public int getX() { return x; }
    public int getY() { return y; }
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public void dragTo(int x, int y) {
        Point anchor = ownerNode.getOppositeAnchor(this);
        ownerNode.resizeByCorner(anchor, new Point(x, y), MIN_NODE_SIZE);
    }

    @Override
    public Rectangle getBounds() {
        int half = size / 2;
        return new Rectangle(x - half, y - half, size, size);
    }

    @Override
    public boolean contains(Point p) {
        return getBounds().contains(p);
    }
}