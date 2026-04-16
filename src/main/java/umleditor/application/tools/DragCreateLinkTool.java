package umleditor.application.tools;

import umleditor.application.service.LinkCreationService;
import umleditor.application.service.PointerTargetingService;
import umleditor.domain.model.Port;
import umleditor.enumtype.ToolMode;

import java.awt.*;

import static umleditor.config.EditorDefaults.LINK_PORT_REVEAL_PROXIMITY_PX;

public class DragCreateLinkTool implements Tool {
    private final ToolMode mode;
    private final LinkCreationService linkCreationService;
    private final PointerTargetingService pointerTargetingService;

    private Port sourcePort;
    private Point currentPoint;

    public DragCreateLinkTool(
            ToolMode mode,
            LinkCreationService linkCreationService,
            PointerTargetingService pointerTargetingService
    ) {
        this.mode = mode;
        this.linkCreationService = linkCreationService;
        this.pointerTargetingService = pointerTargetingService;
    }

    @Override
    public void mousePressed(Point p) {
        sourcePort = pointerTargetingService.findTopPortAt(p);
        currentPoint = p;
        pointerTargetingService.applyLinkDragHoverAt(p, LINK_PORT_REVEAL_PROXIMITY_PX);
    }

    @Override
    public void mouseDragged(Point p) {
        if (sourcePort == null) {
            return;
        }
        currentPoint = p;
        pointerTargetingService.applyLinkDragHoverAt(p, LINK_PORT_REVEAL_PROXIMITY_PX);
    }

    @Override
    public void mouseMoved(Point p) {
        pointerTargetingService.applyLinkDragHoverAt(p, LINK_PORT_REVEAL_PROXIMITY_PX);
    }

    @Override
    public boolean mouseReleased(Point p) {
        if (sourcePort == null) {
            currentPoint = null;
            return false;
        }

        Port targetPort = pointerTargetingService.findTopPortAt(p);
        linkCreationService.createLink(mode, sourcePort, targetPort);

        sourcePort = null;
        currentPoint = null;
        pointerTargetingService.clearHover();
        return false;
    }

    @Override
    public void drawOverlay(Graphics2D g2) {
        if (sourcePort == null || currentPoint == null) {
            return;
        }

        Stroke oldStroke = g2.getStroke();
        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawLine(sourcePort.getX(), sourcePort.getY(), currentPoint.x, currentPoint.y);
        g2.setStroke(oldStroke);
    }
}

