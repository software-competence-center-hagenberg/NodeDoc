package at.scch.nodedoc.modelresolver;

import at.scch.nodedoc.nodeset.NodeId;
import at.scch.nodedoc.nodeset.UANode;
import at.scch.nodedoc.nodeset.UANodeSet;
import at.scch.nodedoc.nodeset.UAReferenceType;
import at.scch.nodedoc.parser.rawModel.RawNode;
import at.scch.nodedoc.uaStandard.Nodes;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class UANodeImpl<Node extends RawNode> implements UANode {

    @Getter(AccessLevel.PACKAGE)
    protected final Node rawNode;

    @Getter
    private final MultiValuedMap<UAReferenceType, UANode> forwardReferences = new HashSetValuedHashMap<>();

    @Getter
    private final MultiValuedMap<UAReferenceType, UANode> backwardReferences = new HashSetValuedHashMap<>();

    @Getter
    @Setter(AccessLevel.PACKAGE)
    private UANodeSet nodeSet;

    @Override
    public Collection<UANode> getForwardReferencedNodes(NodeId<?> referenceTypeId) {
        return forwardReferences.entries().stream()
                .filter(ref -> ref.getKey().getNodeId().equals(referenceTypeId))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<UANode> getBackwardReferencedNodes(NodeId<?> referenceTypeId) {
        return backwardReferences.entries().stream()
                .filter(ref -> ref.getKey().getNodeId().equals(referenceTypeId))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @EqualsAndHashCode.Include
    private final NodeId<?> nodeId;

    public UANodeImpl(Node rawNode, NodeId<?> nodeId) {
        this.rawNode = rawNode;
        this.nodeId = nodeId;
    }

    @Override
    public NodeId<?> getNodeId() {
        return nodeId;
    }

    @Override
    public String getDisplayName() {
        return rawNode.getDisplayName();
    }

    @Override
    public String getDescription() {
        return rawNode.getDescription();
    }

    @Override
    public void setDescription(String description) {
        rawNode.setDescription(description);
    }

    @Override
    public String getCategory() {
        return rawNode.getCategory();
    }

    @Override
    public String getDocumentation() {
        return rawNode.getDocumentation();
    }

    @Override
    public void setDocumentation(String documentation) {
        rawNode.setDocumentation(documentation);
    }

    @Override
    public String getBrowseName() {
        return rawNode.getBrowseName();
    }

    @Override
    public int getWriteMask() {
        return rawNode.getWriteMask();
    }

    @Override
    public int getUserWriteMask() {
        return rawNode.getUserWriteMask();
    }

    @Override
    public String getSymbolicName() {
        return rawNode.getSymbolicName();
    }

    @Override
    public Collection<UANode> getModellingRules() {
        return getForwardReferencedNodes(Nodes.ReferenceTypes.HAS_MODELLING_RULE);
    }

    @Override
    public Optional<UANode> getTypeDefinition() {
        return getForwardReferencedNodes(Nodes.ReferenceTypes.HAS_TYPE_DEFINITION).stream().findFirst();
    }
}
