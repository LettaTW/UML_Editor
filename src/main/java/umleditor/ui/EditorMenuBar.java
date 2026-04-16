package umleditor.ui;

import umleditor.application.service.EditorController;
import umleditor.domain.DocumentEvent;
import umleditor.domain.DocumentObserver;
import umleditor.enumtype.ToolMode;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import static umleditor.config.EditorDefaults.LABEL_PRESET_COLORS;
import static umleditor.config.EditorDefaults.LABEL_PRESET_NAMES;

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

        Color[] presetColors = LABEL_PRESET_COLORS;
        String[] presetNames = LABEL_PRESET_NAMES;
        int presetCount = Math.min(presetColors.length, presetNames.length);
        if (presetCount == 0) {
            return;
        }

        JTextField nameField = new JTextField(current.text(), 24);
        String[] dialogColorNames = new String[presetCount];
        for (int i = 0; i < presetCount; i++) {
            dialogColorNames[i] = presetNames[i];
        }
        JComboBox<String> colorPresetBox = new JComboBox<>(dialogColorNames);
        int selectedColorIndex = findColorPresetIndex(current.fillColor(), presetColors, presetCount);
        colorPresetBox.setSelectedIndex(selectedColorIndex);

        JPanel previewPanel = new JPanel();
        previewPanel.setPreferredSize(new Dimension(180, 40));
        previewPanel.setBorder(BorderFactory.createLineBorder(new Color(120, 120, 120)));
        JLabel previewLabel = new JLabel("", JLabel.CENTER);
        previewPanel.add(previewLabel);

        Runnable refreshPreview = () -> {
            String text = nameField.getText();
            previewLabel.setText(text == null || text.trim().isEmpty() ? "Preview" : text.trim());
            previewPanel.setBackground(presetColors[colorPresetBox.getSelectedIndex()]);
        };
        nameField.addActionListener(e -> refreshPreview.run());
        nameField.getDocument().addDocumentListener(new SimpleDocumentAdapter(refreshPreview));
        colorPresetBox.addActionListener(e -> refreshPreview.run());
        refreshPreview.run();

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0;
        c.gridy = 0;
        panel.add(new JLabel("Name"), c);
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        panel.add(nameField, c);

        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        panel.add(new JLabel("Color"), c);
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(colorPresetBox, c);

        c.gridx = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Preview"), c);
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(previewPanel, c);

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
        Color nextColor = presetColors[colorPresetBox.getSelectedIndex()];
        controller.updateSelectedBasicNodeLabel(nextText, nextColor);
    }

    private int findColorPresetIndex(Color current, Color[] presets, int count) {
        if (current != null) {
            for (int i = 0; i < count; i++) {
                if (current.equals(presets[i])) {
                    return i;
                }
            }
        }
        return 0;
    }

    private static class SimpleDocumentAdapter implements javax.swing.event.DocumentListener {
        private final Runnable onChange;

        private SimpleDocumentAdapter(Runnable onChange) {
            this.onChange = onChange;
        }

        @Override
        public void insertUpdate(javax.swing.event.DocumentEvent e) {
            onChange.run();
        }

        @Override
        public void removeUpdate(javax.swing.event.DocumentEvent e) {
            onChange.run();
        }

        @Override
        public void changedUpdate(javax.swing.event.DocumentEvent e) {
            onChange.run();
        }
    }
}

