package umleditor.ui;

import umleditor.application.service.EditorController;
import umleditor.domain.DiagramElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EditorCanvas extends JPanel {
    private final EditorController controller;
    private Runnable interactionChangedCallback;

    public EditorCanvas(EditorController controller, Runnable interactionChangedCallback) {
        this.controller = controller;
        this.interactionChangedCallback = interactionChangedCallback;

        setBackground(Color.WHITE);

        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                controller.onMousePressed(e.getX(), e.getY(), e.getButton());
                notifyInteractionChanged();
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                controller.onMouseDragged(e.getX(), e.getY());
                notifyInteractionChanged();
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                controller.onMouseMoved(e.getX(), e.getY());
                notifyInteractionChanged();
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                controller.onMouseReleased(e.getX(), e.getY());
                notifyInteractionChanged();
                repaint();
            }
        };

        addMouseListener(adapter);
        addMouseMotionListener(adapter);
    }

    public void setInteractionChangedCallback(Runnable interactionChangedCallback) {
        this.interactionChangedCallback = interactionChangedCallback;
    }

    private void notifyInteractionChanged() {
        if (interactionChangedCallback != null) {
            interactionChangedCallback.run();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (DiagramElement element : controller.getElementsForRender()) {
            element.draw(g2);
        }
        controller.drawToolOverlay(g2);

        g2.dispose();
    }
}




