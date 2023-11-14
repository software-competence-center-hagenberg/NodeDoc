package at.scch.nodedoc.nodeset;

import org.w3c.dom.Document;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface UANodeSet {
    /**
     * A UAModel represents the NodeSet in a XML-file.
     */

    String getModelUri();

    String getModelUriNoHttp();

    String getVersion();

    OffsetDateTime getPublicationDate();

    OffsetDateTime getLastModified();

    default Collection<UANodeSet> getRequiredNodeSets() {
        return getNodeSetsByNamespaceIndex().values().stream()
                .filter(nodeSet -> !nodeSet.getModelUri().equals(getModelUri()))
                .collect(Collectors.toList());
    }

    // Get Nodes
    Collection<? extends UANode> getAllNodes();
    Collection<UAType> getUATypes();
    Collection<UAObjectType> getUAObjectTypes();
    Collection<UADataType> getUADataTypes();
    Collection<UAVariableType> getUAVariableTypes();
    Collection<UAReferenceType> getUAReferenceTypes();
    Collection<UAObject> getUAObjects();
    Collection<UAVariable> getUAVariables();
    Collection<UAMethod> getUAMethods();
    Collection<UAView> getUAViews();

    UANode getNodeById(NodeId<?> nodeId);

    @Deprecated
    Map<Integer, UANodeSet> getNodeSetsByNamespaceIndex();

    List<String> getNamespaceIndexTable();

    NodeSetUniverse getNodeSetUniverse();

    Document toXMLDocument();
}
