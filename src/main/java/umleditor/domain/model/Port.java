package umleditor.domain.model;

import static umleditor.config.EditorDefaults.PORT_SIZE;

import java.awt.*;


public class Port implements Handle{
    private final String ownerId;
    private int x;
    private int y;
    private int size = PORT_SIZE;

    public Port(String ownerId, int x, int y) {
        this.ownerId = ownerId;
        this.x = x;
        this.y = y;
    }
    public String getOwnerId() { return ownerId; }

    public int getX() { return x; }
    public int getY() { return y; }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }


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