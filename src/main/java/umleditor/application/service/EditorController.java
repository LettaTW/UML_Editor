package umleditor.application.service;

import umleditor.application.tools.CreateOvalTool;
import umleditor.application.tools.CreateRectTool;
import umleditor.application.tools.DragCreateLinkTool;
import umleditor.application.tools.SelectTool;
import umleditor.application.tools.Tool;
import umleditor.application.tools.ToolManager;
import umleditor.domain.DiagramElement;
import umleditor.domain.DiagramDocument;
import umleditor.domain.link.Link;
import umleditor.domain.node.Composite;
import umleditor.domain.node.Node;
import umleditor.enumtype.ToolMode;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static umleditor.config.EditorDefaults.DEFAULT_NODE_HEIGHT;
import static umleditor.config.EditorDefaults.DEFAULT_NODE_WIDTH;

public class EditorController {
    public record LabelEditState(String text, Color fillColor) {
    }

    private final DiagramDocument document;
    private final ToolManager toolManager;

    public EditorController() {
        this.document = new DiagramDocument();
        this.toolManager = new ToolManager();
        PointerTargetingService pointerTargetingService = new PointerTargetingService(document);
        ResizeGestureService resizeGestureService = new ResizeGestureService();
        ElementTransformService elementTransformService = new DefaultElementTransformService(document);

        toolManager.registerTool(
                ToolMode.SELECT,
                new SelectTool(document, pointerTargetingService, resizeGestureService, elementTransformService)
        );
        toolManager.registerTool(ToolMode.CREATE_RECT, new CreateRectTool(document));
        toolManager.registerTool(ToolMode.CREATE_OVAL, new CreateOvalTool(document));
        toolManager.registerTool(ToolMode.LINK_ASSOCIATION, new DragCreateLinkTool(document, ToolMode.LINK_ASSOCIATION, pointerTargetingService));
        toolManager.registerTool(ToolMode.LINK_GENERALIZATION, new DragCreateLinkTool(document, ToolMode.LINK_GENERALIZATION, pointerTargetingService));
        toolManager.registerTool(ToolMode.LINK_COMPOSITION, new DragCreateLinkTool(document, ToolMode.LINK_COMPOSITION, pointerTargetingService));

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

    public boolean groupSelected() {
        if (!canGroupSelected()) {
            return false;
        }

        List<DiagramElement> groupable = getSelectedElements();

        for (DiagramElement element : groupable) {
            document.removeElement(element);
            element.setSelected(false);
            element.setHovered(false);
        }

        Composite composite = new Composite(groupable);
        document.addElement(composite);

        for (DiagramElement element : document.getElements()) {
            element.setSelected(element == composite);
        }
        return true;
    }

    public boolean ungroupSelected() {
        if (!canUngroupSelected()) {
            return false;
        }

        Composite composite = (Composite) getSelectedElements().get(0);

        document.removeElement(composite);
        List<DiagramElement> children = composite.releaseChildren();
        for (DiagramElement child : children) {
            child.setSelected(false);
            child.setHovered(false);
            document.addElement(child);
        }
        return true;
    }

    public boolean canGroupSelected() {
        List<DiagramElement> selected = getSelectedElements();
        if (selected.size() < 2) {
            return false;
        }

        for (DiagramElement element : selected) {
            if (element instanceof Link) {
                return false;
            }
        }

        return true;
    }

    public boolean canUngroupSelected() {
        List<DiagramElement> selected = getSelectedElements();
        return selected.size() == 1 && selected.get(0) instanceof Composite;
    }

    public boolean canEditLabelSelection() {
        return getSingleSelectedBasicNode() != null;
    }

    public LabelEditState getSelectedBasicNodeLabelState() {
        if (!canEditLabelSelection()) {
            return null;
        }

        Node node = getSingleSelectedBasicNode();
        if (node == null) {
            return null;
        }
        return new LabelEditState(node.getLabelText(), node.getFillColor());
    }

    public boolean updateSelectedBasicNodeLabel(String text, Color fillColor) {
        Node node = getSingleSelectedBasicNode();
        if (node == null || text == null || fillColor == null) {
            return false;
        }

        node.setLabelText(text);
        node.setFillColor(fillColor);
        return true;
    }

    public void drawToolOverlay(Graphics2D g2) {
        getCurrentTool().drawOverlay(g2);
    }

    private List<DiagramElement> getSelectedElements() {
        List<DiagramElement> selected = new ArrayList<>();
        for (DiagramElement element : document.getElements()) {
            if (element.isSelected()) {
                selected.add(element);
            }
        }
        return selected;
    }

    private Node getSingleSelectedBasicNode() {
        List<DiagramElement> selected = getSelectedElements();
        if (selected.size() != 1) {
            return null;
        }

        DiagramElement element = selected.get(0);
        if (!(element instanceof Node node)) {
            return null;
        }
        return node;
    }

    private Tool getCurrentTool() {
        Tool tool = toolManager.getCurrentTool();
        if (tool == null) {
            throw new IllegalStateException("Current tool is not registered");
        }
        return tool;
    }
}


