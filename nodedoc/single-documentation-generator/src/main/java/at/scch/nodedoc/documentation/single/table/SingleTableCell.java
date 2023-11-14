package at.scch.nodedoc.documentation.single.table;

import at.scch.nodedoc.documentation.displaymodel.table.DisplayTable;
import lombok.Getter;

@Getter
public class SingleTableCell<T> implements DisplayTable.Cell<T> {
    private final T value;

    public SingleTableCell(T value) {
        this.value = value;
    }

    @Override
    public String getCssClass() {
        return "";
    }
}
