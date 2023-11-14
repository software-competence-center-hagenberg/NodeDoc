package at.scch.nodedoc.documentation.diff.graph;

import at.scch.nodedoc.diffengine.DiffContext;
import at.scch.nodedoc.documentation.diff.DiffGraphBrowseNameTemplate;
import at.scch.nodedoc.documentation.diff.DisplayDifferenceType;
import at.scch.nodedoc.documentation.displaymodel.graph.GraphDisplayData;
import at.scch.nodedoc.nodeset.*;
import lombok.Getter;
import org.javatuples.Pair;
import org.javatuples.Quartet;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DiffGraphDisplayDataBuilder {

    @Getter
    public static class NodeInformation {
        private final DiffContext.DiffView<String> headingText;
        private final DisplayDifferenceType displayDifferenceType;
        private final String anchorValue;

        public NodeInformation(DiffContext.DiffView<String> headingText, DisplayDifferenceType displayDifferenceType, String anchorValue) {
            this.headingText = headingText;
            this.displayDifferenceType = displayDifferenceType;
            this.anchorValue = anchorValue;
        }
    }

    public static class ColorNames {
        public static final String DEFAULT = "default";
        public static final String CHANGED = "changed";
        public static final String ADDED = "added";
        public static final String REMOVED = "removed";
    }

    private static final Set<GraphDisplayData.GraphNodeColor> NODE_COLORS = Set.of(
            new GraphDisplayData.GraphNodeColor(ColorNames.DEFAULT, "#e8eef7", "#d1ddef"),
            new GraphDisplayData.GraphNodeColor(ColorNames.CHANGED, "hsla(0, 0%, 82%, 1)", "hsla(0, 0%, 75%, 1)"),
            new GraphDisplayData.GraphNodeColor(ColorNames.ADDED, "hsla(90, 58%, 70%, 1)", "hsla(90, 58%, 63%, 1)"),
            new GraphDisplayData.GraphNodeColor(ColorNames.REMOVED, "hsla(327, 61%, 85%, 1)", "hsla(327, 61%, 78%, 1)")
    );

    private static final Set<GraphDisplayData.GraphReferenceColor> REFERENCE_COLORS = Set.of(
        new GraphDisplayData.GraphReferenceColor(ColorNames.DEFAULT, "#000000"),
        new GraphDisplayData.GraphReferenceColor(ColorNames.ADDED, "hsla(90, 58%, 45%, 1)"),
        new GraphDisplayData.GraphReferenceColor(ColorNames.REMOVED, "hsla(327, 61%, 60%, 1)")
    );

    public GraphDisplayData.NodeId<?> getGraphNodeId(DiffContext.DiffView<? extends UANode> diffNode) {
        return GraphDisplayData.NodeId.from(diffNode.getKeyProperty(UANode::getNodeId));
    }

    private static DiffContext.DiffView<String> getBrowseNameDiffView(DiffContext.DiffView<? extends UANode> diffNode, Map<NodeId<?>, NodeInformation> sectionDiffInformation) {
        return Optional.ofNullable(sectionDiffInformation.get(diffNode.getKeyProperty(UANode::getNodeId)))
                .map(NodeInformation::getHeadingText)
                .orElseGet(() -> diffNode.getProperty(UANode::getBrowseName));
    }

    private String createDiffNodeNameText(DiffContext.DiffView<String> browseName) {
        return DiffGraphBrowseNameTemplate.template(browseName).render().toString();
    }

    private String createDiffToolTipText(DiffContext.DiffView<String> browseName) {
        if (browseName.getDiffType().equals(DiffContext.ValueDiffType.CHANGED)) {
            return browseName.getBaseValue() +
                    " ➔ " + browseName.getCompareValue();
        } else {
            return browseName.getBaseOrElseCompareValue();
        }
    }

    private String getNodeColorName(DiffContext.DiffView<? extends UANode> diffNode, Map<NodeId<?>, NodeInformation> sectionDiffInformation) {
        var sectionDiffType = Optional.ofNullable(sectionDiffInformation.get(diffNode.getKeyProperty(UANode::getNodeId)))
                .map(NodeInformation::getDisplayDifferenceType)
                .orElse(DisplayDifferenceType.UNCHANGED);

        switch (sectionDiffType) {
            case ADDED:
                return ColorNames.ADDED;
            case REMOVED:
                return ColorNames.REMOVED;
            case CHANGED:
                return ColorNames.CHANGED;
            case UNCHANGED:
                return ColorNames.DEFAULT;
            default:
                throw new IllegalStateException("Unexpected value: " + diffNode.getDiffType());
        }
    }

    private String getEdgeColorName(DiffContext.EntryDiffType entryDiffType) {
        switch (entryDiffType) {
            case ADDED:
                return ColorNames.ADDED;
            case REMOVED:
                return ColorNames.REMOVED;
            case UNCHANGED:
                return ColorNames.DEFAULT;
            default:
                throw new IllegalStateException("Unexpected value: " + entryDiffType);
        }
    }

    private GraphDisplayData.GraphNode createGraphNode(DiffContext.DiffView<? extends UANode> diffNode, Map<NodeId<?>, NodeInformation> sectionDiffInformation) {
        var anchorValue = Optional.ofNullable(sectionDiffInformation.get(diffNode.getKeyProperty(UANode::getNodeId)))
                .map(NodeInformation::getAnchorValue)
                .orElse("");

        return DiffContext.readOnlyCast(diffNode, UAVariable.class)
                .map(diffVariable -> {
                    var browseName = getBrowseNameDiffView(diffVariable, sectionDiffInformation);
                    return (GraphDisplayData.GraphNode) new GraphDisplayData.GraphVariableNode(
                            diffVariable.getKeyProperty(UANode::getNodeClass).name(),
                            createDiffNodeNameText(browseName),
                            createDiffToolTipText(browseName),
                            anchorValue,
                            getNodeColorName(diffVariable, sectionDiffInformation),
                            getGraphNodeId(diffVariable),
                            GraphDisplayData.NodeId.from(diffVariable.navigate(UAVariable::getDataType).getMergedProperty(UANode::getNodeId).getBaseOrElseCompareValue())); // TODO: which datatype should be shown (old/new)?
                })
                .orElseGet(() -> {
                    var browseName = getBrowseNameDiffView(diffNode, sectionDiffInformation);
                    return new GraphDisplayData.GraphNode(
                            diffNode.getKeyProperty(UANode::getNodeClass).name(),
                            createDiffNodeNameText(browseName),
                            createDiffToolTipText(browseName),
                            anchorValue,
                            getNodeColorName(diffNode, sectionDiffInformation),
                            getGraphNodeId(diffNode)
                    );
                });
    }

    private void collectReferencedNodesWithFilter(DiffContext.DiffView<? extends UANode> currentNode, Map<NodeId<?>, DiffContext.DiffView<? extends UANode>> allNodes, Set<Pair<DiffContext.DiffView<NodeSetUniverse.Reference>, DiffContext.EntryDiffType>> allReferences, Predicate<DiffContext.DiffView<? extends UANode>> referencedNodeFilter) {
        var currentNodeId = currentNode.getKeyProperty(UANode::getNodeId);

        var allReferencesStream = Stream.concat(
                currentNode.getDiffContext().getDiffReferencesToNode(currentNodeId).stream().map(reference -> {
                    var source = reference.getValue().navigateInKey(NodeSetUniverse.Reference::getSource);
                    var referenceType = reference.getValue().navigateInKey(NodeSetUniverse.Reference::getReferenceType);
                    return new Quartet<>(reference.getValue(), source, referenceType, reference.getEntryDiffType());
                }),
                currentNode.getDiffContext().getDiffReferencesFromNode(currentNodeId).stream().map(reference -> {
                    var target = reference.getValue().navigateInKey(NodeSetUniverse.Reference::getTarget);
                    var referenceType = reference.getValue().navigateInKey(NodeSetUniverse.Reference::getReferenceType);
                    return new Quartet<>(reference.getValue(), target, referenceType, reference.getEntryDiffType());
                })
        );

        allReferencesStream
                .filter(reference -> referencedNodeFilter.test(reference.getValue1()))
                .forEach(quartet -> {
                    var reference = quartet.getValue0();
                    var referencedNode = quartet.getValue1();
                    var referenceType = quartet.getValue2();
                    var entryDiffType = quartet.getValue3();

                    allNodes.put(referencedNode.getKeyProperty(UANode::getNodeId), referencedNode);
                    allNodes.put(referenceType.getKeyProperty(UANode::getNodeId), referenceType);
                    allReferences.add(new Pair<>(reference, entryDiffType));

                    // expand DataType in case of an UAVariable
                    DiffContext.readOnlyCast(referencedNode, UAVariable.class)
                            .ifPresent(variable -> {
                                var dataType = variable.navigate(UAVariable::getDataType);
                                var dataTypeInBase = dataType.getDiffViewInBase();
                                var dataTypeInCompare = dataType.getDiffViewInCompare();
                                if (dataTypeInBase != null) {
                                    allNodes.put(dataTypeInBase.getKeyProperty(UANode::getNodeId), dataTypeInBase);
                                }
                                if (dataTypeInCompare != null) {
                                    allNodes.put(dataTypeInCompare.getKeyProperty(UANode::getNodeId), dataTypeInCompare);
                                }
                            });
                });
    }

    private void collectNodes(DiffContext.DiffView<? extends UANode> currentNode, Map<NodeId<?>, DiffContext.DiffView<? extends UANode>> allNodes, Set<Pair<DiffContext.DiffView<NodeSetUniverse.Reference>, DiffContext.EntryDiffType>> allReferences) {
        collectReferencedNodesWithFilter(currentNode, allNodes, allReferences, diffView -> true);

        // new ArrayList => create temporary copy of values to prevent mutation while iterating
        new ArrayList<>(allNodes.values()).forEach(nodeDiffView -> {
            if (nodeDiffView.getBaseOrElseCompareValue() instanceof UAInstance) {
                collectReferencedNodesWithFilter(nodeDiffView, allNodes, allReferences, node -> node.getBaseOrElseCompareValue() instanceof UAType);
            }
        });

        allNodes.put(currentNode.getKeyProperty(UANode::getNodeId), currentNode);
    }

    public GraphDisplayData buildGraphData(DiffContext.DiffView<? extends UANode> diffType, Map<NodeId<?>, NodeInformation> sectionDiffInformation) {
        var nodes = new HashMap<NodeId<?>, DiffContext.DiffView<? extends UANode>>();
        var references = new HashSet<Pair<DiffContext.DiffView<NodeSetUniverse.Reference>, DiffContext.EntryDiffType>>();

        collectNodes(diffType, nodes, references);

        var graphNodes = nodes.values().stream()
                .map(diffNode -> createGraphNode(diffNode, sectionDiffInformation))
                .collect(Collectors.toSet());

        var graphReferences = references.stream()
                .map(reference -> new GraphDisplayData.GraphReference(
                        getGraphNodeId(reference.getValue0().navigateInKey(NodeSetUniverse.Reference::getReferenceType)),
                        getGraphNodeId(reference.getValue0().navigateInKey(NodeSetUniverse.Reference::getSource)),
                        getGraphNodeId(reference.getValue0().navigateInKey(NodeSetUniverse.Reference::getTarget)),
                        getEdgeColorName(reference.getValue1())
                )).collect(Collectors.toSet());

        return new GraphDisplayData(graphNodes, graphReferences, NODE_COLORS, REFERENCE_COLORS, getGraphNodeId(diffType));
    }
}
