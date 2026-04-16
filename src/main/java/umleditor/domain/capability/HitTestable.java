package umleditor.domain.capability;

import java.awt.Point;
import java.awt.Rectangle;

public interface HitTestable {
    Rectangle getBounds();

    boolean contains(Point p);
}

