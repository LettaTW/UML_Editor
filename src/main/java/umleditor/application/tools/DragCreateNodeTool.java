package umleditor.application.tools;

import umleditor.application.service.NodeFactory;
import umleditor.domain.DiagramDocument;
import umleditor.domain.node.Node;
import umleditor.enumtype.ToolMode;

import java.awt.*;

import static umleditor.config.EditorDefaults.DEFAULT_NODE_HEIGHT;
import static umleditor.config.EditorDefaults.DEFAULT_NODE_WIDTH;

public abstract class DragCreateNodeTool implements Tool {
    private final DiagramDocument model;
    private final ToolMode mode;
    private final NodeFactory nodeFactory;

    private Point dragStart;

    protected DragCreateNodeTool(DiagramDocument model, ToolMode mode, NodeFactory nodeFactory) {
        this.model = model;
        this.mode = mode;
        this.nodeFactory = nodeFactory;
    }

    @Override
    public void mousePressed(Point p) {
        dragStart = p;
    }

    @Override
    public void mouseDragged(Point p) {
        // Default-size creation does not depend on drag distance.
    }

    @Override
    public boolean mouseReleased(Point p) {
        if (dragStart == null) {
            return false;
        }

        // Center the new node at the release point by adjusting the top-left corner position
        int left = p.x - (DEFAULT_NODE_WIDTH / 2);
        int top = p.y - (DEFAULT_NODE_HEIGHT / 2);
        Rectangle bounds = new Rectangle(left, top, DEFAULT_NODE_WIDTH, DEFAULT_NODE_HEIGHT);
        Node createdNode = nodeFactory.createNode(mode, bounds);
        dragStart = null;

        if (createdNode == null) {
            return false;
        }

        model.addElement(createdNode);
        return true;
    }

    @Override
    public void drawOverlay(Graphics2D g2) {
        // Default-size creation does not render drag-size preview.
    }
}

