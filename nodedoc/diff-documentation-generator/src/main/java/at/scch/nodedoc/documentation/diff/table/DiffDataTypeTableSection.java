package at.scch.nodedoc.documentation.diff.table;

import at.scch.nodedoc.diffengine.DiffContext;
import at.scch.nodedoc.documentation.diff.DisplayDifferenceType;
import at.scch.nodedoc.documentation.displaymodel.table.DataTypeTableSection;
import at.scch.nodedoc.documentation.template.StringTemplate;
import at.scch.nodedoc.util.StreamUtils;
import com.fizzed.rocker.RockerContent;
import com.fizzed.rocker.runtime.DefaultRockerModel;
import lombok.Getter;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class DiffDataTypeTableSection implements DataTypeTableSection {

    @Getter
    private final List<DiffTableRow> rows;

    protected DiffDataTypeTableSection(List<DiffTableRow> rows) {
        this.rows = rows;
    }

    public DiffContext.ValueDiffType getDiffType() {
        return Stream.concat(
                getRows().stream().map(DiffTableRow::getDisplayDifferenceType),
                getRows().stream()
                        .map(DiffTableRow::getCells)
                        .flatMap(Collection::stream)
                        .flatMap(StreamUtils.filterCast(DiffTableCell.class))
                        .map(DiffTableCell::getDisplayDifferenceType)
        ).allMatch(displayDifferenceType -> displayDifferenceType == DisplayDifferenceType.UNCHANGED)
                ? DiffContext.ValueDiffType.UNCHANGED
                : DiffContext.ValueDiffType.CHANGED;
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
