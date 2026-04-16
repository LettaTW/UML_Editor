package umleditor.application.factory;

import umleditor.application.service.ElementTransformService;
import umleditor.application.service.LinkCreationService;
import umleditor.application.service.PointerTargetingService;
import umleditor.application.service.ResizeService;
import umleditor.application.service.SelectInteractionStateService;
import umleditor.application.service.SelectionStateService;
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

    public ToolFactory(
            DiagramDocument document,
            NodeFactory nodeFactory,
            LinkCreationService linkCreationService,
            SelectionStateService selectionStateService,
            PointerTargetingService pointerTargetingService,
            ResizeService resizeService,
            ElementTransformService elementTransformService,
            SelectInteractionStateService interactionStateService
    ) {
        this.document = document;
        this.nodeFactory = nodeFactory;
        this.linkCreationService = linkCreationService;
        this.selectionStateService = selectionStateService;
        this.pointerTargetingService = pointerTargetingService;
        this.resizeService = resizeService;
        this.elementTransformService = elementTransformService;
        this.interactionStateService = interactionStateService;
    }

    public Tool createTool(ToolMode mode) {
        if (mode == null) {
            return null;
        }

        return switch (mode) {
            case SELECT -> new SelectTool(
                    selectionStateService,
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


