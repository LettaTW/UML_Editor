package umleditor.application.service;

import umleditor.domain.DiagramDocument;
import umleditor.domain.DiagramElement;
import umleditor.domain.capability.NodeTransformReactable;
import umleditor.domain.model.Port;
import umleditor.domain.node.Composite;
import umleditor.domain.node.Node;

import java.awt.*;
import java.util.Collections;
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

        for (String movedNodeId : collectMovedNodeIds(element)) {
            notifyLinkNodeMoved(movedNodeId, dx, dy);
        }
    }

    @Override
    public void applyResize(DiagramElement element, Rectangle bounds) {
        if (element == null || bounds == null || !(element instanceof Node node)) {
            return;
        }

        node.resizeTo(bounds);

        String reshapedNodeId = element.getID();
        List<Port> ports = node.getPorts();
        for (DiagramElement candidate : model.getElements()) {
            if (candidate instanceof NodeTransformReactable reactable) {
                reactable.onNodeReshaped(reshapedNodeId, ports);
            }
        }
    }

    private void notifyLinkNodeMoved(String movedNodeId, int dx, int dy) {
        for (DiagramElement candidate : model.getElements()) {
            if (candidate instanceof NodeTransformReactable reactable) {
                reactable.onNodeMoved(movedNodeId, dx, dy);
            }
        }
    }

    private List<String> collectMovedNodeIds(DiagramElement element) {
        if (element instanceof Node node) {
            return List.of(node.getID());
        }

        if (element instanceof Composite composite) {
            return composite.collectOwnedNodeIds();
        }

        return Collections.emptyList();
    }
}

