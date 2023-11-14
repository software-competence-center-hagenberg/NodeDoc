package at.scch.nodedoc.documentation.diff.table;

import at.scch.nodedoc.diffengine.DiffContext;
import at.scch.nodedoc.documentation.diff.DisplayDifferenceType;
import at.scch.nodedoc.documentation.displaymodel.table.DisplayTable;
import at.scch.nodedoc.documentation.displaymodel.table.NodeAttributesTableSection;
import at.scch.nodedoc.documentation.template.StringTemplate;
import at.scch.nodedoc.nodeset.UANode;
import com.fizzed.rocker.RockerContent;
import com.fizzed.rocker.runtime.DefaultRockerModel;

import java.util.List;
import java.util.function.Function;

public class DiffNodeAttributesTableSection<Node extends UANode> implements NodeAttributesTableSection<DiffContext.DiffView<?>> {

    protected final DiffContext.DiffView<Node> diffView;

    public DiffNodeAttributesTableSection(DiffContext.DiffView<Node> diffView) {
        this.diffView = diffView;
    }

    @Override
    public DefaultRockerModel getRowTemplate(Object row, RockerContent content) {
        return DiffDisplayTableRowTemplate.template((DiffTableRow) row, content);
    }

    @Override
    public Function<Object, DefaultRockerModel> getCellValueTemplateFunction() {
        return (cellValue) -> DiffDisplayTableCellValueTemplate.template((DiffContext.DiffView<?>) cellValue, StringTemplate::template);
    }

    @Override
    public DisplayTable.Row buildRow(DiffContext.DiffView<?> attributeValue, List<? extends DisplayTable.Cell<?>> cells) {
        return new DiffTableRow(DisplayDifferenceType.UNCHANGED, cells);
    }

    @Override
    public DiffContext.DiffView<String> getBrowseNameValue() {
        return diffView.getProperty(UANode::getBrowseName);
    }

    public DiffContext.ValueDiffType getDiffType() {
        return getBrowseNameValue().getDiffType();
    }

    @Override
    public DisplayTable.Cell<DiffContext.DiffView<?>> buildCell(DiffContext.DiffView<?> value) {
        return new DiffTableCell<>(value, DisplayDifferenceType.of(value.getDiffType()));
    }
}
