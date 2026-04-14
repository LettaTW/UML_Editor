# AGENTS Guide for UML_Editor

## Project snapshot
- Language: Java, IntelliJ module project (`UML_Editor.iml`), no Maven/Gradle wrapper.
- Current implementation is domain-centric; UI/rendering/application layers are scaffolded but empty.
- Existing source files are only under `src/main/java/umleditor/{config,domain}` plus empty package placeholders.

## Architecture you should assume
- `DiagramElement` is the core contract for interactive diagram objects: identity, z-depth, selection/hover state, geometry and hit-testing (`src/main/java/umleditor/domain/DiagramElement.java`).
- `BaseElement` centralizes shared element state (`uuid`, depth, selected, hovered) and default depth from config (`src/main/java/umleditor/domain/BaseElement.java`, `src/main/java/umleditor/config/EditorDefaults.java`).
- `Node` is the base geometric primitive for box-like elements with bounds + ports (`src/main/java/umleditor/domain/node/Node.java`).
- `Port` is a handle-like connection point owned by a node ID; hit-testing is rectangle-based (`src/main/java/umleditor/domain/model/Port.java`, `src/main/java/umleditor/domain/model/Handle.java`).

## Important behavior patterns (follow these)
- When node geometry changes, ports must be recomputed by calling `updatePorts()`.
  - This is already enforced in `Node.moveBy`, `Node.setBounds`, and `Node.resizeByCorner`.
- Expose ports read-only to callers (`Collections.unmodifiableList(...)` in `Node.getPorts()`).
- Keep hit-testing based on `java.awt.Rectangle` + `Point` (`contains`, `getBounds`) for both nodes and ports.
- Respect sizing constants from `EditorDefaults` instead of hardcoding (`MIN_NODE_SIZE`, `PORT_SIZE`, etc.).
- Preserve ID semantics: `BaseElement` IDs are UUID strings generated once in constructor.

## Package boundary reality (do not hallucinate missing layers)
- `src/main/java/umleditor/application/{command,service,tools}` exists but has no classes yet.
- `src/main/java/umleditor/{ui,rendering,enumtype}` and `domain/link` are currently empty.
- `src/main/java/umleditor/App.java` exists but is a stub (no `main` method yet).

## Build/test workflow (current state)
- There is no discovered build tool config (`pom.xml`, `build.gradle`, wrapper scripts are absent).
- Use plain `javac` for quick compile checks, for example:
```powershell
Set-Location "E:\A lot of NCU\Courses\3-2\Object-Oriented Analysis and Design\UML_Editor"
New-Item -ItemType Directory -Force out | Out-Null
javac -d out src\main\java\umleditor\config\EditorDefaults.java src\main\java\umleditor\domain\DiagramElement.java src\main\java\umleditor\domain\BaseElement.java src\main\java\umleditor\domain\model\Handle.java src\main\java\umleditor\domain\model\Port.java src\main\java\umleditor\domain\node\Node.java
```
- `src/test/java` exists but no test classes are present.

## Integration notes for future agent edits
- Node-to-port relationship is ID-based (`Port.ownerId` stores node ID string), not object reference.
- Keep geometry methods deterministic and side-effect scoped to element state + port refresh.
- If adding concrete node/link types, place them under `domain/node` or `domain/link` and keep `DiagramElement` contract intact.

