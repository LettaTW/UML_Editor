package umleditor.application.service;

import umleditor.application.tools.CreateOvalTool;
import umleditor.application.tools.CreateRectTool;
import umleditor.application.tools.SelectTool;
import umleditor.application.tools.Tool;
import umleditor.application.tools.ToolManager;
import umleditor.domain.DiagramElement;
import umleditor.enumtype.ToolMode;

import java.awt.*;
import java.util.List;

public class EditorController {
    private final DiagramModel document;
    private final ToolManager toolManager;

    public EditorController() {
        this.document = new DiagramModel();
        this.toolManager = new ToolManager();

        toolManager.registerTool(ToolMode.SELECT, new SelectTool());
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

    public void onMousePressed(int x, int y, int button) {
        if (button != 1) {
            return;
        }
        getCurrentTool().mousePressed(new Point(x, y));
    }

    public void onMouseDragged(int x, int y) {
        getCurrentTool().mouseDragged(new Point(x, y));
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

