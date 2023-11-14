package at.scch.nodedoc.documentation.diff.generator;

import at.scch.nodedoc.diffengine.DiffContext;
import at.scch.nodedoc.documentation.diff.DisplayDifferenceType;
import at.scch.nodedoc.documentation.diff.table.*;
import at.scch.nodedoc.nodeset.DefinitionField;
import at.scch.nodedoc.nodeset.UADataType;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DiffDisplayDataTypeTableSectionGenerator {

    public DiffDataTypeTableSection generateSectionForEnum(DiffContext.DiffView<UADataType> typeEntry, boolean showAllRowsAsUnchanged) {
        var enumRows = buildRows(typeEntry, showAllRowsAsUnchanged, List.of(
                definitionFieldDiffView -> definitionFieldDiffView.getProperty(DefinitionField::getValue).getProperty(Object::toString),
                definitionFieldDiffView -> definitionFieldDiffView.getProperty(DefinitionField::getName),
                definitionFieldDiffView -> definitionFieldDiffView.getProperty(DefinitionField::getDescription).replaceNull("")
        ), DefinitionField::getValue);
        return new DiffEnumDataTypeTableSection(enumRows);
    }

    public DiffDataTypeTableSection generateSectionForStruct(DiffContext.DiffView<UADataType> typeEntry, boolean showAllRowsAsUnchanged) {
        var structRows = buildRows(typeEntry, showAllRowsAsUnchanged, List.of(
                definitionFieldDiffView -> definitionFieldDiffView.getProperty(DefinitionField::getName),
                definitionFieldDiffView -> definitionFieldDiffView.navigate(DefinitionField::getDataType).getMergedProperty(UADataType::getBrowseName),
                definitionFieldDiffView -> definitionFieldDiffView.getProperty(DefinitionField::getDescription).replaceNull("")
        ), DefinitionField::getName);
        return new DiffStructDataTypeTableSection(structRows);
    }

    public DiffDataTypeTableSection generateDefaultSection(DiffContext.DiffView<UADataType> typeEntry, boolean showAllRowsAsUnchanged) {
        var rows = buildRows(typeEntry, showAllRowsAsUnchanged, List.of(
                definitionFieldDiffView -> definitionFieldDiffView.getProperty(DefinitionField::getName),
                definitionFieldDiffView -> definitionFieldDiffView.getProperty(DefinitionField::getDescription).replaceNull("")
        ), DefinitionField::getName);
        return new DiffDataTypeDefaultTableSection(rows);
    }

    private <T extends Comparable<T>> List<DiffTableRow> buildRows(DiffContext.DiffView<UADataType> typeEntry, boolean showAllRowsAsUnchanged, List<Function<DiffContext.DiffView<DefinitionField>, DiffContext.DiffView<String>>> cellDiffViewExtractors, Function<DefinitionField, T> sortingKeyExtractor) {
        return typeEntry.getDiffSetWithValues(UADataType::getDefinition, DefinitionField::getName, String.class).stream()
                .sorted(Comparator.comparing(
                        entry -> entry.getValue().getProperty(sortingKeyExtractor).getBaseOrElseCompareValue())
                )
                .map(entry -> {
                    var definitionFieldDiffView = entry.getValue();
                    var rowDisplayDifferenceType = showAllRowsAsUnchanged ? DisplayDifferenceType.UNCHANGED : DisplayDifferenceType.of(entry.getEntryDiffType());
                    var cells = cellDiffViewExtractors.stream()
                            .map(f -> DiffTableUtils.diffViewToTableCell(f.apply(definitionFieldDiffView)))
                            .collect(Collectors.toList());
                    return new DiffTableRow(rowDisplayDifferenceType, cells);
                }).collect(Collectors.toList());
    }
}
