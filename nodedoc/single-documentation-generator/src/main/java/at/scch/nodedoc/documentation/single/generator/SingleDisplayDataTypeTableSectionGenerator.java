package at.scch.nodedoc.documentation.single.generator;

import at.scch.nodedoc.db.documents.TextId;
import at.scch.nodedoc.documentation.single.table.*;
import at.scch.nodedoc.nodeset.DefinitionField;
import at.scch.nodedoc.nodeset.UADataType;
import org.jsoup.nodes.Element;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SingleDisplayDataTypeTableSectionGenerator {

    private final DocEntryEditorGenerator docEntryEditorGenerator;

    public SingleDisplayDataTypeTableSectionGenerator(DocEntryEditorGenerator docEntryEditorGenerator) {
        this.docEntryEditorGenerator = docEntryEditorGenerator;
    }

    private Element generateEditorForField(UADataType dataType, DefinitionField definitionField) {
        return docEntryEditorGenerator.getDivForIdentifier(
                TextId.forField(dataType.getNodeId(), definitionField),
                4);
    }

    public SingleDataTypeTableSection generateSectionForEnum(UADataType type) {
        var enumRows = buildRows(type, List.of(
                definitionField -> Integer.toString(definitionField.getValue()),
                DefinitionField::getName,
                field -> generateEditorForField(type, field)
        ), DefinitionField::getValue);
        return new SingleEnumDataTypeTableSection(enumRows);
    }

    public SingleDataTypeTableSection generateSectionForStruct(UADataType type) {
        var structRows = buildRows(type, List.of(
                DefinitionField::getName,
                definitionField -> definitionField.getDataType().getBrowseName(),
                field -> generateEditorForField(type, field)
        ), DefinitionField::getName);
        return new SingleStructDataTypeTableSection(structRows);
    }

    public SingleDataTypeTableSection generateDefaultSection(UADataType type) {
        var rows = buildRows(type, List.of(
                DefinitionField::getName,
                field -> generateEditorForField(type, field)
        ), DefinitionField::getName);
        return new SingleDataTypeDefaultTableSection(rows);
    }

    private <T extends Comparable<T>> List<SingleTableRow> buildRows(UADataType type, List<Function<DefinitionField, Object>> cellValueExtractors, Function<DefinitionField, T> sortingKeyExtractor) {
        return type.getDefinition().stream()
                .sorted(Comparator.comparing(field -> sortingKeyExtractor.apply(field)))
                .map(field -> {
                    var cells = cellValueExtractors.stream()
                            .map(f -> new SingleTableCell<>(f.apply(field)))
                            .collect(Collectors.toList());
                    return new SingleTableRow(cells);
                }).collect(Collectors.toList());
    }
}
