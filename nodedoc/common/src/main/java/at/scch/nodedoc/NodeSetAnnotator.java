package at.scch.nodedoc;

import at.scch.nodedoc.db.documents.NodeSetText;
import at.scch.nodedoc.db.repository.NodeDescriptionRepository;
import at.scch.nodedoc.nodeset.UADataType;
import at.scch.nodedoc.nodeset.UAMethod;
import at.scch.nodedoc.nodeset.UANodeSet;
import at.scch.nodedoc.nodeset.UAVariable;
import at.scch.nodedoc.uaStandard.Nodes;
import at.scch.nodedoc.util.StreamUtils;

import java.util.List;

public class NodeSetAnnotator {

    private final NodeDescriptionRepository nodeDescriptionRepository;

    public NodeSetAnnotator(NodeDescriptionRepository nodeDescriptionRepository) {
        this.nodeDescriptionRepository = nodeDescriptionRepository;
    }

    public void annotateNodeSetWithCurrentDocumentation(UANodeSet nodeSet) {
        var allDescriptions = nodeDescriptionRepository.getUserDescriptionsForNodeSet(nodeSet.getModelUri(), nodeSet.getVersion(), nodeSet.getPublicationDate());

        nodeSet.getAllNodes().forEach(node -> {
            allDescriptions.stream()
                    .filter(nodeDescriptionRepository.matchNodeForDescription(node))
                    .map(NodeSetText::getUserText)
                    .findFirst()
                    .ifPresent(node::setDescription);

            allDescriptions.stream()
                    .filter(nodeDescriptionRepository.matchNodeForDocumentation(node))
                    .map(NodeSetText::getUserText)
                    .findFirst()
                    .ifPresent(node::setDocumentation);

            if (node instanceof UADataType) {
                ((UADataType) node).getDefinition().forEach(definitionField -> {
                    allDescriptions.stream()
                            .filter(nodeDescriptionRepository.matchForField((UADataType) node, definitionField))
                            .map(NodeSetText::getUserText)
                            .findFirst()
                            .ifPresent(definitionField::setDescription);
                });
            }

            if (node instanceof UAMethod) {
                node.getForwardReferencedNodes(Nodes.ReferenceTypes.HAS_PROPERTY).stream()
                        .flatMap(StreamUtils.filterCast(UAVariable.class))
                        .map(UAVariable::getArguments)
                        .flatMap(List::stream).forEach(argument -> {
                    allDescriptions.stream()
                            .filter(nodeDescriptionRepository.matchForArgument((UAMethod) node, argument))
                            .map(NodeSetText::getUserText)
                            .findFirst()
                            .ifPresent(argument::setDescription);
                });
            }
        });
    }

}
