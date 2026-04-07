package umleditor.domain.node;
import umleditor.domain.DiagramElement;
import java.awt.*;
import java.util.UUID;

public abstract class BaseNodeElement implements DiagramElement {
    protected final String uuid;
    protected int z; // z-axis coordinates
    protected boolean selected;
    protected boolean hovered;

    protected BaseNodeElement() {
        this.uuid = UUID.randomUUID().toString();
    }

    @Override
    public String getID() {
        return this.uuid;
    }

    @Override
    public int getZ() {
        return this.z;
    }

    @Override
    public void setZ(int z) {
        this.z = z;
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
