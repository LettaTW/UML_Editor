package umleditor.domain.link;

import umleditor.domain.BaseElement;
import umleditor.domain.capability.NodeTransformReactable;
import umleditor.domain.model.Port;

import java.awt.*;
import java.awt.geom.Line2D;

public abstract class Link extends BaseElement {
    protected Port sourcePort;
    protected Port targetPort;

    protected Link(Port sourcePort, Port targetPort) {
        this.sourcePort = sourcePort;
        this.targetPort = targetPort;
    }

    public Port getSourcePort() {
        return sourcePort;
    }

    public Port getTargetPort() {
        return targetPort;
    }

    @Override
    public int getRenderPriority() {
        return 2;
    }

    @Override
    public boolean isDraggable() {
        return false;
    }

    @Override
    public boolean isGroupable() {
        return false;
    }

    @Override
    public void moveBy(int dx, int dy) {
        // Link position is determined by Ports.
        // When Node moves, Port updates automatically. No implementation needed here.
    }

    @Override
    public Rectangle getBounds() {
        int x = Math.min(sourcePort.getX(), targetPort.getX());
        int y = Math.min(sourcePort.getY(), targetPort.getY());
        int width = Math.abs(sourcePort.getX() - targetPort.getX());
        int height = Math.abs(sourcePort.getY() - targetPort.getY());
        return new Rectangle(x, y, width, height);
    }

    @Override
    public boolean contains(Point p) {
        return Line2D.ptSegDist(sourcePort.getX(), sourcePort.getY(), targetPort.getX(), targetPort.getY(), p.x, p.y) <= 5.0;
    }

    protected void drawBaseLine(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.drawLine(sourcePort.getX(), sourcePort.getY(), targetPort.getX(), targetPort.getY());
    }

    protected double getLineAngle() {
        return Math.atan2(targetPort.getY() - sourcePort.getY(), targetPort.getX() - sourcePort.getX());
    }
}