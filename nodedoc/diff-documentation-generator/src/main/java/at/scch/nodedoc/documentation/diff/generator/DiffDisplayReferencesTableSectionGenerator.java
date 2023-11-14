package at.scch.nodedoc.documentation.diff.generator;

import at.scch.nodedoc.diffengine.DiffContext;
import at.scch.nodedoc.documentation.diff.DisplayDifferenceType;
import at.scch.nodedoc.documentation.diff.table.DiffReferenceTableSection;
import at.scch.nodedoc.documentation.diff.table.DiffTableCell;
import at.scch.nodedoc.documentation.diff.table.DiffTableRow;
import at.scch.nodedoc.documentation.diff.table.DiffTableUtils;
import at.scch.nodedoc.html.Utilities;
import at.scch.nodedoc.nodeset.*;
import at.scch.nodedoc.uaStandard.Nodes;
import at.scch.nodedoc.util.UAModelUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DiffDisplayReferencesTableSectionGenerator {

    public DiffReferenceTableSection generateReferenceSection(DiffContext.DiffView<? extends UAType> type, boolean showAllTableRowsAsUnchanged) {
        var rows = type.getDiffContext().getDiffReferencesFromNode(type.getKeyProperty(UANode::getNodeId))
                .stream()
                .filter(entry -> !entry.getValue().getProperty(NodeSetUniverse.Reference::getReferenceType).getKeyProperty(UANode::getNodeId).equals(Nodes.ReferenceTypes.HAS_SUBTYPE))
                .sorted(Comparator
                        .comparing((DiffContext.DiffCollectionEntry<NodeSetUniverse.Reference> entry) -> entry.getValue().getProperty(NodeSetUniverse.Reference::getReferenceType).getProperty(UANode::getBrowseName).getBaseOrElseCompareValue())
                        .thenComparing(entry -> entry.getValue().getProperty(NodeSetUniverse.Reference::getTarget).getKeyProperty(UANode::getNodeClass))
                        .thenComparing(entry -> entry.getValue().getProperty(NodeSetUniverse.Reference::getTarget).getProperty(UANode::getBrowseName).getBaseOrElseCompareValue())
                )
                .map(entry -> {
                    var referenceDiffView = entry.getValue();
                    var refNode = referenceDiffView.getProperty(NodeSetUniverse.Reference::getTarget);
                    var references = refNode.getDiffContext().getDiffReferencesFromNode(refNode.getKeyProperty(UANode::getNodeId));

                    var nodeClass = refNode.getProperty(UANode::getNodeClass);

                    var dataTypeBrowseName = refNode.cast(UAVariable.class)
                            .map(variable -> variable.navigate(UAVariable::getDataType).getMergedProperty(UANode::getBrowseName))
                            .map(browseName -> {
                                switch (entry.getEntryDiffType()) {
                                    case ADDED:
                                        return browseName.toUnchangedKeepCompareValue();
                                    case REMOVED:
                                        return browseName.toUnchangedKeepBaseValue();
                                    default:
                                        return browseName;
                                }
                            })
                            .orElseGet(() -> refNode.getDiffContext().diffViewOf("NA"));

                    DiffContext.DiffView<String> typeDefBrowseName;

                    typeDefBrowseName = refNode.cast(UAInstance.class)
                            .map(object -> object.navigate(UAModelUtils::getTypeDefinition).getMergedProperty(UANode::getBrowseName))
                            .filter(browseName -> Objects.nonNull(browseName.getBaseOrElseCompareValue()))
                            .map(browseName -> {
                                switch (entry.getEntryDiffType()) {
                                    case ADDED:
                                        return browseName.toUnchangedKeepCompareValue();
                                    case REMOVED:
                                        return browseName.toUnchangedKeepBaseValue();
                                    default:
                                        return browseName;
                                }
                            })
                            .orElseGet(() -> refNode.getDiffContext().diffViewOf("NA"));

                    var accessLevels = refNode.cast(UAVariable.class)
                            .map(variable -> variable.getProperty(UAVariable::getAccessLevel).getProperty(Utilities::getAccessLevelList));

                    var baseModellingRules = references.stream()
                            .filter(reference -> reference.getValue().getProperty(NodeSetUniverse.Reference::getReferenceType).getKeyProperty(UANode::getNodeId).equals(Nodes.ReferenceTypes.HAS_MODELLING_RULE))
                            .filter(reference -> reference.getEntryDiffType() != DiffContext.EntryDiffType.ADDED)
                            .map(e -> e.getValue().getProperty(NodeSetUniverse.Reference::getTarget).getProperty(UANode::getBrowseName).getBaseOrElseCompareValue())
                            .sorted()
                            .collect(Collectors.toList());

                    var compareModellingRules = references.stream()
                            .filter(reference -> reference.getValue().getProperty(NodeSetUniverse.Reference::getReferenceType).getKeyProperty(UANode::getNodeId).equals(Nodes.ReferenceTypes.HAS_MODELLING_RULE))
                            .filter(reference -> reference.getEntryDiffType() != DiffContext.EntryDiffType.REMOVED)
                            .map(e -> e.getValue().getProperty(NodeSetUniverse.Reference::getTarget).getProperty(UANode::getBrowseName).getBaseOrElseCompareValue())
                            .sorted()
                            .collect(Collectors.toList());

                    var modellingRules = refNode.getDiffContext().diffViewOf(baseModellingRules, compareModellingRules);

                    DiffContext.DiffView<Stream<String>> accessLevelsAndModellingRules;
                    if (accessLevels.isPresent()) {
                        accessLevelsAndModellingRules = accessLevels.get().combineNonNull(modellingRules, (a, b) -> Stream.concat(a.stream(), b.stream()));
                    } else {
                        accessLevelsAndModellingRules = refNode.combineNonNull(modellingRules, (a, b) -> b.stream());
                    }

                    var modellingRuleDiffView = accessLevelsAndModellingRules
                            .getProperty(s -> s.collect(Collectors.joining(", ")))
                            .getProperty(rules -> rules.isEmpty() ? "not specified" : rules);

                    var referenceDisplayDifferenceType = showAllTableRowsAsUnchanged ? DisplayDifferenceType.UNCHANGED : DisplayDifferenceType.of(entry.getEntryDiffType());

                    return new DiffTableRow(DisplayDifferenceType.UNCHANGED, List.of(
                            new DiffTableCell<>(referenceDiffView.getProperty(NodeSetUniverse.Reference::getReferenceType).getProperty(UANode::getBrowseName), referenceDisplayDifferenceType),
                            DiffTableUtils.diffViewToTableCell(nodeClass),
                            DiffTableUtils.diffViewToTableCell(refNode.getProperty(UANode::getBrowseName)),
                            DiffTableUtils.diffViewToTableCell(dataTypeBrowseName),
                            DiffTableUtils.diffViewToTableCell(typeDefBrowseName),
                            DiffTableUtils.diffViewToTableCell(modellingRuleDiffView)
                    ));
                }).collect(Collectors.toList());

        return new DiffReferenceTableSection(rows);
    }

}
