package umleditor.application.tools;

import umleditor.application.service.DiagramModel;
import umleditor.domain.node.Node;
import umleditor.enumtype.ToolMode;

import java.awt.*;

import static umleditor.config.EditorDefaults.MIN_NODE_SIZE;

public abstract class DragCreateNodeTool implements Tool {
    private final DiagramModel model;
    private final ToolMode mode;

    private Point dragStart;
    private Point dragCurrent;

    protected DragCreateNodeTool(DiagramModel model, ToolMode mode) {
        this.model = model;
        this.mode = mode;
    }

    @Override
    public void mousePressed(Point p) {
        dragStart = p;
        dragCurrent = p;
    }

    @Override
    public void mouseDragged(Point p) {
        if (dragStart == null) {
            return;
        }
        dragCurrent = p;
    }

    @Override
    public boolean mouseReleased(Point p) {
        if (dragStart == null) {
            return false;
        }

        Rectangle bounds = buildBounds(dragStart, p);
        Node createdNode = mode.createNode(bounds);
        if (createdNode != null) {
            model.addElement(createdNode);
        }

        dragStart = null;
        dragCurrent = null;
        return true;
    }

    @Override
    public void drawOverlay(Graphics2D g2) {
        if (dragStart == null || dragCurrent == null) {
            return;
        }

        Rectangle preview = buildBounds(dragStart, dragCurrent);
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5f, 4f}, 0));
        g2.setColor(Color.DARK_GRAY);
        mode.drawPreview(g2, preview);
        g2.setStroke(oldStroke);
    }

    private Rectangle buildBounds(Point start, Point end) {
        int x = Math.min(start.x, end.x);
        int y = Math.min(start.y, end.y);
        int width = Math.max(MIN_NODE_SIZE, Math.abs(end.x - start.x));
        int height = Math.max(MIN_NODE_SIZE, Math.abs(end.y - start.y));
        return new Rectangle(x, y, width, height);
    }
}

