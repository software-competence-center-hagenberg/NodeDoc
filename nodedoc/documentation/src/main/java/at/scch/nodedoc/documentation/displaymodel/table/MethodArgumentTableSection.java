package at.scch.nodedoc.documentation.displaymodel.table;

import com.fizzed.rocker.runtime.DefaultRockerModel;

import java.util.List;
import java.util.function.Function;

public interface MethodArgumentTableSection extends DisplayTable.Section {

    @Override
    default List<DisplayTable.ColumnDefinition> getColumnDefinitions() {
        return List.of(
                new DisplayTable.ColumnDefinition("Name", 1, getCellValueTemplateFunction()),
                new DisplayTable.ColumnDefinition("DataType", 1, getCellValueTemplateFunction()),
                new DisplayTable.ColumnDefinition("ValueRank", 1, getCellValueTemplateFunction()),
                new DisplayTable.ColumnDefinition("ArrayDimensions", 1, getCellValueTemplateFunction()),
                new DisplayTable.ColumnDefinition("Description", 4, getDescriptionCellTemplateFunction())
        );
    }

    Function<Object, DefaultRockerModel> getCellValueTemplateFunction();
    Function<Object, DefaultRockerModel> getDescriptionCellTemplateFunction();
}
