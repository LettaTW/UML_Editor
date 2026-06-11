package umleditor.application.service;

import umleditor.domain.DiagramDocument;
import umleditor.domain.BaseElement;
import umleditor.domain.model.Port;

import java.awt.Rectangle;
import java.util.List;

public class TransformService implements ElementTransformService {
    private final DiagramDocument model;

    public TransformService(DiagramDocument model) {
        this.model = model;
    }

    @Override
    public void applyMove(BaseElement element, int dx, int dy) {
        if (element == null || (dx == 0 && dy == 0)) {
            return;
        }

        element.moveBy(dx, dy);
        model.notifyElementUpdated(element);

        // notify Link Node Moved
        for (String movedNodeId : element.collectOwnedNodeIds()) {
            notifyLinkNodeMoved(movedNodeId, dx, dy);
        }
    }

    @Override
    public void applyResize(BaseElement element, Rectangle bounds) {
        if (element == null || bounds == null) {
            return;
        }

        element.resizeTo(bounds);
        model.notifyElementUpdated(element);

        String reshapedNodeId = element.getID();
        List<Port> ports = element.getPorts();
        for (BaseElement e : model.getElements()) {
            e.onNodeReshaped(reshapedNodeId, ports);
        }
    }

    private void notifyLinkNodeMoved(String movedNodeId, int dx, int dy) {
        for (BaseElement e : model.getElements()) {
            e.onNodeMoved(movedNodeId, dx, dy);
        }
    }
}