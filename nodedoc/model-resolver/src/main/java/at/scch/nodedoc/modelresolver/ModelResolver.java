package at.scch.nodedoc.modelresolver;

import at.scch.nodedoc.ModelMetaData;
import at.scch.nodedoc.ModelRepository;
import at.scch.nodedoc.nodeset.NodeSetUniverse;
import at.scch.nodedoc.nodeset.UANodeSet;
import at.scch.nodedoc.parser.rawModel.*;
import at.scch.nodedoc.uaStandard.Namespaces;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Quintet;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class ModelResolver {

    private final ModelRepository modelRepository;
    private final NodeIdParser nodeIdParser;
    private final ReferenceResolver referenceResolver;

    public ModelResolver(ModelRepository modelRepository, NodeIdParser nodeIdParser, ReferenceResolver referenceResolver) {
        this.modelRepository = modelRepository;
        this.nodeIdParser = nodeIdParser;
        this.referenceResolver = referenceResolver;
    }

    @Deprecated
    public UANodeSet resolveModel(String modelUri, String version, OffsetDateTime publicationDate) {
        log.info("Resolve model {} ({} / {})", modelUri, version, publicationDate);
        var dependencyResolver = new DependencyResolver(modelRepository);
        var currentModelMetaData = new ModelMetaData(modelUri, version, publicationDate);
        var dependencies = dependencyResolver.collectDependencies(currentModelMetaData);
        var models = Stream.concat(dependencies.stream(), Stream.of(currentModelMetaData)).collect(Collectors.toList());

        var nodeSetUniverse = loadNodeSetUniverse(models);
        return nodeSetUniverse.getNodeSetByNamespaceUri(modelUri);
    }

    public NodeSetUniverse loadNodeSetUniverse(Collection<ModelMetaData> models) {
        log.info("Load NodeSetUniverse with NodeSets {}", models);
        var rawNodeSets = models.stream()
                .map(metaData -> {
                    var rawNodeSet = modelRepository.loadNodeSet(metaData);

                    var namespaceUris = new ArrayList<>(rawNodeSet.getNamespaceUris());
                    namespaceUris.add(0, Namespaces.UA);

                    List<UANodeImpl<?>> nodes = rawNodeSet.getNodes().stream()
                            .map(rawNode -> createNodeFromRawNode(rawNode, namespaceUris))
                            .collect(Collectors.toList());

                    var nodeSetsByNamespaceIndex = new HashMap<Integer, UANodeSet>(); // TODO: Should be removed when UANodeSet.getNodeSetsByNamespaceIndex() has been removed
                    var nodeSet = new UANodeSetImpl(rawNodeSet, nodes, nodeSetsByNamespaceIndex);
                    var aliasMap = buildAliasMap(rawNodeSet);
                    return new Quintet<>(nodeSet, namespaceUris, nodeSetsByNamespaceIndex, aliasMap, nodes);
                }).collect(Collectors.toList());

        var nodeSets = rawNodeSets.stream().collect(Collectors.toMap(
                nodeSet -> nodeSet.getValue0().getModelUri(),
                nodeSet -> nodeSet.getValue0()
        ));

        var references = rawNodeSets.stream().flatMap(q -> {
            var nodeSet = q.getValue0();
            var namespaceUris = q.getValue1();
            var nodeSetsByNamespaceIndex = q.getValue2();
            var aliasMap = q.getValue3();
            var nodes = q.getValue4();
            fillNodeSetsByNamespaceIndex(nodeSetsByNamespaceIndex, namespaceUris, Collections.unmodifiableMap(nodeSets));

            var resolveContext = new ReferenceResolver.ResolveContext(Collections.unmodifiableMap(nodeSets), namespaceUris, aliasMap);
            for (var node : nodes) {
                node.setNodeSet(nodeSet);
            }
            return nodes.stream().flatMap(node -> referenceResolver.resolveReferences(node, resolveContext));
        }).collect(Collectors.toSet());

        references.forEach(reference -> {
            var sourceNode = reference.getSource();
            var targetNode = reference.getTarget();
            var referenceType = reference.getReferenceType();

            sourceNode.getForwardReferences().put(referenceType, targetNode);
            targetNode.getBackwardReferences().put(referenceType, sourceNode);
        });

        var universe = new NodeSetUniverseImpl(nodeSets, references);
        for (UANodeSetImpl nodeSet : nodeSets.values()) {
            nodeSet.setNodeSetUniverse(universe);
        }
        return universe;
    }

    private UANodeImpl<?> createNodeFromRawNode(RawNode rawNode, ArrayList<String> namespaceUris) {
        var nodeId = nodeIdParser.parseNodeId(rawNode.getNodeId(), namespaceUris);
        if (rawNode instanceof RawObject) {
            return new UAObjectImpl((RawObject) rawNode, nodeId);
        } else if (rawNode instanceof RawVariable) {
            return new UAVariableImpl((RawVariable) rawNode, nodeId);
        } else if (rawNode instanceof RawMethod) {
            return new UAMethodImpl((RawMethod) rawNode, nodeId);
        } else if (rawNode instanceof RawView) {
            return new UAViewImpl((RawView) rawNode, nodeId);
        } else if (rawNode instanceof RawObjectType) {
            return new UAObjectTypeImpl((RawObjectType) rawNode, nodeId);
        } else if (rawNode instanceof RawVariableType) {
            return new UAVariableTypeImpl((RawVariableType) rawNode, nodeId);
        } else if (rawNode instanceof RawDataType) {
            return new UADataTypeImpl((RawDataType) rawNode, nodeId);
        } else if (rawNode instanceof RawReferenceType) {
            return new UAReferenceTypeImpl((RawReferenceType) rawNode, nodeId);
        } else {
            throw new RuntimeException("Unknown RawNode class " + rawNode.getClass());
        }
    }

    private Map<String, String> buildAliasMap(RawNodeSet rawNodeSet) {
        return rawNodeSet.getAliases().stream()
                .collect(Collectors.toMap(RawAlias::getAlias, RawAlias::getNodeId));
    }

    private void fillNodeSetsByNamespaceIndex(Map<Integer, UANodeSet> nodeSetsByNamespaceIndex, List<String> namespaceUris, Map<String, UANodeSet> nodeSetsByNamespaceUri) {
        for (int i = 0; i < namespaceUris.size(); i++) {
            nodeSetsByNamespaceIndex.put(i, nodeSetsByNamespaceUri.get(namespaceUris.get(i)));
        }
    }

}
