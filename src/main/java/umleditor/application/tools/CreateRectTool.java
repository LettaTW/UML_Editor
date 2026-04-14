package umleditor.application.tools;

import umleditor.domain.DiagramDocument;
import umleditor.enumtype.ToolMode;

public class CreateRectTool extends DragCreateNodeTool {
    public CreateRectTool(DiagramDocument model) {
        super(model, ToolMode.CREATE_RECT);
    }
}

