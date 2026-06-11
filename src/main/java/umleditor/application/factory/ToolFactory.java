package umleditor.application.factory;

import umleditor.application.service.*;
import umleditor.application.tools.*;
import umleditor.domain.DiagramDocument;
import umleditor.enumtype.ToolMode;

public class ToolFactory {
    private final DiagramDocument document;
    private final NodeFactory nodeFactory;
    private final LinkCreationService linkCreationService;
    private final SelectionStateService selectionStateService;
    private final PointerTargetingService pointerTargetingService;
    private final ResizeService resizeService;
    private final ElementTransformService elementTransformService;
    private final SelectInteractionStateService interactionStateService;
    private final SelectionQueryService selectionQueryService;

    public ToolFactory(
            DiagramDocument document,
            NodeFactory nodeFactory,
            LinkCreationService linkCreationService,
            SelectionStateService selectionStateService,
            PointerTargetingService pointerTargetingService,
            ResizeService resizeService,
            ElementTransformService elementTransformService,
            SelectInteractionStateService interactionStateService, SelectionQueryService selectionQueryService
    ) {
        this.document = document;
        this.nodeFactory = nodeFactory;
        this.linkCreationService = linkCreationService;
        this.selectionStateService = selectionStateService;
        this.pointerTargetingService = pointerTargetingService;
        this.resizeService = resizeService;
        this.elementTransformService = elementTransformService;
        this.interactionStateService = interactionStateService;
        this.selectionQueryService = selectionQueryService;
    }

    public Tool createTool(ToolMode mode) {
        if (mode == null) {
            return null;
        }

        return switch (mode) {
            case SELECT -> new SelectTool(
                    selectionStateService,
                    selectionQueryService,
                    pointerTargetingService,
                    resizeService,
                    elementTransformService,
                    interactionStateService
            );
            case CREATE_RECT -> new CreateRectTool(document, nodeFactory);
            case CREATE_OVAL -> new CreateOvalTool(document, nodeFactory);
            case LINK_ASSOCIATION, LINK_GENERALIZATION, LINK_COMPOSITION ->
                    new DragCreateLinkTool(mode, linkCreationService, pointerTargetingService);
        };
    }
}


