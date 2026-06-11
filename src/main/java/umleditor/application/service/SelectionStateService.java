package umleditor.application.service;

import umleditor.domain.DiagramDocument;
import umleditor.domain.BaseElement;

import java.awt.Rectangle;
import java.util.List;

public class SelectionStateService {
    private final DiagramDocument document;

    public SelectionStateService(DiagramDocument document) {
        this.document = document;
    }

    public void selectSingle(BaseElement target) {
        if (target == null) {
            clearSelection();
            return;
        }

        boolean alreadySelected = target.isSelected();
        for (BaseElement element : document.getElements()) {
            element.setSelected(element == target);
        }

        if (document.isCompositeElement(target)) {
            // Composite should be raised on every click, while keeping grouped internals isolated.
            document.bringToFrontIsolated(target);
        } else if (!alreadySelected) {
            // Avoid unnecessary depth churn when repeatedly clicking the same non-composite element.
            document.bringToFront(target);
        }
        document.notifySelectionChanged();
    }

    public void clearSelection() {
        for (BaseElement element : document.getElements()) {
            element.setSelected(false);
        }
        document.notifySelectionChanged();
    }

    public void selectByBox(Rectangle box) {
        boolean anySelected = false;
        List<BaseElement> elements = document.getElements();

        for (BaseElement element : elements) {
            boolean selected = !document.isLinkElement(element) && box.contains(element.getBounds());
            element.setSelected(selected);
            if (selected) {
                anySelected = true;
            }
        }

        if (!anySelected) {
            clearSelection();
            return;
        }

        document.notifySelectionChanged();
    }
}

