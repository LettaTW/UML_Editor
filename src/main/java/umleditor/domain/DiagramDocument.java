package umleditor.domain;

import umleditor.enumtype.ToolMode;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static umleditor.config.EditorDefaults.MAX_DEPTH;
import static umleditor.config.EditorDefaults.MIN_DEPTH;

public class DiagramDocument {
    private final List<BaseElement> elements = new ArrayList<>();
    private final List<DocumentObserver> observers = new ArrayList<>();

    public void addObserver(DocumentObserver observer) {
        if (observer == null || observers.contains(observer)) {
            return;
        }
        observers.add(observer);
    }

    public void removeObserver(DocumentObserver observer) {
        observers.remove(observer);
    }

    public void addElement(BaseElement element) {
        if (element == null) {
            return;
        }
        elements.add(element);
        bringToFront(element);
        notifyObservers(DocumentEvent.elementAdded(element));
    }

    public void addElementPreserveDepth(BaseElement element) {
        if (element == null) {
            return;
        }
        elements.add(element);
        notifyObservers(DocumentEvent.elementAdded(element));
    }

    public void removeElement(BaseElement element) {
        if (element == null) {
            return;
        }
        boolean removed = elements.remove(element);
        if (removed) {
            notifyObservers(DocumentEvent.elementRemoved(element));
        }
    }

    public List<BaseElement> getElements() {
        return Collections.unmodifiableList(elements);
    }

    public List<BaseElement> getElementsForRender() {
        List<BaseElement> ordered = new ArrayList<>(elements);
        // Depth rule: smaller depth is visually above larger depth.
        // Render order is back -> front, so larger depth draws first.
        // Link rule: links are above normal blocks.
        // Selected composite rule: selected composite is drawn above links,
        // so external links do not visually pass through the active group.
        ordered.sort(
                Comparator
                        .comparingInt(this::renderPriority)
                        .thenComparing(Comparator.comparingInt(BaseElement::getDepth).reversed())
        );
        return Collections.unmodifiableList(ordered);
    }

    private int renderPriority(BaseElement element) {
        if (element.isComposite() && element.isSelected()) {
            return 2;
        }
        if (element.isLink()) {
            return 1;
        }
        return 0;
    }

    public void bringToFront(BaseElement target) {
        if (!elements.contains(target)) {
            return;
        }

        int targetDepth = target.getDepth();
        if (targetDepth <= MIN_DEPTH) {
            return;
        }

        for (BaseElement element : elements) {
            if (element == target) {
                continue;
            }

            if (element.getDepth() < targetDepth) {
                element.setDepth(Math.min(MAX_DEPTH, element.getDepth() + 1));
            }
        }

        target.setDepth(MIN_DEPTH);
    }

    public void bringToFrontIsolated(BaseElement target) {
        if (target == null || !elements.contains(target)) {
            return;
        }

        // Resolve MIN_DEPTH ties so isolated-front target is visually top-most.
        for (BaseElement element : elements) {
            if (element == target) {
                continue;
            }
            if (element.getDepth() == MIN_DEPTH) {
                element.setDepth(Math.min(MAX_DEPTH, MIN_DEPTH + 1));
            }
        }

        target.setDepth(MIN_DEPTH);
    }

    public BaseElement findTopElementAt(Point p) {
        BaseElement topElement = null;
        for (BaseElement element : getElementsForRender()) {
            if (element.contains(p)) {
                // getElementsForRender() is back -> front, so the last hit is top-most.
                topElement = element;
            }
        }
        return topElement;
    }

    public void notifyElementUpdated(BaseElement element) {
        notifyObservers(DocumentEvent.elementUpdated(element));
    }

    public void notifySelectionChanged() {
        notifyObservers(DocumentEvent.selectionChanged());
    }

    public void notifyHoverChanged() {
        notifyObservers(DocumentEvent.hoverChanged());
    }

    public void notifyToolChanged(ToolMode toolMode) {
        notifyObservers(DocumentEvent.toolChanged(toolMode));
    }

    private void notifyObservers(DocumentEvent event) {
        for (DocumentObserver observer : observers) {
            observer.onDocumentChanged(event);
        }
    }
}