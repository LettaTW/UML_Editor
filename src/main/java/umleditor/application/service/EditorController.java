package umleditor.application.service;

import umleditor.application.factory.LinkFactory;
import umleditor.application.factory.NodeFactory;
import umleditor.application.tools.Tool;
import umleditor.application.factory.ToolFactory;
import umleditor.application.tools.ToolManager;
import umleditor.domain.DiagramElement;
import umleditor.domain.DiagramDocument;
import umleditor.domain.DocumentObserver;
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
    private final LabelService labelService;

    public EditorController() {
        this.document = new DiagramDocument();
        this.toolManager = new ToolManager();
        this.nodeFactory = new NodeFactory();
        SelectionQueryService selectionQueryService = new SelectionQueryService(document);
        this.groupService = new GroupService(document, selectionQueryService);
        this.labelService = new LabelService(document, selectionQueryService);
        ToolFactory toolFactory = getToolFactory();

        for (ToolMode mode : ToolMode.values()) {
            Tool tool = toolFactory.createTool(mode);
            if (tool != null) {
                toolManager.registerTool(mode, tool);
            }
        }

        toolManager.setTool(ToolMode.SELECT);
        document.notifyToolChanged(ToolMode.SELECT);
    }

    private ToolFactory getToolFactory() {
        SelectionStateService selectionStateService = new SelectionStateService(document);
        SelectInteractionStateService interactionStateService = new SelectInteractionStateService();
        LinkFactory linkFactory = new LinkFactory();
        LinkCreationService linkCreationService = new LinkCreationService(document, linkFactory);
        PointerTargetingService pointerTargetingService = new PointerTargetingService(document);
        ResizeService resizeService = new ResizeService();
        ElementTransformService elementTransformService = new TransformService(document);
        return new ToolFactory(
                document,
                nodeFactory,
                linkCreationService,
                selectionStateService,
                pointerTargetingService,
                resizeService,
                elementTransformService,
                interactionStateService
        );
    }

    public void setCurrentTool(ToolMode mode) {
        toolManager.setTool(mode);
        document.notifyToolChanged(mode);
    }

    public void setTemporaryTool(ToolMode mode) {
        toolManager.setTemporaryTool(mode);
        document.notifyToolChanged(mode);
    }

    public ToolMode getCurrentToolMode() {
        return toolManager.getCurrentMode();
    }

    public void restorePreviousTool() {
        toolManager.restorePreviousTool();
        document.notifyToolChanged(toolManager.getCurrentMode());
    }

    public void addDocumentObserver(DocumentObserver observer) {
        document.addObserver(observer);
    }

    public void removeDocumentObserver(DocumentObserver observer) {
        document.removeObserver(observer);
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

    public void groupSelected() {
        groupService.groupSelected();
    }

    public void ungroupSelected() {
        groupService.ungroupSelected();
    }

    public boolean canGroupSelected() {
        return groupService.canGroupSelected();
    }

    public boolean canUngroupSelected() {
        return groupService.canUngroupSelected();
    }

    public boolean canEditLabelSelection() {
        return labelService.canEditLabelSelection();
    }

    public LabelEditState getSelectedBasicNodeLabelState() {
        if (!canEditLabelSelection()) {
            return null;
        }

        Node element = labelService.getSingleSelectedNode();
        if (element == null) {
            return null;
        }
        return new LabelEditState(element.getLabelText(), element.getFillColor());
    }

    public void updateSelectedBasicNodeLabel(String text, Color fillColor) {
        labelService.updateSelectedLabel(text, fillColor);
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


