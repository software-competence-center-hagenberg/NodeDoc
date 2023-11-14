package at.scch.nodedoc.documentation.displaymodel.table;

import at.scch.nodedoc.documentation.template.StringTemplate;
import com.fizzed.rocker.runtime.DefaultRockerModel;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public interface NodeAttributesTableSection<AttributeValueType> extends DisplayTable.Section {

    @Override
    default List<DisplayTable.ColumnDefinition> getColumnDefinitions() {
        return List.of(
                new DisplayTable.ColumnDefinition("Attribute", 1, StringTemplate::template),
                new DisplayTable.ColumnDefinition("Value", 5, getCellValueTemplateFunction())
        );
    }

    @Override
    default List<DisplayTable.Row> getRows() {
        return List.of(
                buildRow("BrowseName", this::getBrowseNameValue)
        );
    }

    Function<Object, DefaultRockerModel> getCellValueTemplateFunction();
    DisplayTable.Row buildRow(AttributeValueType attributeValue, List<? extends DisplayTable.Cell<?>> cellValues);

    default DisplayTable.Row buildRow(String attributeName, Supplier<AttributeValueType> attributeValueSupplier) {
        var attributeValue = attributeValueSupplier.get();
        var attributeCells = List.of(
                new DisplayTable.Cell<>() {
                    @Override
                    public String getValue() {
                        return attributeName;
                    }

                    @Override
                    public String getCssClass() {
                        return "";
                    }
                },
                buildCell(attributeValue));
        return buildRow(attributeValue, attributeCells);
    }

    AttributeValueType getBrowseNameValue();

    DisplayTable.Cell<AttributeValueType> buildCell(AttributeValueType value);

}
