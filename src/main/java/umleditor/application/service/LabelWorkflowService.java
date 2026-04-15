package umleditor.application.service;

import umleditor.domain.node.Node;

import java.awt.Color;

public class LabelWorkflowService {
    private final SelectionQueryService selectionQueryService;

    public LabelWorkflowService(SelectionQueryService selectionQueryService) {
        this.selectionQueryService = selectionQueryService;
    }

    public boolean canEditLabelSelection() {
        return selectionQueryService.getSingleSelectedNode() != null;
    }

    public Node getSingleSelectedNode() {
        return selectionQueryService.getSingleSelectedNode();
    }

    public boolean updateSelectedLabel(String text, Color fillColor) {
        Node node = selectionQueryService.getSingleSelectedNode();
        if (node == null || text == null || fillColor == null) {
            return false;
        }

        node.setLabelText(text);
        node.setFillColor(fillColor);
        return true;
    }
}

