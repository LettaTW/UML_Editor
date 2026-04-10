package umleditor.domain.model;

import java.awt.*;

public interface Handle {
    void dragTo(int x, int y);

    Rectangle getBounds();
    boolean contains(Point p);
}
