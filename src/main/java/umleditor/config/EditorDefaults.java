package umleditor.config;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public final class EditorDefaults {
    private EditorDefaults() {}

    private static final Properties PROPS = loadProperties();

    public static final int MIN_DEPTH = 0;
    public static final int MAX_DEPTH = 99;

    public static final int DEFAULT_DEPTH = clampDepth(getInt("default.depth", 99));

    public static final int MIN_NODE_SIZE = getInt("node.minSize", 20);
    public static final int DEFAULT_NODE_WIDTH = getInt("node.defaultWidth", 120);
    public static final int DEFAULT_NODE_HEIGHT = getInt("node.defaultHeight", 80);

    public static final int PORT_SIZE = getInt("port.size", 10);

    public static final int DEFAULT_LABEL_FONT_SIZE = getInt("label.defaultFontSize", 14);
    public static final int DEFAULT_LABEL_PADDING = getInt("label.defaultPadding", 4);
    public static final String DEFAULT_LABEL_TEXT = getString("label.defaultText", "New Label");

    public static final String DEFAULT_RECT_LABEL_TEXT = getString("rect.defaultLabel", "Class");
    public static final String DEFAULT_OVAL_LABEL_TEXT = getString("oval.defaultLabel", "Use Case");
    public static final Color DEFAULT_RECT_FILL_COLOR = getColor("rect.defaultFillColor", new Color(255, 249, 196));
    public static final Color DEFAULT_OVAL_FILL_COLOR = getColor("oval.defaultFillColor", new Color(205, 229, 255));

    public static final int TOOLBAR_BUTTON_WIDTH = getInt("toolbar.buttonWidth", 84);
    public static final int TOOLBAR_BUTTON_HEIGHT = getInt("toolbar.buttonHeight", 44);
    public static final int TOOLBAR_BUTTON_FONT_SIZE = getInt("toolbar.buttonFontSize", 13);

    public static final Color DEFAULT_SELECTION_BOX_STROKE_COLOR =
            getColor("selection.boxStrokeColor", new Color(50, 90, 180));
    public static final int DEFAULT_SELECTION_BOX_FILL_ALPHA =
            Math.max(0, Math.min(255, getInt("selection.boxFillAlpha", 70)));
    public static final Color DEFAULT_SELECTION_BOX_FILL_COLOR =
            new Color(
                    DEFAULT_SELECTION_BOX_STROKE_COLOR.getRed(),
                    DEFAULT_SELECTION_BOX_STROKE_COLOR.getGreen(),
                    DEFAULT_SELECTION_BOX_STROKE_COLOR.getBlue(),
                    DEFAULT_SELECTION_BOX_FILL_ALPHA
            );

    private static Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream in = EditorDefaults.class.getClassLoader().getResourceAsStream("editor-defaults.properties")) {
            if (in != null) {
                properties.load(in);
                return properties;
            }
        } catch (IOException ignored) {
            // Use fallback sources/default values.
        }

        Path devPath = Paths.get("src", "main", "resources", "editor-defaults.properties");
        if (Files.exists(devPath)) {
            try (InputStream in = Files.newInputStream(devPath)) {
                properties.load(in);
            } catch (IOException ignored) {
                // Use hardcoded default values.
            }
        }

        return properties;
    }

    private static int getInt(String key, int fallback) {
        String raw = PROPS.getProperty(key);
        if (raw == null) {
            return fallback;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static String getString(String key, String fallback) {
        String value = PROPS.getProperty(key);
        return value == null ? fallback : value;
    }

    private static Color getColor(String key, Color fallback) {
        String raw = PROPS.getProperty(key);
        if (raw == null) {
            return fallback;
        }

        String text = raw.trim();
        if (text.startsWith("#")) {
            text = text.substring(1);
        }

        if (text.length() != 6) {
            return fallback;
        }

        try {
            int rgb = Integer.parseInt(text, 16);
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = rgb & 0xFF;
            return new Color(r, g, b);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    public static int clampDepth(int depth) {
        return Math.max(MIN_DEPTH, Math.min(MAX_DEPTH, depth));
    }
}
