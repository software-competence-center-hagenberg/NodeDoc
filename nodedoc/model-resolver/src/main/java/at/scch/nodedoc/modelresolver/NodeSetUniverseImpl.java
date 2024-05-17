package at.scch.nodedoc.modelresolver;

import at.scch.nodedoc.nodeset.*;
import at.scch.nodedoc.util.StreamUtils;
import lombok.Getter;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class NodeSetUniverseImpl implements NodeSetUniverse {

    private final Map<String, UANodeSetImpl> nodeSetsByNamespaceUri;
    @Getter
    private final Set<Reference> references;

    public NodeSetUniverseImpl(Map<String, UANodeSetImpl> nodeSetsByNamespaceUri, Set<Reference> references) {
        this.nodeSetsByNamespaceUri = nodeSetsByNamespaceUri;
        this.references = references;
    }

    @Override
    public Collection<UANodeSet> getAllNodeSets() {
        return Collections.unmodifiableCollection(nodeSetsByNamespaceUri.values());
    }

    @Override
    public UANodeSet getNodeSetByNamespaceUri(String namespaceUri) {
        return nodeSetsByNamespaceUri.get(namespaceUri);
    }

    @Override
    public Set<Reference> getReferencesFromNode(NodeId<?> source) {
        return references.stream()
                .filter(reference -> reference.getSource().getNodeId().equals(source))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Reference> getReferencesToNode(NodeId<?> target) {
        return references.stream()
                .filter(reference -> reference.getTarget().getNodeId().equals(target))
                .collect(Collectors.toSet());
    }

    @Override
    public UANode getNodeById(NodeId<?> nodeId) {
        return getAllNodes().stream()
                .filter(uaNode -> uaNode.getNodeId().equals(nodeId))
                .findFirst()
                .orElseThrow(() -> new NodeNotFoundException("Node with id " + nodeId.toString() + " not found."));
    }

    @Override
    public Set<UANode> getAllNodes() {
        return getAllNodeSets().stream()
                .flatMap(nodeSet -> nodeSet.getAllNodes().stream())
                .collect(Collectors.toSet());
    }

    private <T> Set<T> getNodes(Class<T> typ) {
        return getAllNodes().stream()
                .flatMap(StreamUtils.filterCast(typ))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<UAType> getUATypes() {
        return getNodes(UAType.class);
    }

    @Override
    public Set<UAObjectType> getUAObjectTypes() {
        return getNodes(UAObjectType.class);
    }

    @Override
    public Set<UADataType> getUADataTypes() {
        return getNodes(UADataType.class);
    }

    @Override
    public Set<UAVariableType> getUAVariableTypes() {
        return getNodes(UAVariableType.class);
    }

    @Override
    public Set<UAReferenceType> getUAReferenceTypes() {
        return getNodes(UAReferenceType.class);
    }

    @Override
    public Set<UAObject> getUAObjects() {
        return getNodes(UAObject.class);
    }

    @Override
    public Set<UAVariable> getUAVariables() {
        return getNodes(UAVariable.class);
    }

    @Override
    public Set<UAMethod> getUAMethods() {
        return getNodes(UAMethod.class);
    }

    @Override
    public Set<UAView> getUAViews() {
        return getNodes(UAView.class);
    }
}
