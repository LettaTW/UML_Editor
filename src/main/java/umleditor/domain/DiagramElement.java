package umleditor.domain;

import java.awt.*;

public interface DiagramElement {
    String getID();

    // about depth information
    int getDepth();
    void setDepth(int depth);

    boolean isSelected();
    void setSelected(boolean selected);

    boolean isHovered();
    void setHovered(boolean hovered);

    void moveBy(int dx, int dy);

    Rectangle getBounds();
    boolean contains(Point p);
}
