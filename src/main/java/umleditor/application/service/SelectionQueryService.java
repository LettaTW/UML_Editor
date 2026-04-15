package umleditor.application.service;

import umleditor.domain.DiagramDocument;
import umleditor.domain.DiagramElement;
import umleditor.domain.node.Composite;
import umleditor.domain.node.Node;

import java.util.ArrayList;
import java.util.List;

public class SelectionQueryService {
    private final DiagramDocument document;

    public SelectionQueryService(DiagramDocument document) {
        this.document = document;
    }

    public List<DiagramElement> getSelectedElements() {
        List<DiagramElement> selected = new ArrayList<>();
        for (DiagramElement element : document.getElements()) {
            if (element.isSelected()) {
                selected.add(element);
            }
        }
        return selected;
    }

    public List<DiagramElement> getSelectedElementsForRenderOrder() {
        List<DiagramElement> selected = new ArrayList<>();
        for (DiagramElement element : document.getElementsForRender()) {
            if (element.isSelected()) {
                selected.add(element);
            }
        }
        return selected;
    }

    public Node getSingleSelectedNode() {
        List<DiagramElement> selected = getSelectedElements();
        if (selected.size() != 1) {
            return null;
        }

        DiagramElement element = selected.get(0);
        if (!(element instanceof Node node)) {
            return null;
        }
        return node;
    }

    public Composite getSingleSelectedComposite() {
        List<DiagramElement> selected = getSelectedElements();
        if (selected.size() != 1) {
            return null;
        }

        DiagramElement element = selected.get(0);
        if (!(element instanceof Composite composite)) {
            return null;
        }
        return composite;
    }
}

