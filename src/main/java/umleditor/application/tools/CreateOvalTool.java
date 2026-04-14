package umleditor.application.tools;

import umleditor.domain.DiagramDocument;
import umleditor.enumtype.ToolMode;

public class CreateOvalTool extends DragCreateNodeTool {
    public CreateOvalTool(DiagramDocument model) {
        super(model, ToolMode.CREATE_OVAL);
    }
}

