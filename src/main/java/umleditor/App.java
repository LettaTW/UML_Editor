package umleditor;

import umleditor.application.service.EditorController;
import umleditor.ui.MainFrame;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::start);
    }

    private static void start() {
        EditorController controller = new EditorController();
        MainFrame frame = new MainFrame(controller);
        frame.setVisible(true);
    }
}
