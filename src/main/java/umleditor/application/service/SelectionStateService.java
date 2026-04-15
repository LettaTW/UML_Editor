package umleditor.application.service;

import umleditor.domain.DiagramDocument;
import umleditor.domain.DiagramElement;
import umleditor.domain.link.Link;

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
    }

    public void clearSelection() {
        for (DiagramElement element : document.getElements()) {
            element.setSelected(false);
        }
    }

    public void selectByBox(Rectangle box) {
        boolean anySelected = false;
        List<DiagramElement> elements = document.getElements();

        for (DiagramElement element : elements) {
            boolean selected = !(element instanceof Link) && box.contains(element.getBounds());
            element.setSelected(selected);
            if (selected) {
                anySelected = true;
            }
        }

        if (!anySelected) {
            clearSelection();
        }
    }
}

