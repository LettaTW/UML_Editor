package umleditor.application.service;

import umleditor.domain.DiagramDocument;
import umleditor.domain.DiagramElement;
import umleditor.domain.node.Block;
import umleditor.domain.node.Composite;

import java.util.List;

public class GroupService {
    private final DiagramDocument document;
    private final SelectionQueryService selectionQueryService;

    public GroupService(DiagramDocument document, SelectionQueryService selectionQueryService) {
        this.document = document;
        this.selectionQueryService = selectionQueryService;
    }

    public boolean canGroupSelected() {
        List<DiagramElement> selected = selectionQueryService.getSelectedElements();
        if (selected.size() < 2) {
            return false;
        }

        for (DiagramElement element : selected) {
            if (!(element instanceof Block)) {
                return false;
            }
        }

        return true;
    }

    public boolean groupSelected() {
        if (!canGroupSelected()) {
            return false;
        }

        List<DiagramElement> groupable = selectionQueryService.getSelectedElementsForRenderOrder();
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

    public boolean canUngroupSelected() {
        return selectionQueryService.getSingleSelectedComposite() != null;
    }

    public boolean ungroupSelected() {
        Composite composite = selectionQueryService.getSingleSelectedComposite();
        if (composite == null) {
            return false;
        }

        document.removeElement(composite);
        List<DiagramElement> children = composite.releaseChildren();
        for (DiagramElement child : children) {
            child.setSelected(false);
            child.setHovered(false);
            document.addElement(child);
        }
        return true;
    }
}

