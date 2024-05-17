package at.scch.nodedoc.documentation.single.generator;

import at.scch.nodedoc.db.documents.HistoryEntry;
import at.scch.nodedoc.db.documents.NodeSetText;
import at.scch.nodedoc.db.documents.TextId;
import at.scch.nodedoc.db.repository.NodeDescriptionRepository;
import at.scch.nodedoc.documentation.displaymodel.graph.GraphDisplayData;
import at.scch.nodedoc.documentation.single.*;
import at.scch.nodedoc.documentation.single.graph.SingleGraphDisplayDataBuilder;
import at.scch.nodedoc.nodeset.*;
import at.scch.nodedoc.uaStandard.BrowseNames;
import at.scch.nodedoc.uaStandard.Namespaces;
import at.scch.nodedoc.util.StreamUtils;
import at.scch.nodedoc.util.UAModelUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class SingleDisplayNodeSetGenerator {

    private final SingleDisplayTypeGenerator singleDisplayTypeGenerator;
    private final SingleGraphDisplayDataBuilder singleGraphDisplayDataBuilder;
    private final DocEntryEditorGenerator docEntryEditorGenerator;
    private final NodeDescriptionRepository nodeDescriptionRepository;
    public SingleDisplayNodeSetGenerator(SingleDisplayTypeGenerator singleDisplayTypeGenerator, SingleGraphDisplayDataBuilder singleGraphDisplayDataBuilder, DocEntryEditorGenerator docEntryEditorGenerator, NodeDescriptionRepository nodeDescriptionRepository) {
        this.singleDisplayTypeGenerator = singleDisplayTypeGenerator;
        this.singleGraphDisplayDataBuilder = singleGraphDisplayDataBuilder;
        this.docEntryEditorGenerator = docEntryEditorGenerator;
        this.nodeDescriptionRepository = nodeDescriptionRepository;
    }

    private static final String NAMESPACE_INDEX_FREETEXT_ID = "Indices_of_used_Namespaces";

    public SingleDisplayNodeSet generateSingleDocumentation(NodeSetUniverse universe, String namespaceUri) {
        log.info("Generate single documentation for {} from NodeSetUniverse", namespaceUri);
        var nodeSet = universe.getNodeSetByNamespaceUri(namespaceUri);

        importDocEntries(nodeSet);

        Map<NodeId<?>, UAType> types = filteredNodeStream(universe, NodeSetUniverse::getUATypes, namespaceUri)
                .collect(Collectors.toMap(
                        UANode::getNodeId,
                        Function.identity()
                ));

        List<GenericSingleDisplayType> containerTypes = filteredNodeStream(universe, NodeSetUniverse::getUAObjectTypes, namespaceUri)
                .filter(UAModelUtils::isContainerType)
                .map(singleDisplayTypeGenerator::generateGenericType)
                .sorted(Utils.byBrowseName())
                .collect(Collectors.toUnmodifiableList());


        List<GenericSingleDisplayType> objectTypes = filteredNodeStream(universe, NodeSetUniverse::getUAObjectTypes, namespaceUri)
                .filter(entry -> !UAModelUtils.isContainerType(entry))
                .map(singleDisplayTypeGenerator::generateGenericType)
                .sorted(Utils.byBrowseName())
                .collect(Collectors.toUnmodifiableList());

        List<SingleDisplayVariableType> variableTypes = filteredNodeStream(universe, NodeSetUniverse::getUAVariableTypes, namespaceUri)
                .map(singleDisplayTypeGenerator::generateVariableType)
                .sorted(Utils.byBrowseName())
                .collect(Collectors.toUnmodifiableList());

        List<SingleDisplayDataType> dataTypes = filteredNodeStream(universe, NodeSetUniverse::getUADataTypes, namespaceUri)
                .map(singleDisplayTypeGenerator::generateDataType)
                .sorted(Utils.byBrowseName())
                .collect(Collectors.toUnmodifiableList());

        List<GenericSingleDisplayType> referenceTypes = filteredNodeStream(universe, NodeSetUniverse::getUAReferenceTypes, namespaceUri)
                .map(singleDisplayTypeGenerator::generateGenericType)
                .sorted(Utils.byBrowseName())
                .collect(Collectors.toUnmodifiableList());

        var allTypes = new ArrayList<SingleDisplayType<?>>();
        allTypes.addAll(containerTypes);
        allTypes.addAll(objectTypes);
        allTypes.addAll(variableTypes);
        allTypes.addAll(dataTypes);
        allTypes.addAll(referenceTypes);

        allTypes.forEach(typeSection -> typeSection.getGraphFigure().setGraphData(generateGraphData(types, typeSection, allTypes)));

        var namespaceIndicesEditor = docEntryEditorGenerator.getDivForFreetext(NAMESPACE_INDEX_FREETEXT_ID, 2);
        var usedNamespaces = new ArrayList<>(nodeSet.getNamespaceIndexTable());
        usedNamespaces.add(0, Namespaces.UA);

        var singleContent = new SingleDisplayNodeSet.SingleContent(
                nodeSet.getModelUri(),
                nodeSet.getVersion(),
                nodeSet.getPublicationDate().toString(), containerTypes, objectTypes, variableTypes, dataTypes, referenceTypes,
                namespaceIndicesEditor, usedNamespaces);

        return new SingleDisplayNodeSet(singleContent);
    }

    private GraphDisplayData generateGraphData(Map<NodeId<?>, UAType> types, SingleDisplayType<?> typeSection, ArrayList<SingleDisplayType<?>> displayTypes) {
        Stream<SingleDisplayInstance> instanceStream = typeSection.getSubSections().stream()
                .flatMap(StreamUtils.filterCast(SingleDisplayInstance.class));

        HashMap<NodeId<?>, SingleGraphDisplayDataBuilder.NodeInformation> sectionDiffInformation = Stream.concat(displayTypes.stream(), instanceStream)
                .collect(Collectors.toMap(
                        SingleDisplayNode::getNodeId,
                        diffDisplayNode -> new SingleGraphDisplayDataBuilder.NodeInformation(diffDisplayNode.getHeadingTextValue(), diffDisplayNode.getAnchorValue()),
                        (a, b) -> a,
                        HashMap::new
                ));
        var typeDiffView = types.get(typeSection.getNodeId());
        return singleGraphDisplayDataBuilder.buildGraphData(typeDiffView, sectionDiffInformation);
    }

    private <Node extends UANode> Stream<Node> filteredNodeStream(NodeSetUniverse universe, Function<NodeSetUniverse, Set<Node>> setExtractor, String namespaceUri) {
        return setExtractor.apply(universe).stream()
                .filter(node -> node.getNodeId().getNamespaceUri().equals(namespaceUri));
    }

    private void importDocEntries(UANodeSet nodeSet) {
        var existingDocEntries = nodeDescriptionRepository.getAllNodeSetTextsForNodeSet(nodeSet.getModelUri(), nodeSet.getVersion(), nodeSet.getPublicationDate());
        var docEntriesToInsert = new ArrayList<NodeSetText>();

        nodeSet.getUATypes().forEach(type -> {
            addNodeSetTextIfNotExists(nodeSet, TextId.forNodeDescription(type.getNodeId()), type.getDescription(), existingDocEntries, docEntriesToInsert);
            addNodeSetTextIfNotExists(nodeSet, TextId.forNodeDocumentation(type.getNodeId()), type.getDocumentation(), existingDocEntries, docEntriesToInsert);

            if (type instanceof UADataType) {
                ((UADataType) type).getDefinition().forEach(field -> {
                    addNodeSetTextIfNotExists(nodeSet, TextId.forField(type.getNodeId(), field), field.getDescription(), existingDocEntries, docEntriesToInsert);
                });
            }

            type.getForwardReferences().values().stream()
                    .flatMap(StreamUtils.filterCast(UAInstance.class))
                    .forEach(instance -> {
                        addNodeSetTextIfNotExists(nodeSet, TextId.forNodeDescription(instance.getNodeId()), instance.getDescription(), existingDocEntries, docEntriesToInsert);
                        addNodeSetTextIfNotExists(nodeSet, TextId.forNodeDocumentation(instance.getNodeId()), instance.getDocumentation(), existingDocEntries, docEntriesToInsert);

                        if (instance instanceof UAMethod) {
                            instance.getForwardReferences().values().stream()
                                    .filter(node -> node.getBrowseName().equals(BrowseNames.ArgumentNames.INPUT_ARGUMENT) || node.getBrowseName().equals(BrowseNames.ArgumentNames.OUTPUT_ARGUMENT))
                                    .flatMap(StreamUtils.filterCast(UAVariable.class))
                                    .flatMap(variable -> variable.getArguments().stream())
                                    .forEach(argument -> {
                                        addNodeSetTextIfNotExists(nodeSet, TextId.forArgument(instance.getNodeId(), argument), argument.getDescription(), existingDocEntries, docEntriesToInsert);
                                    });
                        }
                    });
        });

        addNodeSetTextIfNotExists(nodeSet, TextId.forFreetext(NAMESPACE_INDEX_FREETEXT_ID), null, existingDocEntries, docEntriesToInsert);

        nodeDescriptionRepository.save(docEntriesToInsert);
    }

    private void addNodeSetTextIfNotExists(UANodeSet nodeSet, TextId textId, String xmlText, List<NodeSetText> existingDocEntries, List<NodeSetText> docEntriesToInsert) {
        var nodeSetText = existingDocEntries.stream()
                .filter(docEntry -> docEntry.getTextId().equals(textId))
                .findAny();

        if (nodeSetText.isPresent()) {
            // if history does not exist yet, add it to the node.
            var nd = nodeSetText.get();
            if (nd.getUserTextHistory() == null) {
                var history = nd.getUserText() == null
                        ? List.<HistoryEntry>of()
                        : List.of(new HistoryEntry(nodeSet.getPublicationDate().toString(), nd.getUserText()));
                nd.setUserTextHistory(history);
                docEntriesToInsert.add(nd);
            }
        } else {
            NodeSetText nd = new NodeSetText();
            nd.setTextId(textId);
            nd.setXmlText(xmlText);
            nd.setUserText(null);
            nd.setUserTextHistory(List.of());
            nd.setNamespaceUri(nodeSet.getModelUri());
            nd.setVersion(nodeSet.getVersion());
            nd.setPublicationDate(nodeSet.getPublicationDate().toString());
            docEntriesToInsert.add(nd);
        }
    }
}
