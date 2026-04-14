package umleditor.application.tools;

import java.awt.*;

public interface Tool {
    void mousePressed(Point p);

    void mouseDragged(Point p);

    boolean mouseReleased(Point p);

    void drawOverlay(Graphics2D g2);
}

