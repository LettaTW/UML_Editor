package umleditor;

import umleditor.application.service.EditorController;
import umleditor.enumtype.ToolMode;
import umleditor.ui.EditorCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static umleditor.config.EditorDefaults.TOOLBAR_BUTTON_FONT_SIZE;
import static umleditor.config.EditorDefaults.TOOLBAR_BUTTON_HEIGHT;
import static umleditor.config.EditorDefaults.TOOLBAR_BUTTON_WIDTH;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::start);
    }

    private static void start() {
        EditorController controller = new EditorController();

        JFrame frame = new JFrame("UML Editor");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(900, 600);

        JPanel toolbar = new JPanel(new GridBagLayout());
        toolbar.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JButton selectButton = new JButton("Select");
        JButton associationButton = new JButton("Assoc");
        JButton generalizationButton = new JButton("Gen");
        JButton compositionButton = new JButton("Comp");
        JButton rectButton = new JButton("Rect");
        JButton ovalButton = new JButton("Oval");

        associationButton.setEnabled(false);
        generalizationButton.setEnabled(false);
        compositionButton.setEnabled(false);

        addToolRow(toolbar, 0, selectButton);
        addToolRow(toolbar, 1, associationButton);
        addToolRow(toolbar, 2, generalizationButton);
        addToolRow(toolbar, 3, compositionButton);
        addToolRow(toolbar, 4, rectButton);
        addToolRow(toolbar, 5, ovalButton);

        EditorCanvas canvas = new EditorCanvas(controller, () -> {});

        Runnable refreshToolbar = () -> {
            applyModeStyle(selectButton, controller.getCurrentToolMode() == ToolMode.SELECT);
            applyModeStyle(rectButton, controller.getCurrentToolMode() == ToolMode.CREATE_RECT);
            applyModeStyle(ovalButton, controller.getCurrentToolMode() == ToolMode.CREATE_OVAL);
        };

        canvas.setModeChangedCallback(refreshToolbar);

        selectButton.addActionListener(e -> {
            controller.setCurrentTool(ToolMode.SELECT);
            refreshToolbar.run();
        });

        attachCreateDragFromButton(rectButton, ToolMode.CREATE_RECT, controller, canvas, refreshToolbar);
        attachCreateDragFromButton(ovalButton, ToolMode.CREATE_OVAL, controller, canvas, refreshToolbar);

        frame.setLayout(new BorderLayout());
        frame.add(toolbar, BorderLayout.WEST);
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

    private static void addToolRow(JPanel toolbar, int row, JButton button) {
        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.gridx = 0;
        buttonConstraints.gridy = row;
        buttonConstraints.anchor = GridBagConstraints.WEST;
        buttonConstraints.insets = new Insets(5, 3, 5, 3);
        button.setPreferredSize(new Dimension(TOOLBAR_BUTTON_WIDTH, TOOLBAR_BUTTON_HEIGHT));
        button.setFont(button.getFont().deriveFont(Font.PLAIN, (float) TOOLBAR_BUTTON_FONT_SIZE));
        button.setFocusPainted(false);

        toolbar.add(button, buttonConstraints);
    }

    private static void attachCreateDragFromButton(
            JButton button,
            ToolMode mode,
            EditorController controller,
            EditorCanvas canvas,
            Runnable refreshToolbar
    ) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    controller.setTemporaryTool(mode);
                    refreshToolbar.run();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!SwingUtilities.isLeftMouseButton(e)) {
                    return;
                }

                Point releaseOnCanvas = SwingUtilities.convertPoint(button, e.getPoint(), canvas);
                if (canvas.contains(releaseOnCanvas)) {
                    controller.createDefaultNodeAt(mode, releaseOnCanvas.x, releaseOnCanvas.y);
                    canvas.repaint();
                }

                controller.restorePreviousTool();
                refreshToolbar.run();
            }
        });
    }
}
