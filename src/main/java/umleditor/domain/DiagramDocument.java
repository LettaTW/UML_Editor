package umleditor.domain;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static umleditor.config.EditorDefaults.MAX_DEPTH;
import static umleditor.config.EditorDefaults.MIN_DEPTH;

public class DiagramDocument {
    private final List<DiagramElement> elements = new ArrayList<>();

    public void addElement(DiagramElement element) {
        elements.add(element);
        bringToFront(element);
    }

    public List<DiagramElement> getElements() {
        return Collections.unmodifiableList(elements);
    }

    public List<DiagramElement> getElementsForRender() {
        List<DiagramElement> ordered = new ArrayList<>(elements);
        ordered.sort(Comparator.comparingInt(DiagramElement::getDepth).reversed());
        return Collections.unmodifiableList(ordered);
    }

    public void bringToFront(DiagramElement target) {
        if (!elements.contains(target)) {
            return;
        }

        for (DiagramElement element : elements) {
            if (element == target) {
                continue;
            }
            element.setDepth(Math.min(MAX_DEPTH, element.getDepth() + 1));
        }

        target.setDepth(MIN_DEPTH);
    }

    public DiagramElement findTopElementAt(Point p) {
        DiagramElement topElement = null;
        int topDepth = MAX_DEPTH + 1;

        for (DiagramElement element : elements) {
            if (!element.contains(p)) {
                continue;
            }

            int depth = element.getDepth();
            if (topElement == null || depth < topDepth || depth == topDepth) {
                topElement = element;
                topDepth = depth;
            }
        }

        return topElement;
    }
}


