package umleditor.ui;

import umleditor.application.service.EditorController;
import umleditor.domain.DocumentEvent;
import umleditor.domain.DocumentObserver;

import javax.swing.SwingUtilities;

public class CanvasPanel extends EditorCanvas implements DocumentObserver {
    public CanvasPanel(EditorController controller) {
        super(controller);
        controller.addDocumentObserver(this);
    }

    @Override
    public void onDocumentChanged(DocumentEvent event) {
        SwingUtilities.invokeLater(this::repaint);
    }
}

