package at.scch.nodedoc.documentation.displaymodel.table;

import com.fizzed.rocker.runtime.DefaultRockerModel;

import java.util.List;
import java.util.function.Function;

public interface TypeSubTypeTableSection extends DisplayTable.Section {

    @Override
    default List<DisplayTable.ColumnDefinition> getColumnDefinitions() {
        return List.of(
                new DisplayTable.ColumnDefinition("Subtype of", 1, getCellValueTemplateFunction()),
                new DisplayTable.ColumnDefinition("isForward", 1, getCellValueTemplateFunction()),
                new DisplayTable.ColumnDefinition("Namespace", 4, getCellValueTemplateFunction())
        );
    }

    @Override
    default List<? extends DisplayTable.Row> getRows() {
        return getSubtypeReferenceRows();
    }

    Function<Object, DefaultRockerModel> getCellValueTemplateFunction();

    List<? extends DisplayTable.Row> getSubtypeReferenceRows();


}
