package at.scch.nodedoc.documentation.displaymodel.table;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface TypeAttributesTableSection<AttributeValueType> extends NodeAttributesTableSection<AttributeValueType> {

    @Override
    default List<DisplayTable.Row> getRows() {
        return Stream.concat(
                NodeAttributesTableSection.super.getRows().stream(),
                Stream.of(buildRow("IsAbstract", this::getIsAbstractValue))
        ).collect(Collectors.toList());
    }

    AttributeValueType getIsAbstractValue();

}
