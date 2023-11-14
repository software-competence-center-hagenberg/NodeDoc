package at.scch.nodedoc.documentation.displaymodel.table;

import java.util.List;

public interface EnumDataTypeTableSection extends DataTypeTableSection {

    @Override
    default List<DisplayTable.ColumnDefinition> getColumnDefinitions() {
        return List.of(
                new DisplayTable.ColumnDefinition("EnumValue", 1, getCellValueTemplateFunction()),
                new DisplayTable.ColumnDefinition("ValueAsText", 2, getCellValueTemplateFunction()),
                new DisplayTable.ColumnDefinition("Description", 3, getDescriptionCellTemplateFunction())
        );
    }
}
