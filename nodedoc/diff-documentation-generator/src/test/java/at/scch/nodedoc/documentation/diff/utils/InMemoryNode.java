package at.scch.nodedoc.documentation.diff.utils;

import at.scch.nodedoc.nodeset.*;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.MultiValuedMap;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class InMemoryNode implements UANode {

    @EqualsAndHashCode.Include
    private final NodeId<?> nodeId;
    private final String browseName;
    protected final InMemoryNodeSetUniverse universe;

    public InMemoryNode(NodeId<?> nodeId, String browseName, InMemoryNodeSetUniverse universe) {
        this.browseName = browseName;
        this.nodeId = nodeId;
        this.universe = universe;
    }

    @Override
    public MultiValuedMap<UAReferenceType, UANode> getForwardReferences() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MultiValuedMap<UAReferenceType, UANode> getBackwardReferences() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<UANode> getForwardReferencedNodes(NodeId<?> referenceTypeId) {
        return universe.getReferencesFromNode(nodeId).stream()
                .filter(reference -> reference.getReferenceType().getNodeId().equals(referenceTypeId))
                .map(NodeSetUniverse.Reference::getTarget)
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<UANode> getBackwardReferencedNodes(NodeId<?> referenceTypeId) {
        return universe.getReferencesToNode(nodeId).stream()
                .filter(reference -> reference.getReferenceType().getNodeId().equals(referenceTypeId))
                .map(NodeSetUniverse.Reference::getSource)
                .collect(Collectors.toSet());
    }

    @Override
    public NodeId<?> getNodeId() {
        return nodeId;
    }

    @Override
    public String getDisplayName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public void setDescription(String description) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCategory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDocumentation() {
        return "";
    }

    @Override
    public void setDocumentation(String documentation) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getBrowseName() {
        return browseName;
    }

    @Override
    public int getWriteMask() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getUserWriteMask() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getSymbolicName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<UANode> getModellingRules() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<UANode> getTypeDefinition() {
        throw new UnsupportedOperationException();
    }

    @Override
    public UANodeSet getNodeSet() {
        return universe.mainNodeSet;
    }
}
