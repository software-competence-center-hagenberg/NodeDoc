package at.scch.nodedoc.documentation.diff.generator;

import at.scch.nodedoc.diffengine.DiffContext;
import at.scch.nodedoc.documentation.diff.*;
import at.scch.nodedoc.documentation.diff.graph.DiffGraphDisplayDataBuilder;
import at.scch.nodedoc.documentation.displaymodel.graph.GraphDisplayData;
import at.scch.nodedoc.nodeset.*;
import at.scch.nodedoc.util.UAModelUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class DiffDisplayNodeSetGenerator {

    private final DiffDisplayTypeGenerator diffDisplayTypeGenerator;
    private final DiffGraphDisplayDataBuilder diffGraphDisplayDataBuilder;

    public DiffDisplayNodeSetGenerator(DiffDisplayTypeGenerator diffDisplayTypeGenerator, DiffGraphDisplayDataBuilder diffGraphDisplayDataBuilder) {
        this.diffDisplayTypeGenerator = diffDisplayTypeGenerator;
        this.diffGraphDisplayDataBuilder = diffGraphDisplayDataBuilder;
    }

    public DiffDisplayNodeSet generateDiffDocumentation(DiffContext diffContext, String namespaceUri) {
        log.info("Generate Diff from DiffContext for Namespace URI {}", namespaceUri);
        var diffContent = generateDiffContent(diffContext, namespaceUri);
        return new DiffDisplayNodeSet(diffContent);
    }

    private DiffDisplayNodeSet.DiffContent generateDiffContent(DiffContext diffContext, String namespaceUri) {
        var diffNodeSet = diffContext.getUniverse().getProperty(universe -> universe.getNodeSetByNamespaceUri(namespaceUri));

        Map<NodeId<?>, DiffContext.DiffCollectionEntry<UAInstance>> diffInstances = filteredNodeStream(diffContext, NodeSetUniverse::getInstances, namespaceUri)
                .collect(Collectors.toMap(
                        instance -> instance.getValue().getKeyProperty(UANode::getNodeId),
                        Function.identity()
                ));

        Map<NodeId<?>, DiffContext.DiffCollectionEntry<UAType>> diffTypes = filteredNodeStream(diffContext, NodeSetUniverse::getUATypes, namespaceUri)
                .collect(Collectors.toMap(
                        type -> type.getValue().getKeyProperty(UANode::getNodeId),
                        Function.identity()
                ));

        List<GenericDiffDisplayType> containerTypes = filteredNodeStream(diffContext, NodeSetUniverse::getUAObjectTypes, namespaceUri)
                .filter(entry -> UAModelUtils.isContainerType(entry.getValue().getBaseOrElseCompareValue()))
                .map(type -> diffDisplayTypeGenerator.generateGenericType(type.getEntryDiffType(), type.getValue(), diffInstances))
                .sorted(Utils.byBrowseName())
                .collect(Collectors.toUnmodifiableList());


        List<GenericDiffDisplayType> objectTypes = filteredNodeStream(diffContext, NodeSetUniverse::getUAObjectTypes, namespaceUri)
                .filter(entry -> !UAModelUtils.isContainerType(entry.getValue().getBaseOrElseCompareValue()))
                .map(type -> diffDisplayTypeGenerator.generateGenericType(type.getEntryDiffType(), type.getValue(), diffInstances))
                .sorted(Utils.byBrowseName())
                .collect(Collectors.toUnmodifiableList());

        List<DiffDisplayVariableType> variableTypes = filteredNodeStream(diffContext, NodeSetUniverse::getUAVariableTypes, namespaceUri)
                .map(type -> diffDisplayTypeGenerator.generateVariableType(type.getEntryDiffType(), type.getValue(), diffInstances))
                .sorted(Utils.byBrowseName())
                .collect(Collectors.toUnmodifiableList());

        List<DiffDisplayDataType> dataTypes = filteredNodeStream(diffContext, NodeSetUniverse::getUADataTypes, namespaceUri)
                .map(type -> diffDisplayTypeGenerator.generateDataType(type.getEntryDiffType(), type.getValue(), diffInstances))
                .sorted(Utils.byBrowseName())
                .collect(Collectors.toUnmodifiableList());

        List<GenericDiffDisplayType> referenceTypes = filteredNodeStream(diffContext, NodeSetUniverse::getUAReferenceTypes, namespaceUri)
                .map(type -> diffDisplayTypeGenerator.generateGenericType(type.getEntryDiffType(), type.getValue(), diffInstances))
                .sorted(Utils.byBrowseName())
                .collect(Collectors.toUnmodifiableList());

        var allTypes = new ArrayList<DiffDisplayType<?>>();
        allTypes.addAll(containerTypes);
        allTypes.addAll(objectTypes);
        allTypes.addAll(variableTypes);
        allTypes.addAll(dataTypes);
        allTypes.addAll(referenceTypes);

        allTypes.forEach(typeSection -> typeSection.getGraphFigure().setGraphData(generateGraphData(diffTypes, typeSection, allTypes)));

        return new DiffDisplayNodeSet.DiffContent(
                diffNodeSet.getProperty(UANodeSet::getModelUri),
                diffNodeSet.getProperty(UANodeSet::getVersion),
                containerTypes, objectTypes, variableTypes, dataTypes, referenceTypes
        );
    }

    private GraphDisplayData generateGraphData(Map<NodeId<?>, DiffContext.DiffCollectionEntry<UAType>> diffTypes, DiffDisplayType<?> typeSection, ArrayList<DiffDisplayType<?>> types) {
        Stream<DiffDisplayInstance> instanceStream = typeSection.getSubSections().stream()
                .filter(section -> section instanceof DiffDisplayInstance)
                .map(section -> (DiffDisplayInstance)section);

        HashMap<NodeId<?>, DiffGraphDisplayDataBuilder.NodeInformation> sectionDiffInformation = Stream.concat(types.stream(), instanceStream)
                .collect(Collectors.toMap(
                        section -> section.getNodeId(),
                        diffDisplayNode -> new DiffGraphDisplayDataBuilder.NodeInformation(diffDisplayNode.getHeadingTextValue(), diffDisplayNode.getDisplayDifferenceType(), diffDisplayNode.getAnchorValue()),
                        (a, b) -> a,
                        HashMap::new
                ));
        var typeDiffView = diffTypes.get(typeSection.getNodeId()).getValue();
        return diffGraphDisplayDataBuilder.buildGraphData(typeDiffView, sectionDiffInformation);
    }

    private <Node extends UANode> Stream<DiffContext.DiffCollectionEntry<Node>> filteredNodeStream(DiffContext diffContext, Function<NodeSetUniverse, Set<Node>> setExtractor, String namespaceUri) {
        return diffContext.getUniverse().getDiffSetWithNodes(setExtractor).stream()
                .filter(entry -> entry.getValue().getKeyProperty(UANode::getNodeId).getNamespaceUri().equals(namespaceUri));
    }
}
