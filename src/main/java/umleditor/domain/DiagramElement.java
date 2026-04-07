package umleditor.domain;
import java.awt.*;

public interface DiagramElement {
    String getID();

    // about depth information (z-axis coordinates)
    int getZ();
    void setZ(int z);

    boolean isSelected();
    void setSelected(boolean selected);

    boolean isHovered();
    void setHovered(boolean hovered);

    void moveBy(int dx, int dy);
    Rectangle getBounds();
    // Whether a certain point falls within this object.
    boolean contains(Point p);
}
