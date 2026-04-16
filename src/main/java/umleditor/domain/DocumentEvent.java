package umleditor.domain;

import umleditor.enumtype.ToolMode;

public record DocumentEvent(Type type, DiagramElement element, ToolMode toolMode) {
    public enum Type {
        ELEMENT_ADDED,
        ELEMENT_REMOVED,
        ELEMENT_UPDATED,
        SELECTION_CHANGED,
        HOVER_CHANGED,
        TOOL_CHANGED
    }

    public static DocumentEvent elementAdded(DiagramElement element) {
        return new DocumentEvent(Type.ELEMENT_ADDED, element, null);
    }

    public static DocumentEvent elementRemoved(DiagramElement element) {
        return new DocumentEvent(Type.ELEMENT_REMOVED, element, null);
    }

    public static DocumentEvent elementUpdated(DiagramElement element) {
        return new DocumentEvent(Type.ELEMENT_UPDATED, element, null);
    }

    public static DocumentEvent selectionChanged() {
        return new DocumentEvent(Type.SELECTION_CHANGED, null, null);
    }

    public static DocumentEvent hoverChanged() {
        return new DocumentEvent(Type.HOVER_CHANGED, null, null);
    }

    public static DocumentEvent toolChanged(ToolMode toolMode) {
        return new DocumentEvent(Type.TOOL_CHANGED, null, toolMode);
    }
}

