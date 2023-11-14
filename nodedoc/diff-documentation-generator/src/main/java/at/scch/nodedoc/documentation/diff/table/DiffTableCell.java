package at.scch.nodedoc.documentation.diff.table;

import at.scch.nodedoc.documentation.diff.DisplayDifferenceType;
import at.scch.nodedoc.documentation.displaymodel.table.DisplayTable;
import lombok.Getter;

@Getter
public class DiffTableCell<T> implements DisplayTable.Cell<T> {
    private final T value;
    private final DisplayDifferenceType displayDifferenceType;

    public DiffTableCell(T value, DisplayDifferenceType displayDifferenceType) {
        this.value = value;
        this.displayDifferenceType = displayDifferenceType;
    }

    @Override
    public String getCssClass() {
        return "diff-cell " + displayDifferenceType.cssClass();
    }
}
