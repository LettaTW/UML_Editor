package umleditor.application.service;

import umleditor.domain.DiagramDocument;
import umleditor.domain.DiagramElement;
import umleditor.domain.node.Composite;

import java.util.List;

import static umleditor.config.EditorDefaults.MIN_DEPTH;

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
            if (!document.isBlockElement(element)) {
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
        int compositeDepth = findBackDepth(groupable);
        for (DiagramElement element : groupable) {
            document.removeElement(element);
            element.setSelected(false);
            element.setHovered(false);
        }

        Composite composite = new Composite(groupable);
        composite.setDepth(compositeDepth);
        document.addElementPreserveDepth(composite);

        for (DiagramElement element : document.getElements()) {
            element.setSelected(element == composite);
        }
        document.notifySelectionChanged();
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
        List<DiagramElement> children = composite.releaseChildrenWithAbsoluteDepth(composite.getDepth());
        for (DiagramElement child : children) {
            child.setSelected(false);
            child.setHovered(false);
            document.addElementPreserveDepth(child);
        }
        document.notifySelectionChanged();
        return true;
    }

    private int findBackDepth(List<DiagramElement> elements) {
        int maxDepth = Integer.MIN_VALUE;
        for (DiagramElement element : elements) {
            maxDepth = Math.max(maxDepth, element.getDepth());
        }
        return maxDepth == Integer.MIN_VALUE ? MIN_DEPTH : maxDepth;
    }
}

