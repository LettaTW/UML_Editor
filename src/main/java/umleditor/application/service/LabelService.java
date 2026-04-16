package umleditor.application.service;

import umleditor.domain.DiagramDocument;
import umleditor.domain.node.Node;

import java.awt.Color;

public class LabelService {
    private final SelectionQueryService selectionQueryService;
    private final DiagramDocument document;

    public LabelService(DiagramDocument document, SelectionQueryService selectionQueryService) {
        this.document = document;
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
        document.notifyElementUpdated(node);
        return true;
    }
}



