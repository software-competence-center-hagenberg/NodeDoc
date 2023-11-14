package at.scch.nodedoc.documentation.displaymodel.table;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface VariableTypeAttributesTableSection<AttributeValueType> extends TypeAttributesTableSection<AttributeValueType> {

    @Override
    default List<DisplayTable.Row> getRows() {
        return Stream.concat(
                TypeAttributesTableSection.super.getRows().stream(),
                Stream.of(
                        buildRow("DataType", this::getDataTypeBrowseNameValue),
                        buildRow("ValueRank", this::getValueRankAsStringValue),
                        buildRow("ArrayDimensions", this::getArrayDimensionsValue)
                )
        ).collect(Collectors.toList());
    }

    AttributeValueType getDataTypeBrowseNameValue();

    AttributeValueType getValueRankAsStringValue();

    AttributeValueType getArrayDimensionsValue();

}
