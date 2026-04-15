package umleditor.application.tools;

import umleditor.domain.DiagramElement;
import umleditor.application.service.SelectionStateService;
import umleditor.application.service.PointerTargetingService;
import umleditor.application.service.ResizeService;
import umleditor.application.service.ElementTransformService;
import umleditor.application.service.SelectInteractionStateService;
import umleditor.domain.link.Link;
import umleditor.domain.model.Port;
import umleditor.domain.node.Node;

import java.awt.*;

import static umleditor.config.EditorDefaults.DEFAULT_SELECTION_BOX_FILL_COLOR;
import static umleditor.config.EditorDefaults.DEFAULT_SELECTION_BOX_STROKE_COLOR;
import static umleditor.config.EditorDefaults.MIN_NODE_SIZE;

public class SelectTool implements Tool {
    private final SelectionStateService selectionStateService;
    private final PointerTargetingService pointerTargetingService;
    private final ResizeService resizeService;
    private final ElementTransformService elementTransformService;
    private final SelectInteractionStateService interactionStateService;

    public SelectTool(
            SelectionStateService selectionStateService,
            PointerTargetingService pointerTargetingService,
            ResizeService resizeService,
            ElementTransformService elementTransformService,
            SelectInteractionStateService interactionStateService
    ) {
        this.selectionStateService = selectionStateService;
        this.pointerTargetingService = pointerTargetingService;
        this.resizeService = resizeService;
        this.elementTransformService = elementTransformService;
        this.interactionStateService = interactionStateService;
    }

    @Override
    public void mousePressed(Point p) {
        interactionStateService.beginPointerDown(p);

        PointerTargetingService.PortHit portHit = pointerTargetingService.findTopPortHitAt(p);
        if (portHit != null) {
            DiagramElement owner = portHit.owner();
            Port pressedPort = portHit.port();
            selectionStateService.selectSingle(owner);

            if (owner instanceof Node) {
                interactionStateService.beginResize(owner, resizeService.beginSession(owner, pressedPort));
            }
            return;
        }

        DiagramElement hit = pointerTargetingService.findTopElementAt(p);
        if (hit != null) {
            selectionStateService.selectSingle(hit);
            if (!(hit instanceof Link)) {
                interactionStateService.beginMove(hit, p);
            }
            return;
        }

        selectionStateService.clearSelection();
        interactionStateService.beginMarquee();
    }

    @Override
    public void mouseDragged(Point p) {
        if (interactionStateService.isResizing()) {
            Rectangle resizedBounds = resizeService.computeResizedBounds(
                    interactionStateService.getResizingElement(),
                    interactionStateService.getResizeSession(),
                    p,
                    MIN_NODE_SIZE
            );
            elementTransformService.applyResize(interactionStateService.getResizingElement(), resizedBounds);
            return;
        }

        if (interactionStateService.isMoving()) {
            Point lastDragPoint = interactionStateService.getLastDragPoint();
            int dx = p.x - lastDragPoint.x;
            int dy = p.y - lastDragPoint.y;
            elementTransformService.applyMove(interactionStateService.getMovingElement(), dx, dy);
            interactionStateService.setLastDragPoint(p);
            return;
        }

        if (!interactionStateService.isMarqueeActive() || interactionStateService.getDragStart() == null) {
            return;
        }

        interactionStateService.setDragCurrent(p);
        if (interactionStateService.shouldClearSelectionForMarquee()) {
            selectionStateService.clearSelection();
            interactionStateService.markMarqueeSelectionCleared();
        }
    }

    @Override
    public void mouseMoved(Point p) {
        if (interactionStateService.isMarqueeActive()) {
            return;
        }

        pointerTargetingService.applyHoverAt(p);
    }

    @Override
    public boolean mouseReleased(Point p) {
        if (interactionStateService.isResizing()) {
            interactionStateService.clearInteraction();
            return false;
        }

        if (interactionStateService.isMoving()) {
            interactionStateService.clearInteraction();
            return false;
        }

        if (!interactionStateService.isMarqueeActive() || interactionStateService.getDragStart() == null) {
            interactionStateService.clearInteraction();
            return false;
        }

        interactionStateService.setDragCurrent(p);
        Rectangle selectionBox = buildNormalizedRect(interactionStateService.getDragStart(), interactionStateService.getDragCurrent());
        if (selectionBox.width > 0 || selectionBox.height > 0) {
            selectionStateService.selectByBox(selectionBox);
        }

        interactionStateService.clearInteraction();
        return false;
    }

    @Override
    public void drawOverlay(Graphics2D g2) {
        if (!interactionStateService.isMarqueeActive()
                || interactionStateService.getDragStart() == null
                || interactionStateService.getDragCurrent() == null) {
            return;
        }

        Rectangle r = buildNormalizedRect(interactionStateService.getDragStart(), interactionStateService.getDragCurrent());
        if (r.width == 0 && r.height == 0) {
            return;
        }

        Stroke oldStroke = g2.getStroke();
        g2.setColor(DEFAULT_SELECTION_BOX_FILL_COLOR);
        g2.fillRect(r.x, r.y, r.width, r.height);
        g2.setColor(DEFAULT_SELECTION_BOX_STROKE_COLOR);
        g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4f, 4f}, 0));
        g2.drawRect(r.x, r.y, r.width, r.height);
        g2.setStroke(oldStroke);
    }


    private Rectangle buildNormalizedRect(Point a, Point b) {
        int x = Math.min(a.x, b.x);
        int y = Math.min(a.y, b.y);
        int width = Math.abs(a.x - b.x);
        int height = Math.abs(a.y - b.y);
        return new Rectangle(x, y, width, height);
    }
}

