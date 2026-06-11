package umleditor.application.service;

import umleditor.domain.BaseElement;
import java.util.List;

import java.awt.*;

public interface ElementTransformService {
    void applyMove(BaseElement element, int dx, int dy);
    void applyMove(List<BaseElement> elements, int dx, int dy);
    void applyResize(BaseElement element, Rectangle bounds);
}

