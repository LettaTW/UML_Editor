package umleditor.application.service;

import umleditor.domain.DiagramDocument;
import umleditor.domain.BaseElement;
import umleditor.domain.model.Port;

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

    public boolean isLinkElement(BaseElement element) {
        // 直接依賴 BaseElement 的多型判斷
        return element != null && element.isLink();
    }

    public PortHit findTopPortHitAt(Point p) {
        BaseElement topOwner = null;
        Port topPort = null;
        int topDepth = MAX_DEPTH + 1;


        for (BaseElement element : document.getElements()) {
            // findPortAt(p) if is not Node (like Link, Composite) default return null
            Port candidate = element.findPortAt(p);
            if (candidate == null) {
                continue;
            }

            int depth = element.getDepth();
            if (topPort == null || depth <= topDepth) {
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

        for (BaseElement element : document.getElements()) {
            if (!element.isNode()) {
                continue;
            }

            Rectangle nearArea = element.getBounds();
            nearArea.grow(proximityPx, proximityPx);
            if (!nearArea.contains(p)) {
                continue;
            }

            int depth = element.getDepth();
            if (topNode == null || depth <= topDepth) {
                topNode = element;
                topDepth = depth;
            }
        }

        return topNode;
    }

    private void applyHoverState(BaseElement hoverTarget) {

        for (BaseElement element : document.getElements()) {
            element.setHovered(element == hoverTarget);
        }

        document.notifyHoverChanged();
    }
}