package umleditor.domain.node;

import umleditor.domain.model.Port;
import umleditor.domain.model.PortDirection;

import java.awt.*;

import static umleditor.config.EditorDefaults.DEFAULT_OVAL_FILL_COLOR;
import static umleditor.config.EditorDefaults.DEFAULT_OVAL_LABEL_TEXT;

public class Oval extends Node {
    public Oval(int x, int y, int width, int height) {
        super(x, y, width, height);
        setFillColor(DEFAULT_OVAL_FILL_COLOR);
        setLabelText(DEFAULT_OVAL_LABEL_TEXT);
    }

    @Override
    protected void initPorts() {
        ports.put(PortDirection.NORTH,      new Port(getID(), 0, 0));
        ports.put(PortDirection.EAST,       new Port(getID(), 0, 0));
        ports.put(PortDirection.SOUTH,      new Port(getID(), 0, 0));
        ports.put(PortDirection.WEST,       new Port(getID(), 0, 0));
    }

    @Override
    public boolean contains(Point p) {
        double rx = width / 2.0;
        double ry = height / 2.0;
        if (rx <= 0 || ry <= 0) {
            return false;
        }

        double cx = x + rx;
        double cy = y + ry;
        double nx = (p.x - cx) / rx;
        double ny = (p.y - cy) / ry;
        return (nx * nx) + (ny * ny) <= 1.0;
    }

    @Override
    protected void drawOutlineShape(Graphics2D g2, Rectangle r) {
        g2.drawOval(r.x - 2, r.y - 2, r.width + 4, r.height + 4);
    }

    @Override
    public void draw(Graphics2D g2) {
        Rectangle r = getBounds();
        g2.setColor(getFillColor());
        g2.fillOval(r.x, r.y, r.width, r.height);
        g2.setColor(Color.BLACK);
        g2.drawOval(r.x, r.y, r.width, r.height);
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

        ports.get(PortDirection.NORTH).setPosition(middleX, top);
        ports.get(PortDirection.EAST).setPosition(right, middleY);
        ports.get(PortDirection.SOUTH).setPosition(middleX, bottom);
        ports.get(PortDirection.WEST).setPosition(left, middleY);
    }
}
