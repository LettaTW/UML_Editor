package umleditor.application.service;

import umleditor.domain.DiagramDocument;
import umleditor.domain.DiagramElement;
import umleditor.domain.link.Link;
import umleditor.domain.model.Port;
import umleditor.domain.node.Block;
import umleditor.domain.node.Node;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class TransformService implements ElementTransformService {
    private final DiagramDocument model;

    public TransformService(DiagramDocument model) {
        this.model = model;
    }

    @Override
    public void applyMove(DiagramElement element, int dx, int dy) {
        if (element == null || (dx == 0 && dy == 0)) {
            return;
        }

        element.moveBy(dx, dy);
        model.notifyElementUpdated(element);

        for (String movedNodeId : collectMovedNodeIds(element)) {
            notifyLinkNodeMoved(movedNodeId, dx, dy);
        }
    }

    @Override
    public void applyResize(DiagramElement element, Rectangle bounds) {
        if (element == null || bounds == null) {
            return;
        }

        Node node = model.asNode(element);
        if (node == null) {
            return;
        }

        node.resizeTo(bounds);
        model.notifyElementUpdated(node);

        String reshapedNodeId = element.getID();
        List<Port> ports = node.getPorts();
        for (Link link : model.getLinks()) {
            link.onNodeReshaped(reshapedNodeId, ports);
        }
    }

    private void notifyLinkNodeMoved(String movedNodeId, int dx, int dy) {
        for (Link link : model.getLinks()) {
            link.onNodeMoved(movedNodeId, dx, dy);
        }
    }

    private List<String> collectMovedNodeIds(DiagramElement element) {
        Block block = model.asBlock(element);
        if (block != null) {
            return block.collectOwnedNodeIds();
        }

        return Collections.emptyList();
    }
}


