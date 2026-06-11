package umleditor.application.service;

import umleditor.domain.DiagramDocument;
import umleditor.domain.BaseElement;
import umleditor.domain.node.Composite;
import umleditor.domain.node.Node;

import java.util.ArrayList;
import java.util.List;

public class SelectionQueryService {
    private final DiagramDocument document;

    public SelectionQueryService(DiagramDocument document) {
        this.document = document;
    }

    public List<BaseElement> getSelectedElements() {
        List<BaseElement> selected = new ArrayList<>();
        for (BaseElement element : document.getElements()) {
            if (element.isSelected()) {
                selected.add(element);
            }
        }
        return selected;
    }

    public List<BaseElement> getSelectedElementsForRenderOrder() {
        List<BaseElement> selected = new ArrayList<>();
        for (BaseElement element : document.getElementsForRender()) {
            if (element.isSelected()) {
                selected.add(element);
            }
        }
        return selected;
    }

    public BaseElement getSingleSelectedNode() {
        List<BaseElement> selected = getSelectedElements();
        if (selected.size() != 1) {
            return null;
        }

        BaseElement element = selected.get(0);
        return element;
    }

    public BaseElement getSingleSelectedComposite() {
        List<BaseElement> selected = getSelectedElements();
        if (selected.size() != 1) {
            return null;
        }

        BaseElement element = selected.get(0);
        return element;
    }
}

