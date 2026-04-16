package umleditor.application.service;

import umleditor.application.factory.LinkFactory;
import umleditor.domain.DiagramDocument;
import umleditor.domain.link.Link;
import umleditor.domain.model.Port;
import umleditor.enumtype.ToolMode;

public class LinkCreationService {
    private final DiagramDocument document;
    private final LinkFactory linkFactory;

    public LinkCreationService(DiagramDocument document, LinkFactory linkFactory) {
        this.document = document;
        this.linkFactory = linkFactory;
    }

    public void createLink(ToolMode mode, Port sourcePort, Port targetPort) {
        if (sourcePort == null || targetPort == null) {
            return;
        }

        if (sourcePort.getOwnerId().equals(targetPort.getOwnerId())) {
            return;
        }

        Link link = linkFactory.createLink(mode, sourcePort, targetPort);
        if (link == null) {
            return;
        }

        document.addLink(link);
    }
}

