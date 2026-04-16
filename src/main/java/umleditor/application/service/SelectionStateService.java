package umleditor.application.service;

import umleditor.domain.DiagramDocument;
import umleditor.domain.DiagramElement;

import java.awt.Rectangle;
import java.util.List;

public class SelectionStateService {
    private final DiagramDocument document;

    public SelectionStateService(DiagramDocument document) {
        this.document = document;
    }

    public void selectSingle(DiagramElement target) {
        for (DiagramElement element : document.getElements()) {
            element.setSelected(element == target);
        }
        document.bringToFront(target);
        document.notifySelectionChanged();
    }

    public void clearSelection() {
        for (DiagramElement element : document.getElements()) {
            element.setSelected(false);
        }
        document.notifySelectionChanged();
    }

    public void selectByBox(Rectangle box) {
        boolean anySelected = false;
        List<DiagramElement> elements = document.getElements();

        for (DiagramElement element : elements) {
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

