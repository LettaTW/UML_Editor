package umleditor.domain.node;

import umleditor.domain.model.Port;

import java.awt.*;

public class Rect extends Node {
    public Rect(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public boolean contains(Point p) {
        return getBounds().contains(p);
    }

    @Override
    public void draw(Graphics2D g2) {
        Rectangle r = getBounds();
        g2.setColor(Color.WHITE);
        g2.fillRect(r.x, r.y, r.width, r.height);
        g2.setColor(Color.BLACK);
        g2.drawRect(r.x, r.y, r.width, r.height);
    }

    @Override
    public void updatePorts() {
        ports.clear();

        int left = x;
        int right = x + width;
        int top = y;
        int bottom = y + height;
        int middleX = x + (width / 2);
        int middleY = y + (height / 2);

        ports.add(new Port(getID(), left, top));
        ports.add(new Port(getID(), middleX, top));
        ports.add(new Port(getID(), right, top));
        ports.add(new Port(getID(), right, middleY));
        ports.add(new Port(getID(), right, bottom));
        ports.add(new Port(getID(), middleX, bottom));
        ports.add(new Port(getID(), left, bottom));
        ports.add(new Port(getID(), left, middleY));
    }
}
