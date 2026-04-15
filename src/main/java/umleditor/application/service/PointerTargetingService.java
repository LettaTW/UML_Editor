package umleditor.application.service;

import umleditor.domain.DiagramDocument;
import umleditor.domain.DiagramElement;
import umleditor.domain.model.Port;

import java.awt.*;

import static umleditor.config.EditorDefaults.MAX_DEPTH;

public class PointerTargetingService {
    public record PortHit(DiagramElement owner, Port port) {
    }

    private final DiagramDocument document;

    public PointerTargetingService(DiagramDocument document) {
        this.document = document;
    }

    public DiagramElement findTopElementAt(Point p) {
        return document.findTopElementAt(p);
    }

    public Port findTopPortAt(Point p) {
        PortHit hit = findTopPortHitAt(p);
        return hit == null ? null : hit.port();
    }

    public PortHit findTopPortHitAt(Point p) {
        DiagramElement topOwner = null;
        Port topPort = null;
        int topDepth = MAX_DEPTH + 1;

        for (DiagramElement element : document.getElements()) {
            if (element.getPorts().isEmpty()) {
                continue;
            }

            Port candidate = element.findPortAt(p);
            if (candidate == null) {
                continue;
            }

            int depth = element.getDepth();
            if (topPort == null || depth < topDepth || depth == topDepth) {
                topOwner = element;
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
        DiagramElement hoverTarget = findTopElementAt(p);
        for (DiagramElement element : document.getElements()) {
            element.setHovered(element == hoverTarget);
        }
    }

    public void applyLinkDragHoverAt(Point p, int proximityPx) {
        DiagramElement hoverNode = findTopNodeNear(p, proximityPx);
        for (DiagramElement element : document.getElements()) {
            element.setHovered(element == hoverNode);
        }
    }

    public void clearHover() {
        for (DiagramElement element : document.getElements()) {
            element.setHovered(false);
        }
    }

    private DiagramElement findTopNodeNear(Point p, int proximityPx) {
        DiagramElement topNode = null;
        int topDepth = MAX_DEPTH + 1;

        for (DiagramElement element : document.getElements()) {
            if (element.getPorts().isEmpty()) {
                continue;
            }

            Rectangle nearArea = element.getBounds();
            nearArea.grow(proximityPx, proximityPx);
            if (!nearArea.contains(p)) {
                continue;
            }

            int depth = element.getDepth();
            if (topNode == null || depth < topDepth || depth == topDepth) {
                topNode = element;
                topDepth = depth;
            }
        }

        return topNode;
    }
}

