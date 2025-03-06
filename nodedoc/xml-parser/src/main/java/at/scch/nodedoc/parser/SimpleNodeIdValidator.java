package at.scch.nodedoc.parser;

import at.scch.nodedoc.parser.rawModel.*;
import at.scch.nodedoc.uaStandard.NodeIdDefinition;
import at.scch.nodedoc.util.StreamUtils;
import lombok.Data;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleNodeIdValidator implements Validator {

    public void validateOrThrow(RawNodeSet nodeSet) {
        validatedUsedNamespaceIndices(nodeSet);
        validateAliases(nodeSet);
    }

    @Data
    public static class NodeIdWithDescription {
        private final String nodeId;
        private final String description;
    }

    private Stream<NodeIdWithDescription> findNodeIds(RawNodeSet nodeSet, boolean includeAliases) {
        var aliasStream = includeAliases ? nodeSet.getAliases().stream()
                .map(alias -> new NodeIdWithDescription(alias.getNodeId(), "Alias")) : Stream.<NodeIdWithDescription>empty();
        return Stream.of(
                aliasStream,
                nodeSet.getNodes().stream()
                        .map(node -> new NodeIdWithDescription(node.getNodeId(), "NodeId of UANode")),
                nodeSet.getNodes().stream()
                        .flatMap(StreamUtils.filterCast(RawInstance.class))
                        .map(instance -> new NodeIdWithDescription(instance.getParentNodeId(), "ParentNodeId of UAInstance")),
                nodeSet.getNodes().stream()
                        .flatMap(StreamUtils.filterCast(RawVariable.class))
                        .map(rawVariable -> new NodeIdWithDescription(rawVariable.getDataType(), "DataType of UAVariable")),
                nodeSet.getNodes().stream()
                        .flatMap(StreamUtils.filterCast(RawVariableType.class))
                        .map(rawVariableType -> new NodeIdWithDescription(rawVariableType.getDataType(), "DataType of UAVariableType")),
                nodeSet.getNodes().stream()
                        .flatMap(StreamUtils.filterCast(RawMethod.class))
                        .map(rawMethod -> new NodeIdWithDescription(rawMethod.getMethodDeclarationId(), "MethodDeclarationId of UAMethod")),
                nodeSet.getNodes().stream()
                        .flatMap(StreamUtils.filterCast(RawDataType.class))
                        .flatMap(dataType -> dataType.getDefinition().stream())
                        .map(rawDefinitionField -> new NodeIdWithDescription(rawDefinitionField.getDataType(), "DataType of Field")),
                nodeSet.getNodes().stream()
                        .flatMap(node -> node.getReferences().stream())
                        .flatMap(reference -> Stream.of(
                                new NodeIdWithDescription(reference.getReferenceType(), "ReferenceType"),
                                new NodeIdWithDescription(reference.getReferencedId(), "referenced node in Reference"))),
                nodeSet.getNodes().stream()
                        .flatMap(StreamUtils.filterCast(RawVariable.class))
                        .flatMap(variable -> variable.getArguments().stream())
                        .map(argument -> new NodeIdWithDescription(argument.getDataType(), "DataType of Argument"))
        ).flatMap(Function.identity());
    }

    private boolean hasValidNamespaceIndex(NodeIdWithDescription nodeIdWithDescription, int highestNamespaceIndex) {
        var matcher = NodeIdDefinition.NODE_ID_REGEX.matcher(nodeIdWithDescription.getNodeId());
        if (matcher.find()) {
            var namespaceId = matcher.group("ns") != null
                    ? Integer.parseInt(matcher.group("ns"))
                    : 0;
            return namespaceId >= 0 && namespaceId <= highestNamespaceIndex;
        } else {
            return true;
        }
    }

    private void validatedUsedNamespaceIndices(RawNodeSet nodeSet) {
        var highestNamespaceIndex = nodeSet.getNamespaceUris().size();
        var nodeIdsWithInvalidNamespaceIndices = findNodeIds(nodeSet, true)
                .filter(nodeIdWithDescription -> nodeIdWithDescription.getNodeId() != null)
                .filter(nodeIdWithDescription -> !hasValidNamespaceIndex(nodeIdWithDescription, highestNamespaceIndex))
                .collect(Collectors.toList());
        if (!nodeIdsWithInvalidNamespaceIndices.isEmpty()) {
            throw new NodeSetNodeIdValidationException("NodeIds detected, which use an invalid namespace index", nodeIdsWithInvalidNamespaceIndices);
        }
    }

    private boolean usesValidAlias(NodeIdWithDescription nodeIdWithDescription, Set<String> aliases) {
        if (!NodeIdDefinition.NODE_ID_REGEX.matcher(nodeIdWithDescription.getNodeId()).find()) {
            return aliases.contains(nodeIdWithDescription.getNodeId());
        } else {
            return true;
        }
    }

    private void validateAliases(RawNodeSet nodeSet) {
        var aliases = nodeSet.getAliases().stream()
                .map(RawAlias::getAlias).collect(Collectors.toSet());
        var unknownUsedAliases = findNodeIds(nodeSet, false)
                .filter(nodeIdWithDescription -> nodeIdWithDescription.getNodeId() != null)
                .filter(nodeIdWithDescription -> !usesValidAlias(nodeIdWithDescription, aliases))
                .collect(Collectors.toList());
        if (!unknownUsedAliases.isEmpty()) {
            throw new NodeSetNodeIdValidationException("NodeIds detected, which use an unknown Alias", unknownUsedAliases);
        }
    }
}
