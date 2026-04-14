package umleditor.enumtype;

import umleditor.domain.node.Node;
import umleditor.domain.node.Oval;
import umleditor.domain.node.Rect;

import java.awt.*;

public enum ToolMode {
    SELECT,
    CREATE_RECT {
        @Override
        public Node createNode(Rectangle bounds) {
            return new Rect(bounds.x, bounds.y, bounds.width, bounds.height);
        }

        @Override
        public void drawPreview(Graphics2D g2, Rectangle preview) {
            g2.drawRect(preview.x, preview.y, preview.width, preview.height);
        }
    },
    CREATE_OVAL {
        @Override
        public Node createNode(Rectangle bounds) {
            return new Oval(bounds.x, bounds.y, bounds.width, bounds.height);
        }

        @Override
        public void drawPreview(Graphics2D g2, Rectangle preview) {
            g2.drawOval(preview.x, preview.y, preview.width, preview.height);
        }
    },
    LINK_ASSOCIATION,
    LINK_GENERALIZATION,
    LINK_COMPOSITION;

    public boolean isCreateMode() {
        return this == CREATE_RECT || this == CREATE_OVAL;
    }

    public Node createNode(Rectangle bounds) {
        return null;
    }

    public void drawPreview(Graphics2D g2, Rectangle preview) {
        // non-creation modes have no preview
    }
}
