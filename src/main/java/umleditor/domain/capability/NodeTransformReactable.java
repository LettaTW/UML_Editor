package umleditor.domain.capability;

import umleditor.domain.model.Port;

import java.util.List;

public interface NodeTransformReactable {
    void onNodeMoved(String nodeId, int dx, int dy);

    void onNodeReshaped(String nodeId, List<Port> ports);
}

