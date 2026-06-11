package umleditor.application.service;

import umleditor.domain.DiagramDocument;
import umleditor.domain.BaseElement;
import umleditor.domain.node.Composite;

import java.util.ArrayList;
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
        List<BaseElement> selected = selectionQueryService.getSelectedElementsForRenderOrder();
        List<BaseElement> elementsToGroup = new ArrayList<>();
        for (BaseElement element : selected) {
            if (element.isGroupable()) {
                elementsToGroup.add(element);
            }
        }
        if (elementsToGroup.size() < 2) {
            return false;
        }


        int compositeDepth = findBackDepth(selected);
        for (BaseElement element : selected) {
            document.removeElement(element);
            element.setSelected(false);
            element.setHovered(false);
        }

        Composite composite = new Composite(selected);
        composite.setDepth(compositeDepth);
        document.addElementPreserveDepth(composite);

        for (BaseElement element : document.getElements()) {
            element.setSelected(element == composite);
        }
        document.notifySelectionChanged();
        return true;
    }

    public boolean canUngroupSelected() {
        return selectionQueryService.getSingleSelectedNode() != null;
    }

    public boolean ungroupSelected() {
        List<BaseElement> selected = selectionQueryService.getSelectedElements();
        if (selected.size() != 1) {
            return false;
        }
        BaseElement target = selected.get(0);
        List<BaseElement> children = target.ungroup();
        if (children.isEmpty()) {
            return false;
        }
        document.removeElement(target);

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

