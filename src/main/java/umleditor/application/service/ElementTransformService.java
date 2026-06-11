package umleditor.application.service;

import umleditor.domain.BaseElement;

import java.awt.*;

public interface ElementTransformService {
    void applyMove(BaseElement element, int dx, int dy);

    void applyResize(BaseElement element, Rectangle bounds);
}

