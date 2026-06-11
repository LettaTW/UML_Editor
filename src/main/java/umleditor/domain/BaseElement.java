package umleditor.domain;

import umleditor.config.EditorDefaults;
import umleditor.domain.model.*;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.*;

import static umleditor.config.EditorDefaults.clampDepth;


public abstract class BaseElement implements DiagramElement {
    protected final String uuid;
    protected int Depth = EditorDefaults.DEFAULT_DEPTH;
    protected boolean selected;
    protected boolean hovered;

    protected BaseElement() {
        this.uuid = UUID.randomUUID().toString();
    }

    @Override
    public String getID() { return this.uuid; }

    @Override
    public int getDepth() { return this.Depth; }

    @Override
    public void setDepth(int depth) {
        this.Depth = EditorDefaults.clampDepth(depth);
    }

    @Override
    public boolean isSelected() {
        return this.selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean isHovered() {
        return this.hovered;
    }

    @Override
    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    // Type Checking Methods (Default to false)
    public boolean isNode() {
        return false;
    }

    public boolean isLink() {
        return false;
    }

    public boolean isComposite() {
        return false;
    }

    // Structural Behaviors (Safe default implementations)
    public List<BaseElement> ungroup() {
        return Collections.emptyList();
    }

    public List<String> collectOwnedNodeIds() {
        return Collections.emptyList();
    }

    // Transform and Geometry Behaviors
    public void resizeTo(Rectangle bounds) {
        // Empty implementation
    }

    public Port findPortAt(Point p) {
        return null;
    }

    public List<Port> getPorts() {
        return Collections.emptyList();
    }

    // Label and Appearance Behaviors
    public String getLabelText() {
        return null;
    }

    public Color getFillColor() {
        return null;
    }

    public void setLabelText(String text) {
        // Empty implementation
    }

    public void setFillColor(Color color) {
        // Empty implementation
    }

    public void onNodeMoved(String nodeId, int dx, int dy) {
        // Empty implementation
    }

    public void onNodeReshaped(String nodeId, List<Port> ports) {
        // Empty implementation
    }

}
