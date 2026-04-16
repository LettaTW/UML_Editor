package umleditor.domain;

import umleditor.domain.capability.Drawable;
import umleditor.domain.capability.HitTestable;

public interface DiagramElement extends Drawable, HitTestable {
    String getID();

    // Depth semantics:
    // smaller value => visually on top, larger value => visually below.
    int getDepth();
    void setDepth(int depth);

    boolean isSelected();
    void setSelected(boolean selected);

    boolean isHovered();
    void setHovered(boolean hovered);

    void moveBy(int dx, int dy);
}
