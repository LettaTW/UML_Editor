# Phase 1 Minimal Interface Draft (Polymorphic Collaboration)

## Goal
Remove core `instanceof` branches from interaction and transform flows by pushing element capabilities into `DiagramElement` polymorphism.

## Capability methods added to `DiagramElement`
- `boolean isLinkElement()`
- `boolean canMoveByDrag()`
- `boolean isBoxSelectable()`
- `boolean isGroupable()`
- `boolean isGroupContainer()`
- `List<DiagramElement> releaseChildren()`
- `List<String> collectOwnedNodeIds()`
- `boolean supportsResize()`
- `void resizeTo(Rectangle bounds)`
- `void onNodeMoved(String nodeId, int dx, int dy)`
- `void onNodeReshaped(String nodeId, List<Port> ports)`
- `boolean supportsLabelEdit()`
- `String getLabelText()`
- `Color getFillColor()`
- `void setLabelText(String text)`
- `void setFillColor(Color color)`

All defaults are no-op or safe fallbacks to keep migration incremental.

## Concrete overrides in Phase 1
- `Link`
  - `isLinkElement() == true`
  - Handles `onNodeMoved/onNodeReshaped`
- `Node`
  - Owns one node id via `collectOwnedNodeIds()`
  - Supports resize via `supportsResize()/resizeTo(...)`
  - Supports label edit via `supportsLabelEdit()`
- `Composite`
  - `isGroupContainer() == true`
  - Aggregates child ids via child `collectOwnedNodeIds()` recursion

## Hotspots migrated in Phase 1
- `DefaultElementTransformService`
  - move/resize and link notifications use polymorphic methods only
- `SelectTool`
  - move and box-selection eligibility use capability methods
- `EditorController`
  - group/ungroup/label eligibility use capability methods

## Group depth-order bug fix
When grouping, selected elements are now collected in `document.getElementsForRender()` order before creating `Composite`, preserving original internal layering after grouping.

## Deferred to later phases
- Tool factory migration (`ToolMode` behavior extraction)
- Use-case service split and command execution pipeline
- Observer event bus integration
- Renderer separation (explicitly deferred)

