package umleditor.application.service;

import umleditor.domain.DiagramElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiagramModel {
    private final List<DiagramElement> elements = new ArrayList<>();

    public void addElement(DiagramElement element) {
        elements.add(element);
    }

    public List<DiagramElement> getElements() {
        return Collections.unmodifiableList(elements);
    }
}

