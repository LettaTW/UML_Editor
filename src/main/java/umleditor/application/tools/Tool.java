package umleditor.application.tools;

import java.awt.*;

public interface Tool {
    void mousePressed(Point p);

    void mouseDragged(Point p);

    default void mouseMoved(Point p) {
        // Default no-op for tools that do not use hover interactions.
    }

    boolean mouseReleased(Point p);

    void drawOverlay(Graphics2D g2);
}

