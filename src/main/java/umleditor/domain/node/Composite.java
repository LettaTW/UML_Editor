package umleditor.domain.node;

import umleditor.config.EditorDefaults;
import umleditor.domain.BaseElement;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static umleditor.config.EditorDefaults.clampDepth;

public class Composite extends BaseElement {

    private final List<BaseElement> children = new ArrayList<>();
    private final Map<String, Integer> relativeDepthById = new HashMap<>();

    public Composite(List<BaseElement> elements) {
        if (elements != null) {
            for (BaseElement element : elements) {
                addDetachedChild(element);
            }
        }
        rebuildRelativeDepth();
    }

    @Override
    public boolean isComposite() {
        return true;
    }

    @Override
    public List<BaseElement> ungroup() {
        return releaseChildrenWithAbsoluteDepth(getDepth());
    }

    private List<BaseElement> releaseChildrenWithAbsoluteDepth(int compositeDepth) {
        List<BaseElement> ordered = new ArrayList<>(children);
        ordered.sort(Comparator.comparingInt(this::relativeDepthOf));

        List<BaseElement> released = new ArrayList<>(ordered.size());
        for (BaseElement child : ordered) {
            int absoluteDepth = clampDepth(compositeDepth + relativeDepthOf(child));
            child.setDepth(absoluteDepth);
            released.add(child);
        }
        return released;
    }

    @Override
    public List<String> collectOwnedNodeIds() {
        List<String> ids = new ArrayList<>();
        for (BaseElement child : children) {
            ids.addAll(child.collectOwnedNodeIds());
        }
        return ids;
    }

    @Override
    public void moveBy(int dx, int dy) {
        for (BaseElement child : children) {
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

        for (BaseElement child : children) {
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
        List<BaseElement> ordered = new ArrayList<>(children);
        // Draw from back to front based on normalized relative depth
        ordered.sort(Comparator.comparingInt(this::relativeDepthOf).reversed());
        for (BaseElement child : ordered) {
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
        g2.setColor(isSelected()
                ? EditorDefaults.NODE_SELECTED_OUTLINE_COLOR
                : EditorDefaults.NODE_HOVER_OUTLINE_COLOR);
        g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{6f, 4f}, 0));
        g2.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        g2.setStroke(oldStroke);
    }

    private void addDetachedChild(BaseElement child) {
        child.setSelected(false);
        child.setHovered(false);
        children.add(child);
    }

    private void rebuildRelativeDepth() {
        relativeDepthById.clear();
        List<BaseElement> ordered = new ArrayList<>(children);
        ordered.sort(Comparator.comparingInt(BaseElement::getDepth));
        for (int i = 0; i < ordered.size(); i++) {
            relativeDepthById.put(ordered.get(i).getID(), i);
        }
    }

    private int relativeDepthOf(BaseElement element) {
        Integer depth = relativeDepthById.get(element.getID());
        if (depth == null) {
            return Integer.MAX_VALUE;
        }
        return depth;
    }
}