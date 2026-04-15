package umleditor.domain.node;

import umleditor.domain.BaseElement;
import umleditor.domain.DiagramElement;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Composite extends BaseElement {
    private final List<DiagramElement> children = new ArrayList<>();

    public Composite(List<DiagramElement> elements) {
        if (elements != null) {
            for (DiagramElement element : elements) {
                if (element != null) {
                    element.setSelected(false);
                    element.setHovered(false);
                    children.add(element);
                }
            }
        }
    }

    public List<DiagramElement> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public List<DiagramElement> releaseChildren() {
        return new ArrayList<>(children);
    }

    public List<String> getContainedNodeIds() {
        List<String> ids = new ArrayList<>();
        collectNodeIds(children, ids);
        return ids;
    }

    @Override
    public void moveBy(int dx, int dy) {
        for (DiagramElement child : children) {
            child.moveBy(dx, dy);
        }
    }

    @Override
    public Rectangle getBounds() {
        if (children.isEmpty()) {
            return new Rectangle(0, 0, 0, 0);
        }

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (DiagramElement child : children) {
            Rectangle bounds = child.getBounds();
            minX = Math.min(minX, bounds.x);
            minY = Math.min(minY, bounds.y);
            maxX = Math.max(maxX, bounds.x + bounds.width);
            maxY = Math.max(maxY, bounds.y + bounds.height);
        }

        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    @Override
    public boolean contains(Point p) {
        return getBounds().contains(p);
    }

    @Override
    public void draw(Graphics2D g2) {
        for (DiagramElement child : children) {
            child.draw(g2);
        }

        if (!isSelected() && !isHovered()) {
            return;
        }

        Rectangle bounds = getBounds();
        if (bounds.width == 0 && bounds.height == 0) {
            return;
        }

        Stroke oldStroke = g2.getStroke();
        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{6f, 4f}, 0));
        g2.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        g2.setStroke(oldStroke);
    }

    private void collectNodeIds(List<DiagramElement> elements, List<String> out) {
        for (DiagramElement element : elements) {
            if (element instanceof Node) {
                out.add(element.getID());
                continue;
            }

            if (element instanceof Composite composite) {
                collectNodeIds(composite.getChildren(), out);
            }
        }
    }
}



