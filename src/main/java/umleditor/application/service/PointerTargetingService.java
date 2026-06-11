package umleditor.application.service;

import umleditor.domain.DiagramDocument;
import umleditor.domain.BaseElement;
import umleditor.domain.link.Link;
import umleditor.domain.model.Port;
import umleditor.domain.node.Block;
import umleditor.domain.node.Node;

import java.awt.*;

import static umleditor.config.EditorDefaults.MAX_DEPTH;

public class PointerTargetingService {
    public record PortHit(BaseElement owner, Port port) {
    }

    private final DiagramDocument document;

    public PointerTargetingService(DiagramDocument document) {
        this.document = document;
    }

    public BaseElement findTopElementAt(Point p) {
        return document.findTopElementAt(p);
    }

    public Port findTopPortAt(Point p) {
        PortHit hit = findTopPortHitAt(p);
        return hit == null ? null : hit.port();
    }

    public boolean isNodeElement(BaseElement element) {
        return document.isNodeElement(element);
    }

    public boolean isLinkElement(BaseElement element) {
        return document.isLinkElement(element);
    }

    public PortHit findTopPortHitAt(Point p) {
        BaseElement topOwner = null;
        Port topPort = null;
        int topDepth = MAX_DEPTH + 1;

        for (Block block : document.getBlocks()) {
            Node node = document.asNode(block);
            if (node == null) {
                continue;
            }

            Port candidate = node.findPortAt(p);
            if (candidate == null) {
                continue;
            }

            int depth = node.getDepth();
            if (topPort == null || depth <= topDepth) {
                topOwner = node;
                topPort = candidate;
                topDepth = depth;
            }
        }

        if (topOwner == null) {
            return null;
        }

        return new PortHit(topOwner, topPort);
    }

    public void applyHoverAt(Point p) {
        applyHoverState(findTopElementAt(p));
    }

    public void applyLinkDragHoverAt(Point p, int proximityPx) {
        applyHoverState(findTopNodeNear(p, proximityPx));
    }

    public void clearHover() {
        applyHoverState(null);
    }

    private BaseElement findTopNodeNear(Point p, int proximityPx) {
        BaseElement topNode = null;
        int topDepth = MAX_DEPTH + 1;

        for (Block block : document.getBlocks()) {
            Node node = document.asNode(block);
            if (node == null) {
                continue;
            }

            Rectangle nearArea = node.getBounds();
            nearArea.grow(proximityPx, proximityPx);
            if (!nearArea.contains(p)) {
                continue;
            }

            int depth = node.getDepth();
            if (topNode == null || depth <= topDepth) {
                topNode = node;
                topDepth = depth;
            }
        }

        return topNode;
    }

    private void applyHoverState(BaseElement hoverTarget) {
        for (Block block : document.getBlocks()) {
            block.setHovered(block == hoverTarget);
        }

        for (Link link : document.getLinks()) {
            link.setHovered(link == hoverTarget);
        }

        document.notifyHoverChanged();
    }
}

