package umleditor.application.service;

import umleditor.domain.link.AssociationLink;
import umleditor.domain.link.CompositionLink;
import umleditor.domain.link.GeneralizationLink;
import umleditor.domain.link.Link;
import umleditor.domain.model.Port;
import umleditor.enumtype.ToolMode;

public class LinkFactory {
    public Link createLink(ToolMode mode, Port sourcePort, Port targetPort) {
        if (mode == null || sourcePort == null || targetPort == null) {
            return null;
        }

        return switch (mode) {
            case LINK_ASSOCIATION -> new AssociationLink(sourcePort, targetPort);
            case LINK_GENERALIZATION -> new GeneralizationLink(sourcePort, targetPort);
            case LINK_COMPOSITION -> new CompositionLink(sourcePort, targetPort);
            default -> null;
        };
    }
}

