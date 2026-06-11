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
        return selected.size() >= 2;
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
        BaseElement element = selectionQueryService.getSingleSelectedComposite();
        if (element == null) {
            return false;
        }

        document.removeElement(element);
        List<BaseElement> children = element.ungroup();
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

