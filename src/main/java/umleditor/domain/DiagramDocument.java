package umleditor.domain;

import umleditor.domain.link.Link;
import umleditor.domain.node.Block;
import umleditor.domain.node.Composite;
import umleditor.domain.node.Node;
import umleditor.enumtype.ToolMode;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static umleditor.config.EditorDefaults.MAX_DEPTH;
import static umleditor.config.EditorDefaults.MIN_DEPTH;

public class DiagramDocument {
    private final List<Block> blocks = new ArrayList<>();
    private final List<Link> links = new ArrayList<>();
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

    public void addBlock(Block block) {
        if (block == null) {
            return;
        }
        blocks.add(block);
        bringToFront(block);
        notifyObservers(DocumentEvent.elementAdded(block));
    }

    public void addBlockPreserveDepth(Block block) {
        if (block == null) {
            return;
        }
        blocks.add(block);
        notifyObservers(DocumentEvent.elementAdded(block));
    }

    public boolean removeBlock(Block block) {
        boolean removed = blocks.remove(block);
        if (removed) {
            notifyObservers(DocumentEvent.elementRemoved(block));
        }
        return removed;
    }

    public List<Block> getBlocks() {
        return Collections.unmodifiableList(blocks);
    }

    public void addLink(Link link) {
        if (link == null) {
            return;
        }
        links.add(link);
        bringToFront(link);
        notifyObservers(DocumentEvent.elementAdded(link));
    }

    public void addLinkPreserveDepth(Link link) {
        if (link == null) {
            return;
        }
        links.add(link);
        notifyObservers(DocumentEvent.elementAdded(link));
    }

    public boolean removeLink(Link link) {
        boolean removed = links.remove(link);
        if (removed) {
            notifyObservers(DocumentEvent.elementRemoved(link));
        }
        return removed;
    }

    public List<Link> getLinks() {
        return Collections.unmodifiableList(links);
    }

    public void addElement(BaseElement element) {
        Link link = asLink(element);
        if (link != null) {
            addLink(link);
            return;
        }

        Block block = asBlock(element);
        if (block != null) {
            addBlock(block);
        }
    }

    public void addElementPreserveDepth(BaseElement element) {
        Link link = asLink(element);
        if (link != null) {
            addLinkPreserveDepth(link);
            return;
        }

        Block block = asBlock(element);
        if (block != null) {
            addBlockPreserveDepth(block);
        }
    }

    public void removeElement(BaseElement element) {
        Link link = asLink(element);
        if (link != null) {
            removeLink(link);
            return;
        }

        Block block = asBlock(element);
        if (block != null) {
            removeBlock(block);
        }

    }

    public List<BaseElement> getElements() {
        List<BaseElement> elements = new ArrayList<>(blocks.size() + links.size());
        elements.addAll(blocks);
        elements.addAll(links);
        return Collections.unmodifiableList(elements);
    }

    public List<BaseElement> getElementsForRender() {
        List<BaseElement> ordered = new ArrayList<>(getElements());
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
        if (isCompositeElement(element) && element.isSelected()) {
            return 2;
        }
        if (isLinkElement(element)) {
            return 1;
        }
        return 0;
    }

    public void bringToFront(BaseElement target) {
        List<BaseElement> elements = getElements();
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
        if (target == null || !getElements().contains(target)) {
            return;
        }

        // Resolve MIN_DEPTH ties so isolated-front target is visually top-most.
        for (BaseElement element : getElements()) {
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

    public boolean isBlockElement(BaseElement element) {
        return element instanceof Block;
    }

    public boolean isLinkElement(BaseElement element) {
        return element instanceof Link;
    }

    public boolean isNodeElement(BaseElement element) {
        return element instanceof Node;
    }

    public boolean isCompositeElement(BaseElement element) {
        return element instanceof Composite;
    }

    public Block asBlock(BaseElement element) {
        return element instanceof Block block ? block : null;
    }

    public Link asLink(BaseElement element) {
        return element instanceof Link link ? link : null;
    }

    public Node asNode(BaseElement element) {
        return element instanceof Node node ? node : null;
    }

    public Composite asComposite(BaseElement element) {
        return element instanceof Composite composite ? composite : null;
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


