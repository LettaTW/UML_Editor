package umleditor.application.service;

import umleditor.domain.DiagramElement;

import java.awt.*;

public interface ElementTransformService {
    void applyMove(DiagramElement element, int dx, int dy);

    void applyResize(DiagramElement element, Rectangle bounds);
}

