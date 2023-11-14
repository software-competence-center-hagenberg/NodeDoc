package at.scch.nodedoc.documentation.displaymodel.table;

import java.util.List;

public interface StructDataTypeTableSection extends DataTypeTableSection {

    @Override
    default List<DisplayTable.ColumnDefinition> getColumnDefinitions() {
        return List.of(
                new DisplayTable.ColumnDefinition("DataTypeFieldName", 1, getCellValueTemplateFunction()),
                new DisplayTable.ColumnDefinition("Type", 2, getCellValueTemplateFunction()),
                new DisplayTable.ColumnDefinition("Description", 3, getDescriptionCellTemplateFunction())
        );
    }
}
