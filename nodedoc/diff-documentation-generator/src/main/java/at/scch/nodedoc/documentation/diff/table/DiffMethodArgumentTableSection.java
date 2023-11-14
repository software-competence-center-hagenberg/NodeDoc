package at.scch.nodedoc.documentation.diff.table;

import at.scch.nodedoc.diffengine.DiffContext;
import at.scch.nodedoc.documentation.diff.DisplayDifferenceType;
import at.scch.nodedoc.documentation.displaymodel.table.DisplayTable;
import at.scch.nodedoc.documentation.displaymodel.table.MethodArgumentTableSection;
import at.scch.nodedoc.documentation.template.StringTemplate;
import at.scch.nodedoc.util.StreamUtils;
import com.fizzed.rocker.RockerContent;
import com.fizzed.rocker.runtime.DefaultRockerModel;

import java.util.List;
import java.util.function.Function;

public class DiffMethodArgumentTableSection implements MethodArgumentTableSection {

    private final List<DiffTableRow> rows;

    public DiffMethodArgumentTableSection(List<DiffTableRow> rows) {
        this.rows = rows;
    }

    public DiffContext.ValueDiffType getDiffType() {
        return getRows().stream()
                .flatMap(row -> row.getCells().stream())
                .flatMap(StreamUtils.filterCast(DiffTableCell.class))
                .map(DiffTableCell::getDisplayDifferenceType)
                .allMatch(displayDifferenceType -> displayDifferenceType == DisplayDifferenceType.UNCHANGED)
                ? DiffContext.ValueDiffType.UNCHANGED
                : DiffContext.ValueDiffType.CHANGED;
    }

    @Override
    public List<? extends DisplayTable.Row> getRows() {
        return rows;
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
    public Function<Object, DefaultRockerModel> getDescriptionCellTemplateFunction() {
        return getCellValueTemplateFunction();
    }
}
