package umleditor.application.service;

import umleditor.enumtype.ToolMode;

public class EditorState {
    private ToolMode currentMode = ToolMode.SELECT;
    private ToolMode previousMode = ToolMode.SELECT;

    public ToolMode getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(ToolMode mode) {
        this.currentMode = mode;
        this.previousMode = mode;
    }

    public void enterTemporaryMode(ToolMode temporaryMode) {
        this.previousMode = this.currentMode;
        this.currentMode = temporaryMode;
    }

    public void restorePreviousMode() {
        this.currentMode = this.previousMode;
    }
}

