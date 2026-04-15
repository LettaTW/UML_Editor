package umleditor.domain.link;

import umleditor.domain.model.Port;

import java.awt.*;

public class AssociationLink extends Link {
    public AssociationLink(Port sourcePort, Port targetPort) {
        super(sourcePort, targetPort);
    }

    @Override
    public void draw(Graphics2D g2) {
        drawBaseLine(g2);

        double angle = getLineAngle();
        int size = 12;

        int tipX = targetPoint.x;
        int tipY = targetPoint.y;

        int leftX = (int) Math.round(tipX - size * Math.cos(angle - Math.PI / 6));
        int leftY = (int) Math.round(tipY - size * Math.sin(angle - Math.PI / 6));
        int rightX = (int) Math.round(tipX - size * Math.cos(angle + Math.PI / 6));
        int rightY = (int) Math.round(tipY - size * Math.sin(angle + Math.PI / 6));

        Stroke oldStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(Color.BLACK);
        g2.drawLine(tipX, tipY, leftX, leftY);
        g2.drawLine(tipX, tipY, rightX, rightY);
        g2.setStroke(oldStroke);
    }
}


