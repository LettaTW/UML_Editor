package umleditor.ui;

import umleditor.application.service.EditorController;
import umleditor.domain.DocumentEvent;
import umleditor.domain.DocumentObserver;
import umleditor.enumtype.ToolMode;

import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;

public class EditorMenuBar extends JMenuBar implements DocumentObserver {
    private final EditorController controller;
    private final Frame owner;

    private final JMenuItem groupMenuItem = new JMenuItem("Group");
    private final JMenuItem ungroupMenuItem = new JMenuItem("Ungroup");
    private final JMenuItem labelMenuItem = new JMenuItem("Label");

    public EditorMenuBar(EditorController controller, Frame owner) {
        this.controller = controller;
        this.owner = owner;

        JMenu editMenu = new JMenu("Edit");
        editMenu.add(groupMenuItem);
        editMenu.add(ungroupMenuItem);
        editMenu.addSeparator();
        editMenu.add(labelMenuItem);
        add(editMenu);

        bindActions();
        refreshState();
        controller.addDocumentObserver(this);
    }

    @Override
    public void onDocumentChanged(DocumentEvent event) {
        DocumentEvent.Type type = event.type();
        if (type != DocumentEvent.Type.SELECTION_CHANGED && type != DocumentEvent.Type.TOOL_CHANGED) {
            return;
        }

        SwingUtilities.invokeLater(this::refreshState);
    }

    private void bindActions() {
        groupMenuItem.addActionListener(e -> controller.groupSelected());
        ungroupMenuItem.addActionListener(e -> controller.ungroupSelected());
        labelMenuItem.addActionListener(e -> openLabelDialog());
    }

    private void refreshState() {
        boolean inSelectMode = controller.getCurrentToolMode() == ToolMode.SELECT;
        groupMenuItem.setEnabled(inSelectMode && controller.canGroupSelected());
        ungroupMenuItem.setEnabled(inSelectMode && controller.canUngroupSelected());
        labelMenuItem.setEnabled(inSelectMode && controller.canEditLabelSelection());
    }

    private void openLabelDialog() {
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
                owner,
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
        controller.updateSelectedBasicNodeLabel(nextText, nextColor);
    }
}

