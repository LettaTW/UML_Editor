package umleditor.ui;

import umleditor.application.service.EditorController;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;

public class MainFrame extends JFrame {
    public MainFrame(EditorController controller) {
        super("UML Editor");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(900, 600);

        CanvasPanel canvasPanel = new CanvasPanel(controller);
        ToolbarPanel toolbarPanel = new ToolbarPanel(controller, canvasPanel);
        EditorMenuBar editorMenuBar = new EditorMenuBar(controller, this);

        setJMenuBar(editorMenuBar);
        setLayout(new BorderLayout());
        add(toolbarPanel, BorderLayout.WEST);
        add(canvasPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
    }
}

