package umleditor.domain.link;

import umleditor.domain.BaseElement;
import umleditor.domain.model.Port;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.List;

public abstract class Link extends BaseElement {
    private final String sourceOwnerId;
    private final String targetOwnerId;
    protected Point sourcePoint;
    protected Point targetPoint;

    protected Link(Port sourcePort, Port targetPort) {
        this.sourceOwnerId = sourcePort.getOwnerId();
        this.targetOwnerId = targetPort.getOwnerId();
        this.sourcePoint = new Point(sourcePort.getX(), sourcePort.getY());
        this.targetPoint = new Point(targetPort.getX(), targetPort.getY());
    }

    public String getSourceOwnerId() {
        return sourceOwnerId;
    }

    public String getTargetOwnerId() {
        return targetOwnerId;
    }

    public void onNodeMoved(String nodeId, int dx, int dy) {
        if (sourceOwnerId.equals(nodeId)) {
            sourcePoint.translate(dx, dy);
        }
        if (targetOwnerId.equals(nodeId)) {
            targetPoint.translate(dx, dy);
        }
    }

    public void onNodeReshaped(String nodeId, List<Port> ports) {
        if (ports == null || ports.isEmpty()) {
            return;
        }

        if (sourceOwnerId.equals(nodeId)) {
            sourcePoint = nearestPortPoint(ports, sourcePoint);
        }
        if (targetOwnerId.equals(nodeId)) {
            targetPoint = nearestPortPoint(ports, targetPoint);
        }
    }

    @Override
    public void moveBy(int dx, int dy) {
        sourcePoint.translate(dx, dy);
        targetPoint.translate(dx, dy);
    }

    @Override
    public Rectangle getBounds() {
        int x = Math.min(sourcePoint.x, targetPoint.x);
        int y = Math.min(sourcePoint.y, targetPoint.y);
        int width = Math.abs(sourcePoint.x - targetPoint.x);
        int height = Math.abs(sourcePoint.y - targetPoint.y);
        return new Rectangle(x, y, width, height);
    }

    @Override
    public boolean contains(Point p) {
        return Line2D.ptSegDist(sourcePoint.x, sourcePoint.y, targetPoint.x, targetPoint.y, p.x, p.y) <= 5.0;
    }

    protected void drawBaseLine(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.drawLine(sourcePoint.x, sourcePoint.y, targetPoint.x, targetPoint.y);
    }

    protected double getLineAngle() {
        return Math.atan2(targetPoint.y - sourcePoint.y, targetPoint.x - sourcePoint.x);
    }

    private Point nearestPortPoint(List<Port> ports, Point reference) {
        Port nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Port port : ports) {
            double dx = port.getX() - reference.x;
            double dy = port.getY() - reference.y;
            double distance = (dx * dx) + (dy * dy);
            if (nearest == null || distance < minDistance) {
                nearest = port;
                minDistance = distance;
            }
        }

        if (nearest == null) {
            return new Point(reference);
        }
        return new Point(nearest.getX(), nearest.getY());
    }
}

