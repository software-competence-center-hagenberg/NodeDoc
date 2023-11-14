package at.scch.nodedoc.documentation.diff.table;

import at.scch.nodedoc.documentation.displaymodel.table.DataTypeTableSection;

import java.util.List;

public class DiffDataTypeDefaultTableSection extends DiffDataTypeTableSection implements DataTypeTableSection {
    public DiffDataTypeDefaultTableSection(List<DiffTableRow> rows) {
        super(rows);
    }
}
