package umleditor.domain.node;

import umleditor.domain.model.Port;
import umleditor.domain.model.PortDirection;

import java.awt.*;

import static umleditor.config.EditorDefaults.DEFAULT_RECT_FILL_COLOR;
import static umleditor.config.EditorDefaults.DEFAULT_RECT_LABEL_TEXT;

public class Rect extends Node {
    public Rect(int x, int y, int width, int height) {
        super(x, y, width, height);
        setFillColor(DEFAULT_RECT_FILL_COLOR);
        setLabelText(DEFAULT_RECT_LABEL_TEXT);
    }

    @Override
    public boolean contains(Point p) {
        return getBounds().contains(p);
    }

    @Override
    protected void drawOutlineShape(Graphics2D g2, Rectangle r) {
        g2.drawRect(r.x - 2, r.y - 2, r.width + 4, r.height + 4);
    }

    @Override
    protected void initPorts() {
        ports.put(PortDirection.NORTH_WEST, new Port(getID(), 0, 0));
        ports.put(PortDirection.NORTH,      new Port(getID(), 0, 0));
        ports.put(PortDirection.NORTH_EAST, new Port(getID(), 0, 0));
        ports.put(PortDirection.EAST,       new Port(getID(), 0, 0));
        ports.put(PortDirection.SOUTH_EAST, new Port(getID(), 0, 0));
        ports.put(PortDirection.SOUTH,      new Port(getID(), 0, 0));
        ports.put(PortDirection.SOUTH_WEST, new Port(getID(), 0, 0));
        ports.put(PortDirection.WEST,       new Port(getID(), 0, 0));
    }

    @Override
    public void draw(Graphics2D g2) {
        Rectangle r = getBounds();
        g2.setColor(getFillColor());
        g2.fillRect(r.x, r.y, r.width, r.height);
        g2.setColor(Color.BLACK);
        g2.drawRect(r.x, r.y, r.width, r.height);
        drawInteractionOutlineIfNeeded(g2, r);
        drawCenteredLabel(g2, r);
        drawPortsIfNeeded(g2);
    }

    @Override
    public void updatePorts() {
        int left = x;
        int right = x + width;
        int top = y;
        int bottom = y + height;
        int middleX = x + (width / 2);
        int middleY = y + (height / 2);

        ports.get(PortDirection.NORTH_WEST).setPosition(left, top);
        ports.get(PortDirection.NORTH).setPosition(middleX, top);
        ports.get(PortDirection.NORTH_EAST).setPosition(right, top);
        ports.get(PortDirection.EAST).setPosition(right, middleY);
        ports.get(PortDirection.SOUTH_EAST).setPosition(right, bottom);
        ports.get(PortDirection.SOUTH).setPosition(middleX, bottom);
        ports.get(PortDirection.SOUTH_WEST).setPosition(left, bottom);
        ports.get(PortDirection.WEST).setPosition(left, middleY);
    }
}
