package umleditor.application.service;

import umleditor.domain.BaseElement;

import java.awt.Point;
import java.util.List;

public class SelectInteractionStateService {
    private Point dragStart;
    private Point dragCurrent;
    private Point lastDragPoint;
    private ResizeService.ResizeSession resizeSession;

    // Changed from single BaseElement to List<BaseElement>
    private List<BaseElement> movingElements;
    private BaseElement resizingElement;

    private boolean marqueeActive;
    private boolean marqueeClearedSelection;

    public void beginPointerDown(Point p) {
        dragStart = p;
        dragCurrent = p;
        movingElements = null;
        resizingElement = null;
        lastDragPoint = null;
        resizeSession = null;
        marqueeActive = false;
        marqueeClearedSelection = false;
    }

    public void beginResize(BaseElement owner, ResizeService.ResizeSession session) {
        resizingElement = owner;
        resizeSession = session;
        movingElements = null;
        lastDragPoint = null;
        marqueeActive = false;
        marqueeClearedSelection = false;
    }

    // Now accepts a List of target elements
    public void beginMove(List<BaseElement> targets, Point startPoint) {
        movingElements = targets;
        lastDragPoint = startPoint;
        resizingElement = null;
        resizeSession = null;
        marqueeActive = false;
        marqueeClearedSelection = false;
    }

    public void beginMarquee() {
        marqueeActive = true;
        marqueeClearedSelection = false;
        movingElements = null;
        resizingElement = null;
        lastDragPoint = null;
        resizeSession = null;
    }

    public boolean isResizing() {
        return resizingElement != null && resizeSession != null;
    }

    public boolean isMoving() {
        // Check if list is not null and not empty
        return movingElements != null && !movingElements.isEmpty() && lastDragPoint != null;
    }

    public boolean isMarqueeActive() {
        return marqueeActive;
    }

    public BaseElement getResizingElement() {
        return resizingElement;
    }

    public ResizeService.ResizeSession getResizeSession() {
        return resizeSession;
    }

    // Return the list of moving elements
    public List<BaseElement> getMovingElements() {
        return movingElements;
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
        movingElements = null;
        resizingElement = null;
        lastDragPoint = null;
        resizeSession = null;
        marqueeActive = false;
        marqueeClearedSelection = false;
    }
}