package umleditor.ui;

import umleditor.application.service.EditorController;
import umleditor.domain.DiagramElement;
import umleditor.rendering.Renderable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EditorCanvas extends JPanel {
    private final EditorController controller;
    private Runnable modeChangedCallback;

    public EditorCanvas(EditorController controller, Runnable modeChangedCallback) {
        this.controller = controller;
        this.modeChangedCallback = modeChangedCallback;

        setBackground(Color.WHITE);

        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                controller.onMousePressed(e.getX(), e.getY(), e.getButton());
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                controller.onMouseDragged(e.getX(), e.getY());
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                controller.onMouseMoved(e.getX(), e.getY());
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                controller.onMouseReleased(e.getX(), e.getY());
                if (modeChangedCallback != null) {
                    modeChangedCallback.run();
                }
                repaint();
            }
        };

        addMouseListener(adapter);
        addMouseMotionListener(adapter);
    }

    public void setModeChangedCallback(Runnable modeChangedCallback) {
        this.modeChangedCallback = modeChangedCallback;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (DiagramElement element : controller.   getElementsForRender()) {
            if (element instanceof Renderable) {
                ((Renderable) element).draw(g2);
            }
        }
        controller.drawToolOverlay(g2);

        g2.dispose();
    }
}




