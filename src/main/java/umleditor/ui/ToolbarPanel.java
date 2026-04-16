package umleditor.ui;

import umleditor.application.service.EditorController;
import umleditor.domain.DocumentEvent;
import umleditor.domain.DocumentObserver;
import umleditor.enumtype.ToolMode;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
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
    private static final Border DEFAULT_BUTTON_BORDER = BorderFactory.createLineBorder(new Color(190, 190, 190));
    private static final Border HOVER_BUTTON_BORDER = BorderFactory.createLineBorder(new Color(110, 110, 110));
    private static final Border ACTIVE_BUTTON_BORDER = BorderFactory.createLineBorder(new Color(40, 40, 40));

    private final EditorController controller;
    private final EditorCanvas canvas;

    private final JButton selectButton = new ToolButton("Select");
    private final JButton associationButton = new ToolButton("Assoc");
    private final JButton generalizationButton = new ToolButton("Gen");
    private final JButton compositionButton = new ToolButton("Comp");
    private final JButton rectButton = new ToolButton("Rect");
    private final JButton ovalButton = new ToolButton("Oval");

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
        button.setRolloverEnabled(true);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorder(DEFAULT_BUTTON_BORDER);
        button.setBorderPainted(true);
        button.getModel().addChangeListener(e -> refreshToolbar());

        add(button, buttonConstraints);
    }

    private void applyModeStyle(JButton button, boolean active) {
        boolean pressed = button.getModel().isPressed();
        boolean hover = button.getModel().isRollover();
        Color defaultBg = UIManager.getColor("Button.background");
        if (defaultBg == null) {
            defaultBg = new Color(238, 238, 238);
        }

        Color defaultFg = UIManager.getColor("Button.foreground");
        if (defaultFg == null) {
            defaultFg = Color.BLACK;
        }

        button.setBorderPainted(true);
        if (pressed) {
            button.setBackground(Color.BLACK);
            button.setForeground(Color.WHITE);
            button.setBorder(ACTIVE_BUTTON_BORDER);
        } else if (active) {
            button.setBackground(Color.BLACK);
            button.setForeground(Color.WHITE);
            button.setBorder(ACTIVE_BUTTON_BORDER);
        } else if (hover) {
            button.setBackground(darken(defaultBg, 0.08f));
            button.setForeground(defaultFg);
            button.setBorder(HOVER_BUTTON_BORDER);
        } else {
            button.setBackground(defaultBg);
            button.setForeground(defaultFg);
            button.setBorder(DEFAULT_BUTTON_BORDER);
        }
    }

    private Color darken(Color color, float ratio) {
        float clamped = Math.max(0f, Math.min(1f, ratio));
        int r = Math.max(0, Math.round(color.getRed() * (1f - clamped)));
        int g = Math.max(0, Math.round(color.getGreen() * (1f - clamped)));
        int b = Math.max(0, Math.round(color.getBlue() * (1f - clamped)));
        return new Color(r, g, b);
    }

    private static class ToolButton extends JButton {
        private ToolButton(String text) {
            super(text);
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
            super.paintComponent(g);
        }
    }

    private void attachCreateDragFromButton(JButton button, ToolMode mode) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    controller.setTemporaryTool(mode);
                    refreshToolbar();
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
                refreshToolbar();
            }
        });
    }
}
