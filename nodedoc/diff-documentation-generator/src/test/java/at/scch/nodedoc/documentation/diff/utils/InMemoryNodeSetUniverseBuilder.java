package at.scch.nodedoc.documentation.diff.utils;

import at.scch.nodedoc.nodeset.NodeId;
import at.scch.nodedoc.nodeset.UADataType;
import at.scch.nodedoc.uaStandard.BrowseNames;
import at.scch.nodedoc.uaStandard.Nodes;
import at.scch.nodedoc.util.StreamUtils;
import org.apache.maven.surefire.shared.lang3.tuple.Triple;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
        universe.nodes.add(new InMemoryReferenceType(Nodes.ReferenceTypes.HAS_PROPERTY, "HasProperty", universe));
    }

    public InMemoryNodeSetUniverseBuilder withObjectType(int nodeId, String browseName) {
        universe.nodes.add(new InMemoryObjectType(new NodeId.IntNodeId(mainNamespaceUri, nodeId), browseName, universe));
        return this;
    }

    public InMemoryNodeSetUniverseBuilder withObject(int nodeId, String browseName) {
        universe.nodes.add(new InMemoryObject(new NodeId.IntNodeId(mainNamespaceUri, nodeId), browseName, universe));
        return this;
    }

    public InMemoryNodeSetUniverseBuilder withDataType(int nodeId, String browseName) {
        universe.nodes.add(new InMemoryDataType(new NodeId.IntNodeId(mainNamespaceUri, nodeId), browseName, universe));
        return this;
    }

    public InMemoryNodeSetUniverseBuilder withVariableType(int nodeId, String browseName) {
        universe.nodes.add(new InMemoryVariableType(new NodeId.IntNodeId(mainNamespaceUri, nodeId), browseName, universe));
        return this;
    }

    public InMemoryNodeSetUniverseBuilder withMethod(int nodeId, String browseName) {
        universe.nodes.add(new InMemoryMethod(new NodeId.IntNodeId(mainNamespaceUri, nodeId), browseName, universe));
        return this;
    }

    public InMemoryNodeSetUniverseBuilder withInputArgumentsVariable(int nodeId, List<String> arguments, int argumentDataTypeId, int dataTypeId) {
        createArgumentsVariable(nodeId, arguments, argumentDataTypeId, BrowseNames.ArgumentNames.INPUT_ARGUMENT, dataTypeId);
        return this;
    }

    private void createArgumentsVariable(int nodeId, List<String> arguments, int argumentDataTypeId, String browseName, int dataTypeId) {
        var argumentDataType = universe.nodes.stream()
                .flatMap(StreamUtils.filterCast(UADataType.class))
                .filter(n -> n.getNodeId().getId().equals(argumentDataTypeId))
                .findFirst().orElseThrow();
        var dataType = universe.nodes.stream()
                .flatMap(StreamUtils.filterCast(UADataType.class))
                .filter(n -> n.getNodeId().getId().equals(dataTypeId))
                .findFirst().orElseThrow();
        var variable = new InMemoryVariable(
                new NodeId.IntNodeId(mainNamespaceUri, nodeId),
                browseName,
                universe,
                arguments.stream().map(name -> new InMemoryArgument(argumentDataType, name)).collect(Collectors.toList()),
                dataType
        );
        universe.nodes.add(variable);
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

    public InMemoryNodeSetUniverseBuilder hasComponent(int nodeId, int componentNodeId) {
        addReference(new NodeId.IntNodeId(mainNamespaceUri, nodeId), new NodeId.IntNodeId(mainNamespaceUri, componentNodeId), Nodes.ReferenceTypes.HAS_COMPONENT);
        return this;
    }

    public InMemoryNodeSetUniverseBuilder hasProperty(int nodeId, int propertyNodeId) {
        addReference(new NodeId.IntNodeId(mainNamespaceUri, nodeId), new NodeId.IntNodeId(mainNamespaceUri, propertyNodeId), Nodes.ReferenceTypes.HAS_PROPERTY);
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
