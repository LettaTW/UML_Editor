package umleditor.application.tools;

import umleditor.enumtype.ToolMode;

import java.util.EnumMap;
import java.util.Map;

public class ToolManager {
    private final Map<ToolMode, Tool> tools = new EnumMap<>(ToolMode.class);
    private ToolMode currentMode = ToolMode.SELECT;
    private ToolMode previousMode = ToolMode.SELECT;

    public void registerTool(ToolMode mode, Tool tool) {
        tools.put(mode, tool);
    }

    public void setTool(ToolMode mode) {
        this.currentMode = mode;
        this.previousMode = mode;
    }

    public void setTemporaryTool(ToolMode mode) {
        this.previousMode = this.currentMode;
        this.currentMode = mode;
    }

    public void restorePreviousTool() {
        this.currentMode = this.previousMode;
    }

    public Tool getCurrentTool() {
        return tools.get(currentMode);
    }

    public ToolMode getCurrentMode() {
        return currentMode;
    }
}

