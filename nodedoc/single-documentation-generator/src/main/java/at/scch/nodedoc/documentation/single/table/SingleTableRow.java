package at.scch.nodedoc.documentation.single.table;

import at.scch.nodedoc.documentation.displaymodel.table.DisplayTable;
import lombok.Getter;

import java.util.List;

@Getter
public class SingleTableRow implements DisplayTable.Row {

    private final List<? extends DisplayTable.Cell<?>> cells;

    public SingleTableRow(List<? extends DisplayTable.Cell<?>> cells) {
        this.cells = cells;
    }
}
