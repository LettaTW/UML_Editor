package umleditor.application.service;

import umleditor.application.tools.CreateOvalTool;
import umleditor.application.tools.CreateRectTool;
import umleditor.application.tools.DragCreateLinkTool;
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
    public record LabelEditState(String text, Color fillColor) {
    }

    private final DiagramDocument document;
    private final ToolManager toolManager;
    private final NodeFactory nodeFactory;
    private final GroupService groupService;
    private final LabelWorkflowService labelWorkflowService;

    public EditorController() {
        this.document = new DiagramDocument();
        this.toolManager = new ToolManager();
        this.nodeFactory = new NodeFactory();
        SelectionQueryService selectionQueryService = new SelectionQueryService(document);
        this.groupService = new GroupService(document, selectionQueryService);
        this.labelWorkflowService = new LabelWorkflowService(selectionQueryService);
        SelectionStateService selectionStateService = new SelectionStateService(document);
        SelectInteractionStateService interactionStateService = new SelectInteractionStateService();
        LinkFactory linkFactory = new LinkFactory();
        PointerTargetingService pointerTargetingService = new PointerTargetingService(document);
        ResizeService resizeService = new ResizeService();
        ElementTransformService elementTransformService = new DefaultElementTransformService(document);

        toolManager.registerTool(
                ToolMode.SELECT,
                new SelectTool(selectionStateService, pointerTargetingService, resizeService, elementTransformService, interactionStateService)
        );
        toolManager.registerTool(ToolMode.CREATE_RECT, new CreateRectTool(document, nodeFactory));
        toolManager.registerTool(ToolMode.CREATE_OVAL, new CreateOvalTool(document, nodeFactory));
        toolManager.registerTool(ToolMode.LINK_ASSOCIATION, new DragCreateLinkTool(document, ToolMode.LINK_ASSOCIATION, linkFactory, pointerTargetingService));
        toolManager.registerTool(ToolMode.LINK_GENERALIZATION, new DragCreateLinkTool(document, ToolMode.LINK_GENERALIZATION, linkFactory, pointerTargetingService));
        toolManager.registerTool(ToolMode.LINK_COMPOSITION, new DragCreateLinkTool(document, ToolMode.LINK_COMPOSITION, linkFactory, pointerTargetingService));

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
        if (!nodeFactory.isCreateMode(mode)) {
            return;
        }
        // Center the new node at (x, y) by adjusting the top-left corner position
        int left = x - (DEFAULT_NODE_WIDTH / 2);
        int top = y - (DEFAULT_NODE_HEIGHT / 2);
        Rectangle bounds = new Rectangle(left, top, DEFAULT_NODE_WIDTH, DEFAULT_NODE_HEIGHT);
        Node createdNode = nodeFactory.createNode(mode, bounds);
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
        if (shouldRestore && nodeFactory.isCreateMode(toolManager.getCurrentMode())) {
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
        return groupService.groupSelected();
    }

    public boolean ungroupSelected() {
        return groupService.ungroupSelected();
    }

    public boolean canGroupSelected() {
        return groupService.canGroupSelected();
    }

    public boolean canUngroupSelected() {
        return groupService.canUngroupSelected();
    }

    public boolean canEditLabelSelection() {
        return labelWorkflowService.canEditLabelSelection();
    }

    public LabelEditState getSelectedBasicNodeLabelState() {
        if (!canEditLabelSelection()) {
            return null;
        }

        Node element = labelWorkflowService.getSingleSelectedNode();
        if (element == null) {
            return null;
        }
        return new LabelEditState(element.getLabelText(), element.getFillColor());
    }

    public boolean updateSelectedBasicNodeLabel(String text, Color fillColor) {
        return labelWorkflowService.updateSelectedLabel(text, fillColor);
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


