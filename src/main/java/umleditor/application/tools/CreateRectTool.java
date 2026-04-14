package umleditor.application.tools;

import umleditor.application.service.DiagramModel;
import umleditor.enumtype.ToolMode;

public class CreateRectTool extends DragCreateNodeTool {
    public CreateRectTool(DiagramModel model) {
        super(model, ToolMode.CREATE_RECT);
    }
}

