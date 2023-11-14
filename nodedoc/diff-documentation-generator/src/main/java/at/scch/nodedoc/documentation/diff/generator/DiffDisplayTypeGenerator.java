package at.scch.nodedoc.documentation.diff.generator;

import at.scch.nodedoc.diffengine.DiffContext;
import at.scch.nodedoc.documentation.diff.*;
import at.scch.nodedoc.documentation.diff.table.*;
import at.scch.nodedoc.documentation.displaymodel.table.DataTypeDefinitionTable;
import at.scch.nodedoc.documentation.displaymodel.table.GenericTypeDefinitionTable;
import at.scch.nodedoc.documentation.displaymodel.table.VariableTypeDefinitionTable;
import at.scch.nodedoc.html.Utilities;
import at.scch.nodedoc.nodeset.*;
import at.scch.nodedoc.util.UAModelUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class DiffDisplayTypeGenerator {

    private final DiffDisplayReferencesTableSectionGenerator diffDisplayReferencesTableSectionGenerator;
    private final DiffDisplayDataTypeTableSectionGenerator diffDisplayDataTypeTableSectionGenerator;
    private final DiffDisplayInstanceGenerator diffDisplayInstanceGenerator;

    public DiffDisplayTypeGenerator(DiffDisplayReferencesTableSectionGenerator diffDisplayReferencesTableSectionGenerator, DiffDisplayDataTypeTableSectionGenerator diffDisplayDataTypeTableSectionGenerator, DiffDisplayInstanceGenerator diffDisplayInstanceGenerator) {
        this.diffDisplayReferencesTableSectionGenerator = diffDisplayReferencesTableSectionGenerator;
        this.diffDisplayDataTypeTableSectionGenerator = diffDisplayDataTypeTableSectionGenerator;
        this.diffDisplayInstanceGenerator = diffDisplayInstanceGenerator;
    }

    private static class CommonParts<T extends UAType> {
        public final DiffContext.DiffView<String> headingText;
        public final DiffTypeAttributesTableSection<?> typeTableAttributesSection;
        public final boolean showAllTableRowsAsUnchanged;
        public final DiffSubTypeTableSection<T> subTypeTableSection;
        public final DiffReferenceTableSection referencesTableSection;
        public final DiffContext.DiffView<String> documentationTextValue;
        public final DiffContext.DiffView<String> descriptionTextValue;
        public final DiffContext.ValueDiffType innerDiffType;
        public final List<? extends DiffDisplayInstance> instanceSections;
        public final String anchorValue;
        public final DiffGraphFigure graphFigure;

        public CommonParts(DiffContext.DiffView<String> headingText, DiffTypeAttributesTableSection<?> typeTableAttributesSection, boolean showAllTableRowsAsUnchanged, DiffSubTypeTableSection<T> subTypeTableSection, DiffReferenceTableSection referencesTableSection, DiffContext.DiffView<String> documentationTextValue, DiffContext.DiffView<String> descriptionTextValue, DiffContext.ValueDiffType innerDiffType, List<? extends DiffDisplayInstance> instanceSections, String anchorValue, DiffGraphFigure graphFigure) {
            this.headingText = headingText;
            this.typeTableAttributesSection = typeTableAttributesSection;
            this.showAllTableRowsAsUnchanged = showAllTableRowsAsUnchanged;
            this.subTypeTableSection = subTypeTableSection;
            this.referencesTableSection = referencesTableSection;
            this.documentationTextValue = documentationTextValue;
            this.descriptionTextValue = descriptionTextValue;
            this.innerDiffType = innerDiffType;
            this.instanceSections = instanceSections;
            this.anchorValue = anchorValue;
            this.graphFigure = graphFigure;
        }
    }

    private <T extends UAType> CommonParts<T> generateCommonParts(DiffContext.EntryDiffType entryDiffType, DiffContext.DiffView<T> typeEntry, Map<NodeId<?>, DiffContext.DiffCollectionEntry<UAInstance>> instances) {
        var headingText = typeEntry.getProperty(UANode::getBrowseName);
        var typeTableAttributesSection = generateAttributeSection(typeEntry);
        var showAllTableRowsAsUnchanged = entryDiffType == DiffContext.EntryDiffType.ADDED || entryDiffType == DiffContext.EntryDiffType.REMOVED;
        var subTypeTableSection = new DiffSubTypeTableSection<>(typeEntry, showAllTableRowsAsUnchanged);
        var referencesTableSection = diffDisplayReferencesTableSectionGenerator.generateReferenceSection(typeEntry, showAllTableRowsAsUnchanged);

        var documentationTextValue = typeEntry.getProperty(UANode::getDocumentation).replaceNull("");
        var descriptionTextValue = typeEntry.getProperty(UANode::getDescription).replaceNull("");

        var innerDiffType = DiffContext.ValueDiffType.getCombinedDiffType(
                typeTableAttributesSection.getDiffType(),
                subTypeTableSection.getDiffType(),
                referencesTableSection.getDiffType(),
                documentationTextValue.getDiffType(),
                descriptionTextValue.getDiffType());

        var instanceSections = generateInstanceSectionsForType(instances, typeEntry);

        var anchorValue = Utilities.getNodeIdAnchor(typeEntry.getKeyProperty(UANode::getNodeId));

        var graphFigure = new DiffGraphFigure(headingText);
        return new CommonParts<>(headingText, typeTableAttributesSection, showAllTableRowsAsUnchanged, subTypeTableSection, referencesTableSection, documentationTextValue, descriptionTextValue, innerDiffType, instanceSections, anchorValue, graphFigure);
    }

    public GenericDiffDisplayType generateGenericType(DiffContext.EntryDiffType entryDiffType, DiffContext.DiffView<? extends UAType> typeEntry, Map<NodeId<?>, DiffContext.DiffCollectionEntry<UAInstance>> instances) {
        var commonParts = generateCommonParts(entryDiffType, typeEntry, instances);
        var displayDifferenceType = calculateDisplayDifferenceType(entryDiffType, commonParts.innerDiffType, commonParts.instanceSections);
        var definitionTable = new GenericTypeDefinitionTable(DiffDisplayUtils.toRenderable(commonParts.headingText), commonParts.typeTableAttributesSection, commonParts.subTypeTableSection, commonParts.referencesTableSection);
        return new GenericDiffDisplayType(typeEntry.getKeyProperty(UANode::getNodeId), commonParts.headingText, displayDifferenceType, commonParts.anchorValue, commonParts.documentationTextValue, commonParts.descriptionTextValue, commonParts.graphFigure, definitionTable, commonParts.instanceSections);
    }

    public DiffDisplayVariableType generateVariableType(DiffContext.EntryDiffType entryDiffType, DiffContext.DiffView<? extends UAVariableType> typeEntry, Map<NodeId<?>, DiffContext.DiffCollectionEntry<UAInstance>> instances) {
        var commonParts = generateCommonParts(entryDiffType, typeEntry, instances);
        DiffContext.DiffView<UAVariableType> typeEntry1 = typeEntry.anyCast(UAVariableType.class).get();
        DiffContext.ValueDiffType innerDiffType = commonParts.innerDiffType;
        var variableTypeAttributesTableSection = new DiffVariableTypeAttributesTableSection(typeEntry1);
        innerDiffType = DiffContext.ValueDiffType.getCombinedDiffType(
                innerDiffType,
                variableTypeAttributesTableSection.getDiffType());
        var displayDifferenceType = calculateDisplayDifferenceType(entryDiffType, innerDiffType, commonParts.instanceSections);
        var definitionTable = new VariableTypeDefinitionTable(DiffDisplayUtils.toRenderable(commonParts.headingText), variableTypeAttributesTableSection, commonParts.subTypeTableSection, commonParts.referencesTableSection);
        return new DiffDisplayVariableType(typeEntry1.getKeyProperty(UANode::getNodeId), commonParts.headingText, displayDifferenceType, commonParts.anchorValue, commonParts.documentationTextValue, commonParts.descriptionTextValue, commonParts.graphFigure, definitionTable, commonParts.instanceSections);
    }

    public DiffDisplayDataType generateDataType(DiffContext.EntryDiffType entryDiffType, DiffContext.DiffView<? extends UADataType> typeEntry, Map<NodeId<?>, DiffContext.DiffCollectionEntry<UAInstance>> instances) {
        var commonParts = generateCommonParts(entryDiffType, typeEntry, instances);
        DiffContext.DiffView<UADataType> typeEntry1 = typeEntry.anyCast(UADataType.class).get();
        DiffContext.ValueDiffType innerDiffType = commonParts.innerDiffType;
        DiffDataTypeTableSection dataTypeTableSection;
        if (UAModelUtils.isEnumDataType(typeEntry1.getBaseOrElseCompareValue())) {
            dataTypeTableSection = diffDisplayDataTypeTableSectionGenerator.generateSectionForEnum(typeEntry1, commonParts.showAllTableRowsAsUnchanged);
        } else if (UAModelUtils.isStructureDataType(typeEntry1.getBaseOrElseCompareValue())) {
            dataTypeTableSection = diffDisplayDataTypeTableSectionGenerator.generateSectionForStruct(typeEntry1, commonParts.showAllTableRowsAsUnchanged);
        } else {
            dataTypeTableSection = diffDisplayDataTypeTableSectionGenerator.generateDefaultSection(typeEntry1, commonParts.showAllTableRowsAsUnchanged);
        }
        innerDiffType = DiffContext.ValueDiffType.getCombinedDiffType(
                innerDiffType,
                dataTypeTableSection.getDiffType()
        );
        var displayDifferenceType = calculateDisplayDifferenceType(entryDiffType, innerDiffType, commonParts.instanceSections);
        var definitionTable = new DataTypeDefinitionTable(DiffDisplayUtils.toRenderable(commonParts.headingText), commonParts.typeTableAttributesSection, commonParts.subTypeTableSection, commonParts.referencesTableSection, dataTypeTableSection);
        return new DiffDisplayDataType(typeEntry1.getKeyProperty(UANode::getNodeId), commonParts.headingText, displayDifferenceType, commonParts.anchorValue, commonParts.documentationTextValue, commonParts.descriptionTextValue, commonParts.graphFigure, definitionTable, commonParts.instanceSections);
    }

    private static boolean anyChangeInInstanceSections(List<? extends DiffDisplayInstance> instanceSections) {
        return instanceSections.stream().anyMatch(instanceSection -> instanceSection.getDisplayDifferenceType() != DisplayDifferenceType.UNCHANGED);
    }
    private static DisplayDifferenceType calculateDisplayDifferenceType(DiffContext.EntryDiffType entryDiffType, DiffContext.ValueDiffType innerDiffType, List<? extends DiffDisplayInstance> instanceSections) {
        var displayDifferenceType = DisplayDifferenceType.of(entryDiffType);
        if (displayDifferenceType == DisplayDifferenceType.UNCHANGED && (innerDiffType == DiffContext.ValueDiffType.CHANGED || anyChangeInInstanceSections(instanceSections))) {
            displayDifferenceType = DisplayDifferenceType.CHANGED;
        }
        return displayDifferenceType;
    }

    private static DiffTypeAttributesTableSection<?> generateAttributeSection(DiffContext.DiffView<? extends UAType> typeEntry) {
        if (typeEntry.getBaseOrElseCompareValue() instanceof UAVariableType) {
            return new DiffVariableTypeAttributesTableSection(typeEntry.anyCast(UAVariableType.class).get());
        } else {
            return new DiffTypeAttributesTableSection<>(typeEntry);
        }
    }

    private List<? extends DiffDisplayInstance> generateInstanceSectionsForType(Map<NodeId<?>, DiffContext.DiffCollectionEntry<UAInstance>> instances, DiffContext.DiffView<? extends UAType> type) {
        var diffReferencesFromNode = type.getDiffContext()
                .getDiffReferencesFromNode(type.getKeyProperty(UANode::getNodeId));
        var referenceChangesByNode = diffReferencesFromNode.stream()
                .collect(Collectors.groupingBy(
                        ref -> ref.getValue().navigateInKey(NodeSetUniverse.Reference::getTarget).getKeyProperty(UANode::getNodeId),
                        Collectors.mapping(ref -> ref.getEntryDiffType(), Collectors.toSet())
                ));

        var typeNodeId = type.getKeyProperty(UANode::getNodeId);

        return diffReferencesFromNode.stream()
                .map(entry -> entry.getValue().navigateInKey(NodeSetUniverse.Reference::getTarget).getKeyProperty(UANode::getNodeId))
                .map(instances::get)
                .filter(Objects::nonNull)
                .map(e -> {
                    var renderMode = RenderMode.SHOW_CHANGES;
                    if (e.getEntryDiffType() == DiffContext.EntryDiffType.UNCHANGED) {
                        var nodeId = e.getValue().getKeyProperty(UANode::getNodeId);
                        if (referenceChangesByNode.get(nodeId).stream().allMatch(entryDiffType -> entryDiffType == DiffContext.EntryDiffType.ADDED)) {
                            renderMode = RenderMode.SHOW_AS_UNCHANGED_WITH_COMPARE_VERSION;
                        } else if (referenceChangesByNode.get(nodeId).stream().allMatch(entryDiffType -> entryDiffType == DiffContext.EntryDiffType.REMOVED)) {
                            renderMode = RenderMode.SHOW_AS_UNCHANGED_WITH_BASE_VERSION;
                        }
                    }
                    return diffDisplayInstanceGenerator.generateInstance(typeNodeId, e.getEntryDiffType(), e.getValue(), renderMode);
                })
                .sorted(Utils.byBrowseName())
                .collect(Collectors.toList());
    }
}
