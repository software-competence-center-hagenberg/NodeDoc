package at.scch.nodedoc.documentation.displaymodel.table;

import com.fizzed.rocker.runtime.DefaultRockerModel;

import java.util.List;
import java.util.function.Function;

public interface DataTypeTableSection extends DisplayTable.Section {

    @Override
    default List<DisplayTable.ColumnDefinition> getColumnDefinitions() {
        return List.of(
                new DisplayTable.ColumnDefinition("DataTypeFieldName", 2, getCellValueTemplateFunction()),
                new DisplayTable.ColumnDefinition("Description", 4, getDescriptionCellTemplateFunction())
        );
    }

    Function<Object, DefaultRockerModel> getCellValueTemplateFunction();
    Function<Object, DefaultRockerModel> getDescriptionCellTemplateFunction();
}
