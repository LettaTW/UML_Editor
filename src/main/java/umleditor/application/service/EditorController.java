package umleditor.application.service;

import umleditor.application.tools.CreateOvalTool;
import umleditor.application.tools.CreateRectTool;
import umleditor.application.tools.SelectTool;
import umleditor.application.tools.Tool;
import umleditor.application.tools.ToolManager;
import umleditor.domain.DiagramElement;
import umleditor.domain.DiagramDocument;
import umleditor.domain.node.Node;
import umleditor.enumtype.ToolMode;

import java.awt.*;
import java.util.List;

import static umleditor.config.EditorDefaults.DEFAULT_NODE_HEIGHT;
import static umleditor.config.EditorDefaults.DEFAULT_NODE_WIDTH;

public class EditorController {
    private final DiagramDocument document;
    private final ToolManager toolManager;

    public EditorController() {
        this.document = new DiagramDocument();
        this.toolManager = new ToolManager();

        toolManager.registerTool(ToolMode.SELECT, new SelectTool(document));
        toolManager.registerTool(ToolMode.CREATE_RECT, new CreateRectTool(document));
        toolManager.registerTool(ToolMode.CREATE_OVAL, new CreateOvalTool(document));

        toolManager.setTool(ToolMode.SELECT);
    }

    public void setCurrentTool(ToolMode mode) {
        toolManager.setTool(mode);
    }

    public void setTemporaryTool(ToolMode mode) {
        toolManager.setTemporaryTool(mode);
    }

    public ToolMode getCurrentToolMode() {
        return toolManager.getCurrentMode();
    }

    public void restorePreviousTool() {
        toolManager.restorePreviousTool();
    }

    public void createDefaultNodeAt(ToolMode mode, int x, int y) {
        if (!mode.isCreateMode()) {
            return;
        }
        // Center the new node at (x, y) by adjusting the top-left corner position
        int left = x - (DEFAULT_NODE_WIDTH / 2);
        int top = y - (DEFAULT_NODE_HEIGHT / 2);
        Rectangle bounds = new Rectangle(left, top, DEFAULT_NODE_WIDTH, DEFAULT_NODE_HEIGHT);
        Node createdNode = mode.createNode(bounds);
        if (createdNode == null) {
            return;
        }

        document.addElement(createdNode);
    }

    public void onMousePressed(int x, int y, int button) {
        if (button != 1) {
            return;
        }
        getCurrentTool().mousePressed(new Point(x, y));
    }

    public void onMouseDragged(int x, int y) {
        getCurrentTool().mouseDragged(new Point(x, y));
    }

    public void onMouseMoved(int x, int y) {
        getCurrentTool().mouseMoved(new Point(x, y));
    }

    public void onMouseReleased(int x, int y) {
        boolean shouldRestore = getCurrentTool().mouseReleased(new Point(x, y));
        if (shouldRestore && toolManager.getCurrentMode().isCreateMode()) {
            toolManager.restorePreviousTool();
        }
    }

    public List<DiagramElement> getElements() {
        return document.getElements();
    }

    public List<DiagramElement> getElementsForRender() {
        return document.getElementsForRender();
    }

    public void drawToolOverlay(Graphics2D g2) {
        getCurrentTool().drawOverlay(g2);
    }

    private Tool getCurrentTool() {
        Tool tool = toolManager.getCurrentTool();
        if (tool == null) {
            throw new IllegalStateException("Current tool is not registered");
        }
        return tool;
    }
}


