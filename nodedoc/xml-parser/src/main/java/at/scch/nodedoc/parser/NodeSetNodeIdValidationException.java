package at.scch.nodedoc.parser;

import java.util.Collection;
import java.util.stream.Collectors;

public class NodeSetNodeIdValidationException extends NodeSetValidationException {

    private final Collection<SimpleNodeIdValidator.NodeIdWithDescription> nodeIdsWithInvalidNamespaceIndices;

    public NodeSetNodeIdValidationException(String message, Collection<SimpleNodeIdValidator.NodeIdWithDescription> nodeIdsWithInvalidNamespaceIndices) {
        super(message + ": " + formatNodeIdsForMessage(nodeIdsWithInvalidNamespaceIndices));
        this.nodeIdsWithInvalidNamespaceIndices = nodeIdsWithInvalidNamespaceIndices;
    }

    private static String formatNodeIdsForMessage(Collection<SimpleNodeIdValidator.NodeIdWithDescription> nodeIdsWithInvalidNamespaceIndices) {
        return nodeIdsWithInvalidNamespaceIndices.stream()
                .map(nodeIdWithDescription -> nodeIdWithDescription.getNodeId() + " (" + nodeIdWithDescription.getDescription() + ")")
                .collect(Collectors.joining(", "));
    }

    public Collection<SimpleNodeIdValidator.NodeIdWithDescription> getInvalidNodeIds() {
        return nodeIdsWithInvalidNamespaceIndices;
    }
}
