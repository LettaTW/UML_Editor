package umleditor.application.tools;

import umleditor.domain.DiagramElement;
import umleditor.domain.DiagramDocument;
import umleditor.application.service.PointerTargetingService;
import umleditor.application.service.ResizeGestureService;
import umleditor.application.service.ElementTransformService;
import umleditor.domain.link.Link;
import umleditor.domain.model.Port;

import java.awt.*;
import java.util.List;

import static umleditor.config.EditorDefaults.DEFAULT_SELECTION_BOX_FILL_COLOR;
import static umleditor.config.EditorDefaults.DEFAULT_SELECTION_BOX_STROKE_COLOR;
import static umleditor.config.EditorDefaults.MIN_NODE_SIZE;

public class SelectTool implements Tool {
    private final DiagramDocument model;
    private final PointerTargetingService pointerTargetingService;
    private final ResizeGestureService resizeGestureService;
    private final ElementTransformService elementTransformService;
    private Point dragStart;
    private Point dragCurrent;
    private Point lastDragPoint;
    private ResizeGestureService.ResizeSession resizeSession;
    private DiagramElement movingElement;
    private DiagramElement resizingElement;
    private boolean marqueeActive;
    private boolean marqueeClearedSelection;

    public SelectTool(
            DiagramDocument model,
            PointerTargetingService pointerTargetingService,
            ResizeGestureService resizeGestureService,
            ElementTransformService elementTransformService
    ) {
        this.model = model;
        this.pointerTargetingService = pointerTargetingService;
        this.resizeGestureService = resizeGestureService;
        this.elementTransformService = elementTransformService;
    }

    @Override
    public void mousePressed(Point p) {
        dragStart = p;
        dragCurrent = p;
        movingElement = null;
        resizingElement = null;
        lastDragPoint = null;
        resizeSession = null;

        PointerTargetingService.PortHit portHit = pointerTargetingService.findTopPortHitAt(p);
        if (portHit != null) {
            DiagramElement owner = portHit.owner();
            Port pressedPort = portHit.port();
            selectSingle(owner);

            if (!owner.getPorts().isEmpty()) {
                resizingElement = owner;
                resizeSession = resizeGestureService.beginSession(owner, pressedPort);
            }

            marqueeActive = false;
            marqueeClearedSelection = false;
            return;
        }

        DiagramElement hit = pointerTargetingService.findTopElementAt(p);
        if (hit != null) {
            marqueeActive = false;
            marqueeClearedSelection = false;
            selectSingle(hit);
            if (!(hit instanceof Link)) {
                movingElement = hit;
                lastDragPoint = p;
            }
            return;
        }

        clearSelection();
        marqueeActive = true;
        marqueeClearedSelection = false;
    }

    @Override
    public void mouseDragged(Point p) {
        if (resizingElement != null && resizeSession != null) {
            Rectangle resizedBounds = resizeGestureService.computeResizedBounds(
                    resizingElement,
                    resizeSession,
                    p,
                    MIN_NODE_SIZE
            );
            elementTransformService.applyResize(resizingElement, resizedBounds);
            return;
        }

        if (movingElement != null && lastDragPoint != null) {
            int dx = p.x - lastDragPoint.x;
            int dy = p.y - lastDragPoint.y;
            elementTransformService.applyMove(movingElement, dx, dy);
            lastDragPoint = p;
            return;
        }

        if (!marqueeActive || dragStart == null) {
            return;
        }

        dragCurrent = p;
        if (!marqueeClearedSelection) {
            clearSelection();
            marqueeClearedSelection = true;
        }
    }

    @Override
    public void mouseMoved(Point p) {
        if (marqueeActive) {
            return;
        }

        pointerTargetingService.applyHoverAt(p);
    }

    @Override
    public boolean mouseReleased(Point p) {
        if (resizingElement != null) {
            resizingElement = null;
            resizeSession = null;
            movingElement = null;
            lastDragPoint = null;
            dragStart = null;
            dragCurrent = null;
            return false;
        }

        if (movingElement != null) {
            movingElement = null;
            lastDragPoint = null;
            resizeSession = null;
            dragStart = null;
            dragCurrent = null;
            return false;
        }

        if (!marqueeActive || dragStart == null) {
            dragStart = null;
            dragCurrent = null;
            return false;
        }

        dragCurrent = p;
        Rectangle selectionBox = buildNormalizedRect(dragStart, dragCurrent);
        if (selectionBox.width > 0 || selectionBox.height > 0) {
            selectByBox(selectionBox);
        }

        marqueeActive = false;
        marqueeClearedSelection = false;
        movingElement = null;
        resizingElement = null;
        lastDragPoint = null;
        resizeSession = null;
        dragStart = null;
        dragCurrent = null;
        return false;
    }

    @Override
    public void drawOverlay(Graphics2D g2) {
        if (!marqueeActive || dragStart == null || dragCurrent == null) {
            return;
        }

        Rectangle r = buildNormalizedRect(dragStart, dragCurrent);
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

    private void selectSingle(DiagramElement target) {
        for (DiagramElement element : model.getElements()) {
            element.setSelected(element == target);
        }
        model.bringToFront(target);
    }

    private void selectByBox(Rectangle box) {
        boolean anySelected = false;
        List<DiagramElement> elements = model.getElements();

        for (DiagramElement element : elements) {
            boolean selected = !(element instanceof Link) && box.contains(element.getBounds());
            element.setSelected(selected);
            if (selected) {
                anySelected = true;
            }
        }

        if (!anySelected) {
            clearSelection();
        }
    }

    private void clearSelection() {
        for (DiagramElement element : model.getElements()) {
            element.setSelected(false);
        }
    }


    private Rectangle buildNormalizedRect(Point a, Point b) {
        int x = Math.min(a.x, b.x);
        int y = Math.min(a.y, b.y);
        int width = Math.abs(a.x - b.x);
        int height = Math.abs(a.y - b.y);
        return new Rectangle(x, y, width, height);
    }
}

