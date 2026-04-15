package umleditor.application.service;

import umleditor.domain.DiagramDocument;
import umleditor.domain.DiagramElement;
import umleditor.domain.link.Link;
import umleditor.domain.model.Port;
import umleditor.domain.node.Composite;
import umleditor.domain.node.Node;

import java.awt.*;
import java.util.List;

public class DefaultElementTransformService implements ElementTransformService {
    private final DiagramDocument model;

    public DefaultElementTransformService(DiagramDocument model) {
        this.model = model;
    }

    @Override
    public void applyMove(DiagramElement element, int dx, int dy) {
        if (element == null || (dx == 0 && dy == 0)) {
            return;
        }

        element.moveBy(dx, dy);

        if (element instanceof Composite composite) {
            for (String movedNodeId : composite.getContainedNodeIds()) {
                notifyLinkNodeMoved(movedNodeId, dx, dy);
            }
            return;
        }

        if (element.getPorts().isEmpty()) {
            return;
        }

        String movedNodeId = element.getID();
        notifyLinkNodeMoved(movedNodeId, dx, dy);
    }

    @Override
    public void applyResize(DiagramElement element, Rectangle bounds) {
        if (element == null || bounds == null || element.getPorts().isEmpty()) {
            return;
        }

        if (!(element instanceof Node node)) {
            return;
        }

        node.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);

        String reshapedNodeId = element.getID();
        List<Port> ports = element.getPorts();
        for (DiagramElement candidate : model.getElements()) {
            if (candidate instanceof Link link) {
                link.onNodeReshaped(reshapedNodeId, ports);
            }
        }
    }

    private void notifyLinkNodeMoved(String movedNodeId, int dx, int dy) {
        for (DiagramElement candidate : model.getElements()) {
            if (candidate instanceof Link link) {
                link.onNodeMoved(movedNodeId, dx, dy);
            }
        }
    }
}

