package at.scch.nodedoc.modelresolver;

import at.scch.nodedoc.nodeset.*;
import at.scch.nodedoc.parser.rawModel.RawNodeSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Document;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UANodeSetImpl implements UANodeSet {

    private final RawNodeSet rawNodeSet;
    private final List<UANodeImpl<?>> nodes;

    @Getter
    private final Map<Integer, UANodeSet> nodeSetsByNamespaceIndex;

    @Getter
    @Setter(AccessLevel.PACKAGE)
    private NodeSetUniverse nodeSetUniverse;

    public UANodeSetImpl(RawNodeSet rawNodeSet, List<UANodeImpl<?>> nodes, Map<Integer, UANodeSet> nodeSetsByNamespaceIndex) {
        this.rawNodeSet = rawNodeSet;
        this.nodes = nodes;
        this.nodeSetsByNamespaceIndex = nodeSetsByNamespaceIndex;
    }

    @Override
    public String getModelUri() {
        return rawNodeSet.getModels().get(0).getModelUri();
    }

    @Override
    public String getModelUriNoHttp() {
        return rawNodeSet.getModels().get(0).getModelUri().replaceAll("^https?://", ""); // TODO
    }

    @Override
    public String getVersion() {
        return rawNodeSet.getModels().get(0).getVersion();
    }

    @Override
    public OffsetDateTime getPublicationDate() {
        return rawNodeSet.getModels().get(0).getPublicationDate();
    }

    @Override
    public OffsetDateTime getLastModified() {
        return rawNodeSet.getLastModified();
    }

    // Get Nodes
    @Override
    public Collection<UAType> getUATypes() {
        return nodes.stream()
                .filter(node -> node instanceof UAType)
                .map(node -> (UAType)node)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<UAObjectType> getUAObjectTypes() {
        return nodes.stream()
                .filter(node -> node instanceof UAObjectType)
                .map(node -> (UAObjectType)node)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<UADataType> getUADataTypes() {
        return nodes.stream()
                .filter(node -> node instanceof UADataType)
                .map(node -> (UADataType)node)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<UAVariableType> getUAVariableTypes() {
        return nodes.stream()
                .filter(node -> node instanceof UAVariableType)
                .map(node -> (UAVariableType)node)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<UAReferenceType> getUAReferenceTypes() {
        return nodes.stream()
                .filter(node -> node instanceof UAReferenceType)
                .map(node -> (UAReferenceType)node)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<UAObject> getUAObjects() {
        return nodes.stream()
                .filter(node -> node instanceof UAObject)
                .map(node -> (UAObject)node)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<UAVariable> getUAVariables() {
        return nodes.stream()
                .filter(node -> node instanceof UAVariable)
                .map(node -> (UAVariable)node)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<UAMethod> getUAMethods() {
        return nodes.stream()
                .filter(node -> node instanceof UAMethod)
                .map(node -> (UAMethod)node)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<UAView> getUAViews() {
        return nodes.stream()
                .filter(node -> node instanceof UAView)
                .map(node -> (UAView)node)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends UANode> getAllNodes() {
        return nodes;
    }

    @Override
    public UANode getNodeById(NodeId<?> nodeId) {
        return nodes.stream()
                .filter(node -> node.getNodeId().equals(nodeId))
                .findAny().orElse(null);
    }

    @Override
    public Document toXMLDocument() {
        return rawNodeSet.getXMLDocument();
    }

    @Override
    public List<String> getNamespaceIndexTable() {
        return rawNodeSet.getNamespaceUris();
    }
}
