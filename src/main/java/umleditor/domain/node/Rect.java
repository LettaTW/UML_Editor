package umleditor.domain.node;

import umleditor.domain.model.Port;

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
    public void draw(Graphics2D g2) {
        Rectangle r = getBounds();
        g2.setColor(getFillColor());
        g2.fillRect(r.x, r.y, r.width, r.height);
        g2.setColor(Color.BLACK);
        g2.drawRect(r.x, r.y, r.width, r.height);
        drawCenteredLabel(g2, r);
        drawPortsIfNeeded(g2);
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
