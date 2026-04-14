package umleditor.application.tools;

import umleditor.application.service.DiagramModel;
import umleditor.enumtype.ToolMode;

public class CreateOvalTool extends DragCreateNodeTool {
    public CreateOvalTool(DiagramModel model) {
        super(model, ToolMode.CREATE_OVAL);
    }
}

