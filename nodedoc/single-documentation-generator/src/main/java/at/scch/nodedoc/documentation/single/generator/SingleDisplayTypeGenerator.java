package at.scch.nodedoc.documentation.single.generator;

import at.scch.nodedoc.documentation.displaymodel.table.DataTypeDefinitionTable;
import at.scch.nodedoc.documentation.displaymodel.table.GenericTypeDefinitionTable;
import at.scch.nodedoc.documentation.displaymodel.table.VariableTypeDefinitionTable;
import at.scch.nodedoc.documentation.single.*;
import at.scch.nodedoc.documentation.single.table.*;
import at.scch.nodedoc.documentation.template.StringTemplate;
import at.scch.nodedoc.html.Utilities;
import at.scch.nodedoc.nodeset.UADataType;
import at.scch.nodedoc.nodeset.UAInstance;
import at.scch.nodedoc.nodeset.UAType;
import at.scch.nodedoc.nodeset.UAVariableType;
import at.scch.nodedoc.util.StreamUtils;
import at.scch.nodedoc.util.UAModelUtils;
import org.jsoup.nodes.Element;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class SingleDisplayTypeGenerator {

    private final SingleDisplayReferencesTableSectionGenerator singleDisplayReferencesTableSectionGenerator;
    private final SingleDisplayDataTypeTableSectionGenerator singleDisplayDataTypeTableSectionGenerator;
    private final SingleDisplayInstanceGenerator singleDisplayInstanceGenerator;
    private final DocEntryEditorGenerator docEntryEditorGenerator;

    public SingleDisplayTypeGenerator(SingleDisplayReferencesTableSectionGenerator singleDisplayReferencesTableSectionGenerator, SingleDisplayDataTypeTableSectionGenerator singleDisplayDataTypeTableSectionGenerator, SingleDisplayInstanceGenerator singleDisplayInstanceGenerator, DocEntryEditorGenerator docEntryEditorGenerator) {
        this.singleDisplayReferencesTableSectionGenerator = singleDisplayReferencesTableSectionGenerator;
        this.singleDisplayDataTypeTableSectionGenerator = singleDisplayDataTypeTableSectionGenerator;
        this.singleDisplayInstanceGenerator = singleDisplayInstanceGenerator;
        this.docEntryEditorGenerator = docEntryEditorGenerator;
    }

    private static class CommonParts<T extends UAType> {
        public final String headingText;
        public final SingleTypeAttributesTableSection<?> typeTableAttributesSection;
        public final SingleSubTypeTableSection<T> subTypeTableSection;
        public final SingleReferenceTableSection referencesTableSection;
        public final List<? extends SingleDisplayInstance> instanceSections;
        public final String anchorValue;
        public final SingleGraphFigure graphFigure;
        public final Element documentationEditor;
        public final Element descriptionEditor;

        public CommonParts(String headingText, SingleTypeAttributesTableSection<?> typeTableAttributesSection, SingleSubTypeTableSection<T> subTypeTableSection, SingleReferenceTableSection referencesTableSection, List<? extends SingleDisplayInstance> instanceSections, String anchorValue, SingleGraphFigure graphFigure, Element documentationEditor, Element descriptionEditor) {
            this.headingText = headingText;
            this.typeTableAttributesSection = typeTableAttributesSection;
            this.subTypeTableSection = subTypeTableSection;
            this.referencesTableSection = referencesTableSection;
            this.instanceSections = instanceSections;
            this.anchorValue = anchorValue;
            this.graphFigure = graphFigure;
            this.documentationEditor = documentationEditor;
            this.descriptionEditor = descriptionEditor;
        }
    }

    private <T extends UAType> CommonParts<T> generateCommonParts(T type) {
        var headingText = type.getBrowseName();
        var typeTableAttributesSection = generateAttributeSection(type);
        var subTypeTableSection = new SingleSubTypeTableSection<>(type);
        var referencesTableSection = singleDisplayReferencesTableSectionGenerator.generateReferenceSection(type);

        var instanceSections = generateInstanceSectionsForType(type);

        var anchorValue = Utilities.getNodeIdAnchor(type.getNodeId());

        var graphFigure = new SingleGraphFigure(headingText);

        var documentationEditor = docEntryEditorGenerator.getDivForNodeDocumentation(type, 3);
        var descriptionEditor = docEntryEditorGenerator.getDivForNodeDescription(type, 3);

        return new CommonParts<>(headingText, typeTableAttributesSection, subTypeTableSection, referencesTableSection, instanceSections, anchorValue, graphFigure, documentationEditor, descriptionEditor);
    }

    public GenericSingleDisplayType generateGenericType(UAType type) {
        var commonParts = generateCommonParts(type);
        var definitionTable = new GenericTypeDefinitionTable(x -> StringTemplate.template(commonParts.headingText), commonParts.typeTableAttributesSection, commonParts.subTypeTableSection, commonParts.referencesTableSection);
        return new GenericSingleDisplayType(type.getNodeId(), commonParts.headingText, commonParts.anchorValue, commonParts.documentationEditor, commonParts.descriptionEditor, commonParts.graphFigure, definitionTable, commonParts.instanceSections);
    }

    public SingleDisplayVariableType generateVariableType(UAVariableType type) {
        var commonParts = generateCommonParts(type);
        var variableTypeAttributesTableSection = new SingleVariableTypeAttributesTableSection(type);
        var definitionTable = new VariableTypeDefinitionTable(x -> StringTemplate.template(commonParts.headingText), variableTypeAttributesTableSection, commonParts.subTypeTableSection, commonParts.referencesTableSection);
        return new SingleDisplayVariableType(type.getNodeId(), commonParts.headingText, commonParts.anchorValue, commonParts.documentationEditor, commonParts.descriptionEditor, commonParts.graphFigure, definitionTable, commonParts.instanceSections);
    }

    public SingleDisplayDataType generateDataType(UADataType type) {
        var commonParts = generateCommonParts(type);
        SingleDataTypeTableSection dataTypeTableSection;
        if (UAModelUtils.isEnumDataType(type)) {
            dataTypeTableSection = singleDisplayDataTypeTableSectionGenerator.generateSectionForEnum(type);
        } else if (UAModelUtils.isStructureDataType(type)) {
            dataTypeTableSection = singleDisplayDataTypeTableSectionGenerator.generateSectionForStruct(type);
        } else {
            dataTypeTableSection = singleDisplayDataTypeTableSectionGenerator.generateDefaultSection(type);
        }
        var definitionTable = new DataTypeDefinitionTable(x -> StringTemplate.template(commonParts.headingText), commonParts.typeTableAttributesSection, commonParts.subTypeTableSection, commonParts.referencesTableSection, dataTypeTableSection);
        return new SingleDisplayDataType(type.getNodeId(), commonParts.headingText, commonParts.anchorValue, commonParts.documentationEditor, commonParts.descriptionEditor, commonParts.graphFigure, definitionTable, commonParts.instanceSections);
    }

    private static SingleTypeAttributesTableSection<?> generateAttributeSection(UAType type) {
        if (type instanceof UAVariableType) {
            return new SingleVariableTypeAttributesTableSection((UAVariableType) type);
        } else {
            return new SingleTypeAttributesTableSection<>(type);
        }
    }

    private List<? extends SingleDisplayInstance> generateInstanceSectionsForType(UAType type) {
        return new HashSet<>(type.getForwardReferences().values()).stream()
                .flatMap(StreamUtils.filterCast(UAInstance.class))
                .map(instance -> singleDisplayInstanceGenerator.generateInstance(type.getNodeId(), instance))
                .sorted(Utils.byBrowseName())
                .collect(Collectors.toList());
    }
}
