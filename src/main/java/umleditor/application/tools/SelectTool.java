package umleditor.application.tools;

import java.awt.*;

public class SelectTool implements Tool {
    @Override
    public void mousePressed(Point p) {
        // Use Case A scope: selection behavior will be added in Use Case C.
    }

    @Override
    public void mouseDragged(Point p) {
        // Use Case A scope: no-op.
    }

    @Override
    public boolean mouseReleased(Point p) {
        return false;
    }

    @Override
    public void drawOverlay(Graphics2D g2) {
        // No preview in select mode.
    }
}

