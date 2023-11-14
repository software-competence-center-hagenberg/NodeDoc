package at.scch.nodedoc.documentation.single.generator;

import at.scch.nodedoc.documentation.single.table.SingleReferenceTableSection;
import at.scch.nodedoc.documentation.single.table.SingleTableCell;
import at.scch.nodedoc.documentation.single.table.SingleTableRow;
import at.scch.nodedoc.html.Utilities;
import at.scch.nodedoc.nodeset.*;
import at.scch.nodedoc.uaStandard.Nodes;
import at.scch.nodedoc.util.OptionalUtils;
import at.scch.nodedoc.util.UAModelUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SingleDisplayReferencesTableSectionGenerator {

    public SingleReferenceTableSection generateReferenceSection(UAType type) {
        var rows = type.getForwardReferences().entries().stream()
                .filter(entry -> !entry.getKey().getNodeId().equals(Nodes.ReferenceTypes.HAS_SUBTYPE))
                .sorted(Comparator
                        .comparing((Map.Entry<UAReferenceType, UANode> entry) -> entry.getKey().getBrowseName())
                        .thenComparing(entry -> entry.getValue().getNodeClass())
                        .thenComparing(entry -> entry.getValue().getBrowseName())
                )
                .map(entry -> {
                    var referenceType = entry.getKey();
                    var referencedNode = entry.getValue();

                    var nodeClass = referencedNode.getNodeClass().toString();

                    var dataTypeBrowseName = Optional.of(referencedNode)
                            .flatMap(OptionalUtils.filterCast(UAVariable.class))
                            .map(UAVariable::getDataType)
                            .map(UANode::getBrowseName)
                            .orElse("NA");

                    var typeDefBrowseName = Optional.of(referencedNode)
                            .flatMap(OptionalUtils.filterCast(UAInstance.class))
                            .map(UAModelUtils::getTypeDefinition)
                            .map(UANode::getBrowseName)
                            .orElse("NA");

                    var modellingRules = referencedNode.getForwardReferencedNodes(Nodes.ReferenceTypes.HAS_MODELLING_RULE).stream()
                            .map(UANode::getBrowseName)
                            .sorted();

                    var accessLevels = Optional.of(referencedNode)
                            .flatMap(OptionalUtils.filterCast(UAVariable.class))
                            .map(UAVariable::getAccessLevel)
                            .map(Utilities::getAccessLevelList)
                            .stream()
                            .flatMap(List::stream);

                    var modellingRulesAndAccessLevels = Stream.concat(modellingRules, accessLevels)
                            .collect(Collectors.joining(", "));
                    if (modellingRulesAndAccessLevels.isEmpty()) {
                        modellingRulesAndAccessLevels = "not specified";
                    }

                    return new SingleTableRow(List.of(
                            new SingleTableCell<>(referenceType.getBrowseName()),
                            new SingleTableCell<>(nodeClass),
                            new SingleTableCell<>(referencedNode.getBrowseName()),
                            new SingleTableCell<>(dataTypeBrowseName),
                            new SingleTableCell<>(typeDefBrowseName),
                            new SingleTableCell<>(modellingRulesAndAccessLevels)
                    ));
                }).collect(Collectors.toList());

        return new SingleReferenceTableSection(rows);
    }

}
