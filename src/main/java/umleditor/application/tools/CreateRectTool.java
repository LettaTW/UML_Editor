package umleditor.application.tools;

import umleditor.application.factory.NodeFactory;
import umleditor.domain.DiagramDocument;
import umleditor.enumtype.ToolMode;

public class CreateRectTool extends DragCreateNodeTool {
    public CreateRectTool(DiagramDocument model, NodeFactory nodeFactory) {
        super(model, ToolMode.CREATE_RECT, nodeFactory);
    }
}

