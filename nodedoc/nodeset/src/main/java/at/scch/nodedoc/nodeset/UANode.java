package at.scch.nodedoc.nodeset;

import org.apache.commons.collections4.MultiValuedMap;

import java.util.Collection;
import java.util.Optional;

public interface UANode {

    MultiValuedMap<UAReferenceType, UANode> getForwardReferences();
    MultiValuedMap<UAReferenceType, UANode> getBackwardReferences();

    Collection<UANode> getForwardReferencedNodes(NodeId<?> referenceTypeId);
    Collection<UANode> getBackwardReferencedNodes(NodeId<?> referenceTypeId);

    NodeId<?> getNodeId();
    String getDisplayName();
    String getDescription();
    void setDescription(String description);
    String getCategory();
    String getDocumentation();
    void setDocumentation(String documentation);
    String getBrowseName();
    int getWriteMask();
    int getUserWriteMask();
    String getSymbolicName();
    NodeClass getNodeClass();
    Collection<UANode> getModellingRules();
    Optional<UANode> getTypeDefinition();
    UANodeSet getNodeSet();
}
