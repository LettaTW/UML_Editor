package umleditor.application.service;

import umleditor.domain.DiagramElement;
import umleditor.domain.model.Port;

import java.awt.*;

public class ResizeGestureService {
    public enum ResizeMode {
        CORNER,
        VERTICAL_EDGE,
        HORIZONTAL_EDGE
    }

    public record ResizeSession(Point anchor, ResizeMode mode) {
    }

    public ResizeSession beginSession(DiagramElement owner, Port pressedPort) {
        Rectangle bounds = owner.getBounds();
        int left = bounds.x;
        int right = bounds.x + bounds.width;
        int top = bounds.y;
        int bottom = bounds.y + bounds.height;

        int centerX = bounds.x + (bounds.width / 2);
        int centerY = bounds.y + (bounds.height / 2);

        Point pressed = new Point(pressedPort.getX(), pressedPort.getY());
        boolean nearCenterX = Math.abs(pressed.x - centerX) <= 1;
        boolean nearCenterY = Math.abs(pressed.y - centerY) <= 1;

        ResizeMode mode = ResizeMode.CORNER;
        if (nearCenterX && !nearCenterY) {
            mode = ResizeMode.VERTICAL_EDGE;
        } else if (nearCenterY && !nearCenterX) {
            mode = ResizeMode.HORIZONTAL_EDGE;
        }

        int anchorX;
        int anchorY;
        if (mode == ResizeMode.VERTICAL_EDGE) {
            anchorX = centerX;
            anchorY = (pressed.y <= centerY) ? bottom : top;
        } else if (mode == ResizeMode.HORIZONTAL_EDGE) {
            anchorX = (pressed.x <= centerX) ? right : left;
            anchorY = centerY;
        } else {
            anchorX = (pressed.x <= centerX) ? right : left;
            anchorY = (pressed.y <= centerY) ? bottom : top;
        }

        return new ResizeSession(new Point(anchorX, anchorY), mode);
    }

    public Rectangle computeResizedBounds(
            DiagramElement owner,
            ResizeSession session,
            Point current,
            int minNodeSize
    ) {
        Rectangle original = owner.getBounds();
        Point anchor = session.anchor();

        if (session.mode() == ResizeMode.VERTICAL_EDGE) {
            int newHeight = Math.max(minNodeSize, Math.abs(current.y - anchor.y));
            int newY = Math.min(current.y, anchor.y);
            return new Rectangle(original.x, newY, original.width, newHeight);
        }

        if (session.mode() == ResizeMode.HORIZONTAL_EDGE) {
            int newWidth = Math.max(minNodeSize, Math.abs(current.x - anchor.x));
            int newX = Math.min(current.x, anchor.x);
            return new Rectangle(newX, original.y, newWidth, original.height);
        }

        int newWidth = Math.max(minNodeSize, Math.abs(current.x - anchor.x));
        int newHeight = Math.max(minNodeSize, Math.abs(current.y - anchor.y));
        int newX = (current.x < anchor.x) ? (anchor.x - newWidth) : anchor.x;
        int newY = (current.y < anchor.y) ? (anchor.y - newHeight) : anchor.y;

        return new Rectangle(newX, newY, newWidth, newHeight);
    }
}

