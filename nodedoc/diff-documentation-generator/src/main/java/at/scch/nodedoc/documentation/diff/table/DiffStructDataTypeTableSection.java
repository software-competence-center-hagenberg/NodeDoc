package at.scch.nodedoc.documentation.diff.table;

import at.scch.nodedoc.documentation.displaymodel.table.StructDataTypeTableSection;

import java.util.List;

public class DiffStructDataTypeTableSection extends DiffDataTypeTableSection implements StructDataTypeTableSection {
    public DiffStructDataTypeTableSection(List<DiffTableRow> rows) {
        super(rows);
    }
}
