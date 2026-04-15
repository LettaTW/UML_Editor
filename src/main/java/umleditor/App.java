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
    private record EditMenuActions(JMenuBar menuBar, JMenuItem group, JMenuItem ungroup, JMenuItem label) {
    }

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

        addToolRow(toolbar, 0, selectButton);
        addToolRow(toolbar, 1, associationButton);
        addToolRow(toolbar, 2, generalizationButton);
        addToolRow(toolbar, 3, compositionButton);
        addToolRow(toolbar, 4, rectButton);
        addToolRow(toolbar, 5, ovalButton);

        EditorCanvas canvas = new EditorCanvas(controller, () -> {});

        EditMenuActions editMenuActions = createEditMenu(controller, canvas, frame);
        frame.setJMenuBar(editMenuActions.menuBar());

        Runnable refreshToolbar = () -> {
            applyModeStyle(selectButton, controller.getCurrentToolMode() == ToolMode.SELECT);
            applyModeStyle(associationButton, controller.getCurrentToolMode() == ToolMode.LINK_ASSOCIATION);
            applyModeStyle(generalizationButton, controller.getCurrentToolMode() == ToolMode.LINK_GENERALIZATION);
            applyModeStyle(compositionButton, controller.getCurrentToolMode() == ToolMode.LINK_COMPOSITION);
            applyModeStyle(rectButton, controller.getCurrentToolMode() == ToolMode.CREATE_RECT);
            applyModeStyle(ovalButton, controller.getCurrentToolMode() == ToolMode.CREATE_OVAL);
        };

        Runnable refreshUiState = () -> {
            refreshToolbar.run();
            refreshEditMenuState(controller, editMenuActions);
        };

        canvas.setInteractionChangedCallback(refreshUiState);

        selectButton.addActionListener(e -> {
            controller.setCurrentTool(ToolMode.SELECT);
            refreshUiState.run();
        });

        associationButton.addActionListener(e -> {
            controller.setCurrentTool(ToolMode.LINK_ASSOCIATION);
            refreshUiState.run();
        });

        generalizationButton.addActionListener(e -> {
            controller.setCurrentTool(ToolMode.LINK_GENERALIZATION);
            refreshUiState.run();
        });

        compositionButton.addActionListener(e -> {
            controller.setCurrentTool(ToolMode.LINK_COMPOSITION);
            refreshUiState.run();
        });

        attachCreateDragFromButton(rectButton, ToolMode.CREATE_RECT, controller, canvas, refreshUiState);
        attachCreateDragFromButton(ovalButton, ToolMode.CREATE_OVAL, controller, canvas, refreshUiState);

        frame.setLayout(new BorderLayout());
        frame.add(toolbar, BorderLayout.WEST);
        frame.add(canvas, BorderLayout.CENTER);

        refreshUiState.run();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static EditMenuActions createEditMenu(EditorController controller, EditorCanvas canvas, JFrame frame) {
        JMenuItem groupMenuItem = new JMenuItem("Group");
        JMenuItem ungroupMenuItem = new JMenuItem("Ungroup");
        JMenuItem labelMenuItem = new JMenuItem("Label");

        groupMenuItem.addActionListener(e -> {
            if (controller.groupSelected()) {
                canvas.repaint();
            }
            refreshEditMenuState(controller, groupMenuItem, ungroupMenuItem, labelMenuItem);
        });

        ungroupMenuItem.addActionListener(e -> {
            if (controller.ungroupSelected()) {
                canvas.repaint();
            }
            refreshEditMenuState(controller, groupMenuItem, ungroupMenuItem, labelMenuItem);
        });

        labelMenuItem.addActionListener(e -> {
            openLabelDialog(frame, controller, canvas);
            refreshEditMenuState(controller, groupMenuItem, ungroupMenuItem, labelMenuItem);
        });

        JMenu editMenu = new JMenu("Edit");
        editMenu.add(groupMenuItem);
        editMenu.add(ungroupMenuItem);
        editMenu.addSeparator();
        editMenu.add(labelMenuItem);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(editMenu);
        return new EditMenuActions(menuBar, groupMenuItem, ungroupMenuItem, labelMenuItem);
    }

    private static void refreshEditMenuState(EditorController controller, EditMenuActions editMenuActions) {
        refreshEditMenuState(controller, editMenuActions.group(), editMenuActions.ungroup(), editMenuActions.label());
    }

    private static void refreshEditMenuState(
            EditorController controller,
            JMenuItem groupMenuItem,
            JMenuItem ungroupMenuItem,
            JMenuItem labelMenuItem
    ) {
        boolean inSelectMode = controller.getCurrentToolMode() == ToolMode.SELECT;
        groupMenuItem.setEnabled(inSelectMode && controller.canGroupSelected());
        ungroupMenuItem.setEnabled(inSelectMode && controller.canUngroupSelected());
        labelMenuItem.setEnabled(inSelectMode && controller.canEditLabelSelection());
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
            Runnable refreshUiState
    ) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    controller.setTemporaryTool(mode);
                    refreshUiState.run();
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
                refreshUiState.run();
            }
        });
    }

    private static void openLabelDialog(JFrame frame, EditorController controller, EditorCanvas canvas) {
        EditorController.LabelEditState current = controller.getSelectedBasicNodeLabelState();
        if (current == null) {
            return;
        }

        JTextField nameField = new JTextField(current.text(), 24);
        JColorChooser colorChooser = new JColorChooser(current.fillColor());

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        JPanel namePanel = new JPanel(new BorderLayout(8, 0));
        namePanel.add(new JLabel("Label Name:"), BorderLayout.WEST);
        namePanel.add(nameField, BorderLayout.CENTER);
        panel.add(namePanel, BorderLayout.NORTH);
        panel.add(colorChooser, BorderLayout.CENTER);

        int option = JOptionPane.showConfirmDialog(
                frame,
                panel,
                "Customize Label",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        String nextText = nameField.getText();
        Color nextColor = colorChooser.getColor();

        if (controller.updateSelectedBasicNodeLabel(nextText, nextColor)) {
            canvas.repaint();
        }
    }
}
