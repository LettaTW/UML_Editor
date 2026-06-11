package umleditor.application.service;

import umleditor.domain.DiagramDocument;
import umleditor.domain.BaseElement;
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
        List<BaseElement> selected = selectionQueryService.getSelectedElements();
        if (selected.size() < 2) {
            return false;
        }

        for (BaseElement element : selected) {
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

        List<BaseElement> groupable = selectionQueryService.getSelectedElementsForRenderOrder();
        int compositeDepth = findBackDepth(groupable);
        for (BaseElement element : groupable) {
            document.removeElement(element);
            element.setSelected(false);
            element.setHovered(false);
        }

        Composite composite = new Composite(groupable);
        composite.setDepth(compositeDepth);
        document.addElementPreserveDepth(composite);

        for (BaseElement element : document.getElements()) {
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
        List<BaseElement> children = composite.releaseChildrenWithAbsoluteDepth(composite.getDepth());
        for (BaseElement child : children) {
            child.setSelected(false);
            child.setHovered(false);
            document.addElementPreserveDepth(child);
        }
        document.notifySelectionChanged();
        return true;
    }

    private int findBackDepth(List<BaseElement> elements) {
        int maxDepth = Integer.MIN_VALUE;
        for (BaseElement element : elements) {
            maxDepth = Math.max(maxDepth, element.getDepth());
        }
        return maxDepth == Integer.MIN_VALUE ? MIN_DEPTH : maxDepth;
    }
}

