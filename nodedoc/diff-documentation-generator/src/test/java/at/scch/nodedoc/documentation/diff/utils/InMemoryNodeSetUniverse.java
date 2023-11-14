package at.scch.nodedoc.documentation.diff.utils;

import at.scch.nodedoc.nodeset.*;
import at.scch.nodedoc.uaStandard.Namespaces;
import at.scch.nodedoc.util.StreamUtils;
import org.apache.maven.surefire.shared.lang3.tuple.Triple;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class InMemoryNodeSetUniverse implements NodeSetUniverse {

    final String mainNamespaceUri;
    final UANodeSet mainNodeSet;
    final UANodeSet uaStandardNodeSet;
    final Set<Triple<NodeId<?>, NodeId<?>, NodeId<?>>> references;
    final Set<UANode> nodes;

    public InMemoryNodeSetUniverse(String mainNamespaceUri) {
        this.mainNamespaceUri = mainNamespaceUri;
        this.mainNodeSet = new InMemoryNodeSet(mainNamespaceUri, "X.Y.Z", this);
        this.uaStandardNodeSet = new InMemoryNodeSet(Namespaces.UA, "X.Y.Z", this);
        this.nodes = new HashSet<>();
        this.references = new HashSet<>();
    }

    @Override
    public Collection<UANodeSet> getAllNodeSets() {
        return List.of(mainNodeSet);
    }

    @Override
    public UANodeSet getNodeSetByNamespaceUri(String namespaceUri) {
        return mainNodeSet.getModelUri().equals(namespaceUri) ? mainNodeSet : null;
    }

    @Override
    public Set<Reference> getReferences() {
        return references.stream().map(ref -> {
            return new Reference(
                    getNodeById(ref.getLeft()),
                    getNodeById(ref.getMiddle()),
                    (UAReferenceType) getNodeById(ref.getRight()));
        }).collect(Collectors.toSet());
    }

    @Override
    public Set<Reference> getReferencesFromNode(NodeId<?> source) {
        return getReferences().stream()
                .filter(reference -> reference.getSource().getNodeId().equals(source))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Reference> getReferencesToNode(NodeId<?> target) {
        return getReferences().stream()
                .filter(reference -> reference.getTarget().getNodeId().equals(target))
                .collect(Collectors.toSet());
    }


    private UANode getNodeById(int nodeId) {
        return getNodeById(new NodeId.IntNodeId(mainNamespaceUri, nodeId));
    }
    @Override
    public UANode getNodeById(NodeId<?> nodeId) {
        return nodes.stream()
                .filter(node -> node.getNodeId().equals(nodeId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Set<UANode> getAllNodes() {
        return nodes;
    }

    @Override
    public Set<UAType> getUATypes() {
        return nodes.stream()
                .flatMap(StreamUtils.filterCast(UAType.class))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<UAObjectType> getUAObjectTypes() {
        return nodes.stream()
                .flatMap(StreamUtils.filterCast(UAObjectType.class))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<UADataType> getUADataTypes() {
        return nodes.stream()
                .flatMap(StreamUtils.filterCast(UADataType.class))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<UAVariableType> getUAVariableTypes() {
        return nodes.stream()
                .flatMap(StreamUtils.filterCast(UAVariableType.class))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<UAReferenceType> getUAReferenceTypes() {
        return nodes.stream()
                .flatMap(StreamUtils.filterCast(UAReferenceType.class))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<UAObject> getUAObjects() {
        return nodes.stream()
                .flatMap(StreamUtils.filterCast(UAObject.class))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<UAVariable> getUAVariables() {
        return nodes.stream()
                .flatMap(StreamUtils.filterCast(UAVariable.class))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<UAMethod> getUAMethods() {
        return nodes.stream()
                .flatMap(StreamUtils.filterCast(UAMethod.class))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<UAView> getUAViews() {
        return nodes.stream()
                .flatMap(StreamUtils.filterCast(UAView.class))
                .collect(Collectors.toSet());
    }
}
