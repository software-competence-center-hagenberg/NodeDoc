package at.scch.nodedoc.documentation.diff.utils;

import at.scch.nodedoc.nodeset.*;
import org.w3c.dom.Document;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class InMemoryNodeSet implements UANodeSet {

    private final NodeSetUniverse universe;
    private final String modelUri;
    String version;
    OffsetDateTime publicationDate;
    OffsetDateTime lastModified;

    public InMemoryNodeSet(String modelUri, String version, NodeSetUniverse universe) {
        this.modelUri = modelUri;
        this.version = version;
        this.universe = universe;
    }

    @Override
    public String getModelUri() {
        return modelUri;
    }

    @Override
    public String getModelUriNoHttp() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public OffsetDateTime getPublicationDate() {
        return publicationDate;
    }

    @Override
    public OffsetDateTime getLastModified() {
        return lastModified;
    }

    @Override
    public Collection<? extends UANode> getAllNodes() {
        return universe.getAllNodes();
    }

    @Override
    public Collection<UAType> getUATypes() {
        return universe.getUATypes();
    }

    @Override
    public Collection<UAObjectType> getUAObjectTypes() {
        return universe.getUAObjectTypes();
    }

    @Override
    public Collection<UADataType> getUADataTypes() {
        return universe.getUADataTypes();
    }

    @Override
    public Collection<UAVariableType> getUAVariableTypes() {
        return universe.getUAVariableTypes();
    }

    @Override
    public Collection<UAReferenceType> getUAReferenceTypes() {
        return universe.getUAReferenceTypes();
    }

    @Override
    public Collection<UAObject> getUAObjects() {
        return universe.getUAObjects();
    }

    @Override
    public Collection<UAVariable> getUAVariables() {
        return universe.getUAVariables();
    }

    @Override
    public Collection<UAMethod> getUAMethods() {
        return universe.getUAMethods();
    }

    @Override
    public Collection<UAView> getUAViews() {
        return universe.getUAViews();
    }

    @Override
    public UANode getNodeById(NodeId<?> nodeId) {
        return universe.getNodeById(nodeId);
    }

    @Override
    public Map<Integer, UANodeSet> getNodeSetsByNamespaceIndex() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NodeSetUniverse getNodeSetUniverse() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Document toXMLDocument() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getNamespaceIndexTable() {
        throw new UnsupportedOperationException();
    }
}
