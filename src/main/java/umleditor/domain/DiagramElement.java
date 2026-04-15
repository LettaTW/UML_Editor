package umleditor.domain;

import umleditor.domain.model.Port;

import java.awt.*;
import java.util.Collections;
import java.util.List;

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

    void draw(Graphics2D g2);

    default Port findPortAt(Point p) {
        return null;
    }

    default List<Port> getPorts() {
        return Collections.emptyList();
    }
}
