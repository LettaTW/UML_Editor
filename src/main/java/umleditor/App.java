package umleditor;

import umleditor.application.service.EditorController;
import umleditor.enumtype.ToolMode;
import umleditor.ui.EditorCanvas;

import javax.swing.*;
import java.awt.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::start);
    }

    private static void start() {
        EditorController controller = new EditorController();

        JFrame frame = new JFrame("UML Editor - Use Case A");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(900, 600);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton selectButton = new JButton("Select");
        JButton rectButton = new JButton("Rect");
        JButton ovalButton = new JButton("Oval");

        toolbar.add(selectButton);
        toolbar.add(rectButton);
        toolbar.add(ovalButton);

        Runnable refreshToolbar = () -> {
            applyModeStyle(selectButton, controller.getCurrentToolMode() == ToolMode.SELECT);
            applyModeStyle(rectButton, controller.getCurrentToolMode() == ToolMode.CREATE_RECT);
            applyModeStyle(ovalButton, controller.getCurrentToolMode() == ToolMode.CREATE_OVAL);
        };

        selectButton.addActionListener(e -> {
            controller.setCurrentTool(ToolMode.SELECT);
            refreshToolbar.run();
        });

        rectButton.addActionListener(e -> {
            controller.setTemporaryTool(ToolMode.CREATE_RECT);
            refreshToolbar.run();
        });

        ovalButton.addActionListener(e -> {
            controller.setTemporaryTool(ToolMode.CREATE_OVAL);
            refreshToolbar.run();
        });

        EditorCanvas canvas = new EditorCanvas(controller, refreshToolbar);

        frame.setLayout(new BorderLayout());
        frame.add(toolbar, BorderLayout.NORTH);
        frame.add(canvas, BorderLayout.CENTER);

        refreshToolbar.run();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void applyModeStyle(JButton button, boolean active) {
        button.setOpaque(true);
        button.setBorderPainted(false);
        if (active) {
            button.setBackground(Color.BLACK);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(UIManager.getColor("Button.background"));
            button.setForeground(UIManager.getColor("Button.foreground"));
        }
    }
}
