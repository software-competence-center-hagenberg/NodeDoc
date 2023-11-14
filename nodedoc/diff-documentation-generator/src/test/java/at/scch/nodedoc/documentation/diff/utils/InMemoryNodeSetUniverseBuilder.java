package at.scch.nodedoc.documentation.diff.utils;

import at.scch.nodedoc.nodeset.NodeId;
import at.scch.nodedoc.uaStandard.Nodes;
import org.apache.maven.surefire.shared.lang3.tuple.Triple;

import java.util.function.Consumer;

public class InMemoryNodeSetUniverseBuilder {

    public static InMemoryNodeSetUniverseBuilder universe(String mainNamespaceUri) {
        return new InMemoryNodeSetUniverseBuilder(mainNamespaceUri);
    }

    private final String mainNamespaceUri;
    private final InMemoryNodeSetUniverse universe;

    private InMemoryNodeSetUniverseBuilder(String mainNamespaceUri) {
        this.mainNamespaceUri = mainNamespaceUri;
        universe = new InMemoryNodeSetUniverse(mainNamespaceUri);
        addStandardNodes();
    }

    private void addStandardNodes() {
        universe.nodes.add(new InMemoryReferenceType(Nodes.ReferenceTypes.HAS_TYPE_DEFINITION, "HasTypeDefinition", universe));
        universe.nodes.add(new InMemoryReferenceType(Nodes.ReferenceTypes.HAS_COMPONENT, "HasComponent", universe));
    }

    public InMemoryNodeSetUniverseBuilder withObjectType(int nodeId, String browseName) {
        universe.nodes.add(new InMemoryObjectType(new NodeId.IntNodeId(mainNamespaceUri, nodeId), browseName, universe));
        return this;
    }

    public InMemoryNodeSetUniverseBuilder withObject(int nodeId, String browseName) {
        universe.nodes.add(new InMemoryObject(new NodeId.IntNodeId(mainNamespaceUri, nodeId), browseName, universe));
        return this;
    }

    public InMemoryNodeSetUniverseBuilder withReferenceType(int nodeId, String browseName) {
        universe.nodes.add(new InMemoryReferenceType(new NodeId.IntNodeId(mainNamespaceUri, nodeId), browseName, universe));
        return this;
    }

    public InMemoryNodeSetUniverseBuilder withReference(int fromNodeId, int toNodeId, int referenceTypeId) {
        addReference(new NodeId.IntNodeId(mainNamespaceUri, fromNodeId), new NodeId.IntNodeId(mainNamespaceUri, toNodeId), new NodeId.IntNodeId(mainNamespaceUri, referenceTypeId));
        return this;
    }

    public InMemoryNodeSetUniverseBuilder hasTypeDefinition(int nodeId, int typeNodeId) {
        addReference(new NodeId.IntNodeId(mainNamespaceUri, nodeId), new NodeId.IntNodeId(mainNamespaceUri, typeNodeId), Nodes.ReferenceTypes.HAS_TYPE_DEFINITION);
        return this;
    }

    public InMemoryNodeSetUniverseBuilder hasComponent(int nodeId, int typeNodeId) {
        addReference(new NodeId.IntNodeId(mainNamespaceUri, nodeId), new NodeId.IntNodeId(mainNamespaceUri, typeNodeId), Nodes.ReferenceTypes.HAS_COMPONENT);
        return this;
    }

    public InMemoryNodeSetUniverseBuilder useConfigurer(Consumer<InMemoryNodeSetUniverseBuilder> configurer) {
        configurer.accept(this);
        return this;
    }

    public InMemoryNodeSetUniverse build() {
        return universe;
    }

    private void addReference(NodeId<?> fromNodeId, NodeId<?> toNodeId, NodeId<?> referenceTypeId) {
        universe.references.add(Triple.of(fromNodeId, toNodeId, referenceTypeId));
    }
}
