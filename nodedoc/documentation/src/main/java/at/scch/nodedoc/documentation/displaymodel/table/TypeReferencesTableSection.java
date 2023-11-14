package at.scch.nodedoc.documentation.displaymodel.table;

import com.fizzed.rocker.runtime.DefaultRockerModel;

import java.util.List;
import java.util.function.Function;

public interface TypeReferencesTableSection extends DisplayTable.Section {

    @Override
    default List<DisplayTable.ColumnDefinition> getColumnDefinitions() {
        return List.of(
                new DisplayTable.ColumnDefinition("Reference", 1, getCellValueTemplateFunction()),
                new DisplayTable.ColumnDefinition("NodeClass", 1, getCellValueTemplateFunction()),
                new DisplayTable.ColumnDefinition("BrowseName", 1, getCellValueTemplateFunction()),
                new DisplayTable.ColumnDefinition("DataType", 1, getCellValueTemplateFunction()),
                new DisplayTable.ColumnDefinition("TypeDefinition", 1, getCellValueTemplateFunction()),
                new DisplayTable.ColumnDefinition("ModellingRule", 1, getCellValueTemplateFunction())
        );
    }

    List<? extends DisplayTable.Row> getRows();

    Function<Object, DefaultRockerModel> getCellValueTemplateFunction();
}
