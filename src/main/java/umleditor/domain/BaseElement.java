package umleditor.domain;

import umleditor.config.EditorDefaults;

import java.util.UUID;

public abstract class BaseElement implements DiagramElement {
    protected final String uuid;
    protected int Depth = EditorDefaults.DEFAULT_DEPTH;
    protected boolean selected;
    protected boolean hovered;

    protected BaseElement() {
        this.uuid = UUID.randomUUID().toString();
    }

    @Override
    public String getID() { return this.uuid; }

    @Override
    public int getDepth() { return this.Depth; }

    @Override
    public void setDepth(int depth) {
        this.Depth = depth;
    }

    @Override
    public boolean isSelected() {
        return this.selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean isHovered() {
        return this.hovered;
    }

    @Override
    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }
}
