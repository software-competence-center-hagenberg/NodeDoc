package at.scch.nodedoc.documentation.single.table;

import at.scch.nodedoc.documentation.displaymodel.table.DisplayTable;
import at.scch.nodedoc.documentation.displaymodel.table.NodeAttributesTableSection;
import at.scch.nodedoc.documentation.template.StringTemplate;
import at.scch.nodedoc.nodeset.UANode;
import com.fizzed.rocker.RockerContent;
import com.fizzed.rocker.runtime.DefaultRockerModel;

import java.util.List;
import java.util.function.Function;

public class SingleNodeAttributesTableSection<Node extends UANode> implements NodeAttributesTableSection<String> {

    protected final Node node;

    public SingleNodeAttributesTableSection(Node node) {
        this.node = node;
    }

    @Override
    public DefaultRockerModel getRowTemplate(Object row, RockerContent content) {
        return SingleDisplayTableRowTemplate.template((SingleTableRow) row, content);
    }

    @Override
    public Function<Object, DefaultRockerModel> getCellValueTemplateFunction() {
        return (cellValue) -> StringTemplate.template(cellValue);
    }

    @Override
    public DisplayTable.Row buildRow(String attributeValue, List<? extends DisplayTable.Cell<?>> cells) {
        return new SingleTableRow(cells);
    }

    @Override
    public String getBrowseNameValue() {
        return node.getBrowseName();
    }

    @Override
    public DisplayTable.Cell<String> buildCell(String value) {
        return new SingleTableCell<>(value);
    }
}
