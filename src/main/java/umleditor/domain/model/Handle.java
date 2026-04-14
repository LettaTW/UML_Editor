package umleditor.domain.model;

import java.awt.*;

public interface Handle {
    Rectangle getBounds();
    boolean contains(Point p);
}
