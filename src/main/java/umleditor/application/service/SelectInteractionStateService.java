package umleditor.application.service;

import umleditor.domain.DiagramElement;

import java.awt.Point;

public class SelectInteractionStateService {
    private Point dragStart;
    private Point dragCurrent;
    private Point lastDragPoint;
    private ResizeService.ResizeSession resizeSession;
    private DiagramElement movingElement;
    private DiagramElement resizingElement;
    private boolean marqueeActive;
    private boolean marqueeClearedSelection;

    public void beginPointerDown(Point p) {
        dragStart = p;
        dragCurrent = p;
        movingElement = null;
        resizingElement = null;
        lastDragPoint = null;
        resizeSession = null;
        marqueeActive = false;
        marqueeClearedSelection = false;
    }

    public void beginResize(DiagramElement owner, ResizeService.ResizeSession session) {
        resizingElement = owner;
        resizeSession = session;
        movingElement = null;
        lastDragPoint = null;
        marqueeActive = false;
        marqueeClearedSelection = false;
    }

    public void beginMove(DiagramElement target, Point startPoint) {
        movingElement = target;
        lastDragPoint = startPoint;
        resizingElement = null;
        resizeSession = null;
        marqueeActive = false;
        marqueeClearedSelection = false;
    }

    public void beginMarquee() {
        marqueeActive = true;
        marqueeClearedSelection = false;
        movingElement = null;
        resizingElement = null;
        lastDragPoint = null;
        resizeSession = null;
    }

    public boolean isResizing() {
        return resizingElement != null && resizeSession != null;
    }

    public boolean isMoving() {
        return movingElement != null && lastDragPoint != null;
    }

    public boolean isMarqueeActive() {
        return marqueeActive;
    }

    public DiagramElement getResizingElement() {
        return resizingElement;
    }

    public ResizeService.ResizeSession getResizeSession() {
        return resizeSession;
    }

    public DiagramElement getMovingElement() {
        return movingElement;
    }

    public Point getLastDragPoint() {
        return lastDragPoint;
    }

    public void setLastDragPoint(Point p) {
        lastDragPoint = p;
    }

    public Point getDragStart() {
        return dragStart;
    }

    public Point getDragCurrent() {
        return dragCurrent;
    }

    public void setDragCurrent(Point p) {
        dragCurrent = p;
    }

    public boolean shouldClearSelectionForMarquee() {
        return !marqueeClearedSelection;
    }

    public void markMarqueeSelectionCleared() {
        marqueeClearedSelection = true;
    }

    public void clearInteraction() {
        dragStart = null;
        dragCurrent = null;
        movingElement = null;
        resizingElement = null;
        lastDragPoint = null;
        resizeSession = null;
        marqueeActive = false;
        marqueeClearedSelection = false;
    }
}

