package at.scch.nodedoc.documentation.diff.table;

import at.scch.nodedoc.documentation.diff.DisplayDifferenceType;
import at.scch.nodedoc.documentation.displaymodel.table.DisplayTable;
import lombok.Getter;

import java.util.List;

@Getter
public class DiffTableRow implements DisplayTable.Row {

    private final DisplayDifferenceType displayDifferenceType;
    private final List<? extends DisplayTable.Cell<?>> cells;

    public DiffTableRow(DisplayDifferenceType displayDifferenceType, List<? extends DisplayTable.Cell<?>> cells) {
        this.displayDifferenceType = displayDifferenceType;
        this.cells = cells;
    }
}
