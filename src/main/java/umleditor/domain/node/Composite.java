package umleditor.domain.node;

import umleditor.config.EditorDefaults;
import umleditor.domain.DiagramElement;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static umleditor.config.EditorDefaults.clampDepth;

public class Composite extends Block {
    private final List<Block> children = new ArrayList<>();
    private final Map<String, Integer> relativeDepthById = new HashMap<>();

    public Composite(List<DiagramElement> elements) {
        if (elements != null) {
            for (DiagramElement element : elements) {
                addElementToChildren(element);
            }
        }
        rebuildRelativeDepth();
    }

    public List<Block> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public List<DiagramElement> releaseChildren() {
        return new ArrayList<>(children);
    }

    public List<DiagramElement> releaseChildrenWithAbsoluteDepth(int compositeDepth) {
        List<Block> ordered = new ArrayList<>(children);
        ordered.sort(Comparator.comparingInt(this::relativeDepthOf));

        List<DiagramElement> released = new ArrayList<>(ordered.size());
        for (Block child : ordered) {
            int absoluteDepth = clampDepth(compositeDepth + relativeDepthOf(child));
            child.setDepth(absoluteDepth);
            released.add(child);
        }
        return released;
    }

    @Override
    public List<String> collectOwnedNodeIds() {
        List<String> ids = new ArrayList<>();
        for (Block child : children) {
            ids.addAll(child.collectOwnedNodeIds());
        }
        return ids;
    }

    @Override
    public void moveBy(int dx, int dy) {
        for (Block child : children) {
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

        for (Block child : children) {
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
        List<Block> ordered = new ArrayList<>(children);
        // Draw from back to front based on normalized relative depth.
        ordered.sort(Comparator.comparingInt(this::relativeDepthOf).reversed());
        for (Block child : ordered) {
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

    private void addElementToChildren(DiagramElement element) {
        if (!(element instanceof Block block)) {
            return;
        }

        addBlockToChildren(block);
    }

    private void addBlockToChildren(Block block) {

        addDetachedChild(block);
    }

    private void addDetachedChild(Block child) {
        child.setSelected(false);
        child.setHovered(false);
        children.add(child);
    }

    private void rebuildRelativeDepth() {
        relativeDepthById.clear();
        List<Block> ordered = new ArrayList<>(children);
        ordered.sort(Comparator.comparingInt(Block::getDepth));
        for (int i = 0; i < ordered.size(); i++) {
            relativeDepthById.put(ordered.get(i).getID(), i);
        }
    }

    private int relativeDepthOf(Block block) {
        Integer depth = relativeDepthById.get(block.getID());
        if (depth == null) {
            return Integer.MAX_VALUE;
        }
        return depth;
    }
}



