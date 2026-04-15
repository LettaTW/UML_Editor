package umleditor.domain.link;

import umleditor.domain.model.Port;

import java.awt.*;
import java.awt.geom.Path2D;

public class CompositionLink extends Link {
    public CompositionLink(Port sourcePort, Port targetPort) {
        super(sourcePort, targetPort);
    }

    @Override
    public void draw(Graphics2D g2) {
        drawBaseLine(g2);

        double angle = getLineAngle();
        int size = 12;

        int tipX = targetPoint.x;
        int tipY = targetPoint.y;

        int nearX = (int) Math.round(tipX - size * Math.cos(angle));
        int nearY = (int) Math.round(tipY - size * Math.sin(angle));

        int centerX = (int) Math.round(tipX - (size * 2.0) * Math.cos(angle));
        int centerY = (int) Math.round(tipY - (size * 2.0) * Math.sin(angle));

        int leftX = (int) Math.round(nearX + (size * 0.6) * Math.cos(angle + Math.PI / 2));
        int leftY = (int) Math.round(nearY + (size * 0.6) * Math.sin(angle + Math.PI / 2));
        int rightX = (int) Math.round(nearX + (size * 0.6) * Math.cos(angle - Math.PI / 2));
        int rightY = (int) Math.round(nearY + (size * 0.6) * Math.sin(angle - Math.PI / 2));

        Path2D diamond = new Path2D.Double();
        diamond.moveTo(tipX, tipY);
        diamond.lineTo(leftX, leftY);
        diamond.lineTo(centerX, centerY);
        diamond.lineTo(rightX, rightY);
        diamond.closePath();

        g2.setColor(Color.WHITE);
        g2.fill(diamond);
        g2.setColor(Color.BLACK);
        g2.draw(diamond);
    }
}

