package umleditor.domain;

import umleditor.domain.link.Link;
import umleditor.domain.node.Block;

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

    public void addBlock(Block block) {
        if (block == null) {
            return;
        }
        blocks.add(block);
        bringToFront(block);
    }

    public boolean removeBlock(Block block) {
        return blocks.remove(block);
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
    }

    public boolean removeLink(Link link) {
        return links.remove(link);
    }

    public List<Link> getLinks() {
        return Collections.unmodifiableList(links);
    }

    public void addElement(DiagramElement element) {
        if (element instanceof Link link) {
            addLink(link);
            return;
        }

        if (element instanceof Block block) {
            addBlock(block);
        }
    }

    public boolean removeElement(DiagramElement element) {
        if (element instanceof Link link) {
            return removeLink(link);
        }

        if (element instanceof Block block) {
            return removeBlock(block);
        }

        return false;
    }

    public List<DiagramElement> getElements() {
        List<DiagramElement> elements = new ArrayList<>(blocks.size() + links.size());
        elements.addAll(blocks);
        elements.addAll(links);
        return Collections.unmodifiableList(elements);
    }

    public List<DiagramElement> getElementsForRender() {
        List<DiagramElement> ordered = new ArrayList<>(getElements());
        ordered.sort(Comparator.comparingInt(DiagramElement::getDepth).reversed());
        return Collections.unmodifiableList(ordered);
    }

    public void bringToFront(DiagramElement target) {
        List<DiagramElement> elements = getElements();
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

        for (DiagramElement element : getElements()) {
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


