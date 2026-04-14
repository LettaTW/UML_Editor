package umleditor.application.tools;

import umleditor.domain.DiagramElement;
import umleditor.domain.DiagramDocument;
import umleditor.domain.node.Node;

import java.awt.*;
import java.util.List;

import static umleditor.config.EditorDefaults.DEFAULT_SELECTION_BOX_FILL_COLOR;
import static umleditor.config.EditorDefaults.DEFAULT_SELECTION_BOX_STROKE_COLOR;

public class SelectTool implements Tool {
    private final DiagramDocument model;
    private Point dragStart;
    private Point dragCurrent;
    private boolean marqueeActive;
    private boolean marqueeClearedSelection;

    public SelectTool(DiagramDocument model) {
        this.model = model;
    }

    @Override
    public void mousePressed(Point p) {
        dragStart = p;
        dragCurrent = p;

        DiagramElement hit = model.findTopElementAt(p);
        if (hit != null) {
            marqueeActive = false;
            marqueeClearedSelection = false;
            selectSingle(hit);
            return;
        }
        clearSelection();
        marqueeActive = true;
        marqueeClearedSelection = false;
    }

    @Override
    public void mouseDragged(Point p) {
        if (!marqueeActive || dragStart == null) {
            return;
        }

        dragCurrent = p;
        if (!marqueeClearedSelection) {
            clearSelection();
            marqueeClearedSelection = true;
        }
    }

    @Override
    public void mouseMoved(Point p) {
        if (marqueeActive) {
            return;
        }

        DiagramElement hoverTarget = model.findTopElementAt(p);
        for (DiagramElement element : model.getElements()) {
            element.setHovered(element == hoverTarget);
        }
    }

    @Override
    public boolean mouseReleased(Point p) {
        if (!marqueeActive || dragStart == null) {
            dragStart = null;
            dragCurrent = null;
            return false;
        }

        dragCurrent = p;
        Rectangle selectionBox = buildNormalizedRect(dragStart, dragCurrent);
        if (selectionBox.width > 0 || selectionBox.height > 0) {
            selectByBox(selectionBox);
        }

        marqueeActive = false;
        marqueeClearedSelection = false;
        dragStart = null;
        dragCurrent = null;
        return false;
    }

    @Override
    public void drawOverlay(Graphics2D g2) {
        if (!marqueeActive || dragStart == null || dragCurrent == null) {
            return;
        }

        Rectangle r = buildNormalizedRect(dragStart, dragCurrent);
        if (r.width == 0 && r.height == 0) {
            return;
        }

        Stroke oldStroke = g2.getStroke();
        g2.setColor(DEFAULT_SELECTION_BOX_FILL_COLOR);
        g2.fillRect(r.x, r.y, r.width, r.height);
        g2.setColor(DEFAULT_SELECTION_BOX_STROKE_COLOR);
        g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4f, 4f}, 0));
        g2.drawRect(r.x, r.y, r.width, r.height);
        g2.setStroke(oldStroke);
    }

    private void selectSingle(DiagramElement target) {
        for (DiagramElement element : model.getElements()) {
            element.setSelected(element == target);
        }
        model.bringToFront(target);
    }

    private void selectByBox(Rectangle box) {
        boolean anySelected = false;
        List<DiagramElement> elements = model.getElements();

        for (DiagramElement element : elements) {
            boolean selected = element instanceof Node && box.contains(element.getBounds());
            element.setSelected(selected);
            if (selected) {
                anySelected = true;
            }
        }

        if (!anySelected) {
            clearSelection();
        }
    }

    private void clearSelection() {
        for (DiagramElement element : model.getElements()) {
            element.setSelected(false);
        }
    }


    private Rectangle buildNormalizedRect(Point a, Point b) {
        int x = Math.min(a.x, b.x);
        int y = Math.min(a.y, b.y);
        int width = Math.abs(a.x - b.x);
        int height = Math.abs(a.y - b.y);
        return new Rectangle(x, y, width, height);
    }
}

