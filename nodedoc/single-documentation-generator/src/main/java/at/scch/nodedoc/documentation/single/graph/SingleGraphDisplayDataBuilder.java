package at.scch.nodedoc.documentation.single.graph;

import at.scch.nodedoc.documentation.displaymodel.graph.GraphDisplayData;
import at.scch.nodedoc.documentation.template.StringTemplate;
import at.scch.nodedoc.nodeset.*;
import lombok.Getter;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SingleGraphDisplayDataBuilder {

    @Getter
    public static class NodeInformation {
        private final String headingText;
        private final String anchorValue;

        public NodeInformation(String headingText, String anchorValue) {
            this.headingText = headingText;
            this.anchorValue = anchorValue;
        }
    }

    public static class ColorNames {
        public static final String DEFAULT = "default";
    }

    private static final Set<GraphDisplayData.GraphNodeColor> NODE_COLORS = Set.of(
            new GraphDisplayData.GraphNodeColor(ColorNames.DEFAULT, "#e8eef7", "#d1ddef")
    );

    private static final Set<GraphDisplayData.GraphReferenceColor> REFERENCE_COLORS = Set.of(
        new GraphDisplayData.GraphReferenceColor(ColorNames.DEFAULT, "#000000")
    );

    private GraphDisplayData.GraphNode createGraphNode(UANode node, Map<NodeId<?>, NodeInformation> sectionDiffInformation) {
        var anchorValue = Optional.ofNullable(sectionDiffInformation.get(node.getNodeId()))
                .map(NodeInformation::getAnchorValue)
                .orElse("");

        var browseName = StringTemplate.template(node.getBrowseName()).render().toString();
        var tooltip = node.getBrowseName();
        if (node instanceof UAVariable) {
            return new GraphDisplayData.GraphVariableNode(
                    node.getNodeClass().name(),
                    browseName,
                    tooltip,
                    anchorValue,
                    ColorNames.DEFAULT,
                    GraphDisplayData.NodeId.from(node.getNodeId()),
                    GraphDisplayData.NodeId.from(((UAVariable) node).getDataType().getNodeId()));
        } else {
            return new GraphDisplayData.GraphNode(
                    node.getNodeClass().name(),
                    browseName,
                    tooltip,
                    anchorValue,
                    ColorNames.DEFAULT,
                    GraphDisplayData.NodeId.from(node.getNodeId())
            );
        }
    }

    private void collectReferencedNodesWithFilter(UANode currentNode, Map<NodeId<?>, UANode> allNodes, Set<NodeSetUniverse.Reference> allReferences, Predicate<UANode> referencedNodeFilter) {
        var allReferencesStream = Stream.concat(
                currentNode.getBackwardReferences().entries().stream()
                        .filter(reference -> referencedNodeFilter.test(reference.getValue()))
                        .map(reference -> {
                            var source = reference.getValue();
                            var referenceType = reference.getKey();
                            return new NodeSetUniverse.Reference(source, currentNode, referenceType);
                        })
                ,
                currentNode.getForwardReferences().entries().stream()
                        .filter(reference -> referencedNodeFilter.test(reference.getValue()))
                        .map(reference -> {
                            var target = reference.getValue();
                            var referenceType = reference.getKey();
                            return new NodeSetUniverse.Reference(currentNode, target, referenceType);
                        })
        );

        allReferencesStream
                .forEach(reference -> {
                    var sourceNode = reference.getSource();
                    var targetNode = reference.getTarget();
                    var referenceType = reference.getReferenceType();

                    allNodes.put(sourceNode.getNodeId(), sourceNode);
                    allNodes.put(targetNode.getNodeId(), targetNode);
                    allNodes.put(referenceType.getNodeId(), referenceType);
                    allReferences.add(reference);

                    // expand DataType in case of an UAVariable
                    if (targetNode instanceof UAVariable) {
                        var dataType = ((UAVariable) targetNode).getDataType();
                        allNodes.put(dataType.getNodeId(), dataType);
                    }
                });
    }

    private void collectNodes(UANode currentNode, Map<NodeId<?>, UANode> allNodes, Set<NodeSetUniverse.Reference> allReferences) {
        collectReferencedNodesWithFilter(currentNode, allNodes, allReferences, diffView -> true);

        // new ArrayList => create temporary copy of values to prevent mutation while iterating
        new ArrayList<>(allNodes.values()).forEach(nodeDiffView -> {
            if (nodeDiffView instanceof UAInstance) {
                collectReferencedNodesWithFilter(nodeDiffView, allNodes, allReferences, node -> node instanceof UAType);
            }
        });

        allNodes.put(currentNode.getNodeId(), currentNode);
    }

    public GraphDisplayData buildGraphData(UANode type, Map<NodeId<?>, NodeInformation> sectionNodeInformation) {
        var nodes = new HashMap<NodeId<?>, UANode>();
        var references = new HashSet<NodeSetUniverse.Reference>();

        collectNodes(type, nodes, references);

        var graphNodes = nodes.values().stream()
                .map(node -> createGraphNode(node, sectionNodeInformation))
                .collect(Collectors.toSet());

        var graphReferences = references.stream()
                .map(reference -> new GraphDisplayData.GraphReference(
                        GraphDisplayData.NodeId.from(reference.getReferenceType().getNodeId()),
                        GraphDisplayData.NodeId.from(reference.getSource().getNodeId()),
                        GraphDisplayData.NodeId.from(reference.getTarget().getNodeId()),
                        ColorNames.DEFAULT
                )).collect(Collectors.toSet());

        return new GraphDisplayData(graphNodes, graphReferences, NODE_COLORS, REFERENCE_COLORS, GraphDisplayData.NodeId.from(type.getNodeId()));
    }
}
