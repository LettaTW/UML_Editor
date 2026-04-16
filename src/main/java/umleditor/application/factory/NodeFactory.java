package umleditor.application.factory;

import umleditor.domain.node.Node;
import umleditor.domain.node.Oval;
import umleditor.domain.node.Rect;
import umleditor.enumtype.ToolMode;

import java.awt.Rectangle;

public class NodeFactory {
    public Node createNode(ToolMode mode, Rectangle bounds) {
        if (mode == null || bounds == null) {
            return null;
        }

        return switch (mode) {
            case CREATE_RECT -> new Rect(bounds.x, bounds.y, bounds.width, bounds.height);
            case CREATE_OVAL -> new Oval(bounds.x, bounds.y, bounds.width, bounds.height);
            default -> null;
        };
    }

    public boolean isCreateMode(ToolMode mode) {
        return mode == ToolMode.CREATE_RECT || mode == ToolMode.CREATE_OVAL;
    }
}

