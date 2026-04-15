package umleditor.domain.link;

import umleditor.domain.model.Port;

import java.awt.*;
import java.awt.geom.Path2D;

public class GeneralizationLink extends Link {
    public GeneralizationLink(Port sourcePort, Port targetPort) {
        super(sourcePort, targetPort);
    }

    @Override
    public void draw(Graphics2D g2) {
        drawBaseLine(g2);

        double angle = getLineAngle();
        int size = 14;

        int tipX = targetPoint.x;
        int tipY = targetPoint.y;

        int baseX = (int) Math.round(tipX - size * Math.cos(angle));
        int baseY = (int) Math.round(tipY - size * Math.sin(angle));

        int leftX = (int) Math.round(baseX + (size * 0.6) * Math.cos(angle + Math.PI / 2));
        int leftY = (int) Math.round(baseY + (size * 0.6) * Math.sin(angle + Math.PI / 2));
        int rightX = (int) Math.round(baseX + (size * 0.6) * Math.cos(angle - Math.PI / 2));
        int rightY = (int) Math.round(baseY + (size * 0.6) * Math.sin(angle - Math.PI / 2));

        Path2D triangle = new Path2D.Double();
        triangle.moveTo(tipX, tipY);
        triangle.lineTo(leftX, leftY);
        triangle.lineTo(rightX, rightY);
        triangle.closePath();

        g2.setColor(Color.WHITE);
        g2.fill(triangle);
        g2.setColor(Color.BLACK);
        g2.draw(triangle);
    }
}

