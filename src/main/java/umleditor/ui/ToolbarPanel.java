package umleditor.ui;

import umleditor.application.service.EditorController;
import umleditor.domain.DocumentEvent;
import umleditor.domain.DocumentObserver;
import umleditor.enumtype.ToolMode;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static umleditor.config.EditorDefaults.TOOLBAR_BUTTON_FONT_SIZE;
import static umleditor.config.EditorDefaults.TOOLBAR_BUTTON_HEIGHT;
import static umleditor.config.EditorDefaults.TOOLBAR_BUTTON_WIDTH;

public class ToolbarPanel extends JPanel implements DocumentObserver {
    private final EditorController controller;
    private final EditorCanvas canvas;

    private final JButton selectButton = new JButton("Select");
    private final JButton associationButton = new JButton("Assoc");
    private final JButton generalizationButton = new JButton("Gen");
    private final JButton compositionButton = new JButton("Comp");
    private final JButton rectButton = new JButton("Rect");
    private final JButton ovalButton = new JButton("Oval");

    public ToolbarPanel(EditorController controller, EditorCanvas canvas) {
        super(new GridBagLayout());
        this.controller = controller;
        this.canvas = canvas;

        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        addToolRow(0, selectButton);
        addToolRow(1, associationButton);
        addToolRow(2, generalizationButton);
        addToolRow(3, compositionButton);
        addToolRow(4, rectButton);
        addToolRow(5, ovalButton);

        bindActions();
        refreshToolbar();
        controller.addDocumentObserver(this);
    }

    @Override
    public void onDocumentChanged(DocumentEvent event) {
        if (event.type() != DocumentEvent.Type.TOOL_CHANGED) {
            return;
        }
        SwingUtilities.invokeLater(this::refreshToolbar);
    }

    private void bindActions() {
        selectButton.addActionListener(e -> controller.setCurrentTool(ToolMode.SELECT));
        associationButton.addActionListener(e -> controller.setCurrentTool(ToolMode.LINK_ASSOCIATION));
        generalizationButton.addActionListener(e -> controller.setCurrentTool(ToolMode.LINK_GENERALIZATION));
        compositionButton.addActionListener(e -> controller.setCurrentTool(ToolMode.LINK_COMPOSITION));

        attachCreateDragFromButton(rectButton, ToolMode.CREATE_RECT);
        attachCreateDragFromButton(ovalButton, ToolMode.CREATE_OVAL);
    }

    private void refreshToolbar() {
        applyModeStyle(selectButton, controller.getCurrentToolMode() == ToolMode.SELECT);
        applyModeStyle(associationButton, controller.getCurrentToolMode() == ToolMode.LINK_ASSOCIATION);
        applyModeStyle(generalizationButton, controller.getCurrentToolMode() == ToolMode.LINK_GENERALIZATION);
        applyModeStyle(compositionButton, controller.getCurrentToolMode() == ToolMode.LINK_COMPOSITION);
        applyModeStyle(rectButton, controller.getCurrentToolMode() == ToolMode.CREATE_RECT);
        applyModeStyle(ovalButton, controller.getCurrentToolMode() == ToolMode.CREATE_OVAL);
    }

    private void addToolRow(int row, JButton button) {
        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.gridx = 0;
        buttonConstraints.gridy = row;
        buttonConstraints.anchor = GridBagConstraints.WEST;
        buttonConstraints.insets = new Insets(5, 3, 5, 3);
        button.setPreferredSize(new Dimension(TOOLBAR_BUTTON_WIDTH, TOOLBAR_BUTTON_HEIGHT));
        button.setFont(button.getFont().deriveFont(Font.PLAIN, (float) TOOLBAR_BUTTON_FONT_SIZE));
        button.setFocusPainted(false);

        add(button, buttonConstraints);
    }

    private void applyModeStyle(JButton button, boolean active) {
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

    private void attachCreateDragFromButton(JButton button, ToolMode mode) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    controller.setTemporaryTool(mode);
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
                }

                controller.restorePreviousTool();
            }
        });
    }
}

