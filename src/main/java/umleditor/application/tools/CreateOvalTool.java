package umleditor.application.tools;

import umleditor.application.factory.NodeFactory;
import umleditor.domain.DiagramDocument;
import umleditor.enumtype.ToolMode;

public class CreateOvalTool extends DragCreateNodeTool {
    public CreateOvalTool(DiagramDocument model, NodeFactory nodeFactory) {
        super(model, ToolMode.CREATE_OVAL, nodeFactory);
    }
}

