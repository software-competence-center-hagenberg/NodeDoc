package at.scch.nodedoc.documentation.diff.table;

import at.scch.nodedoc.documentation.displaymodel.table.EnumDataTypeTableSection;

import java.util.List;

public class DiffEnumDataTypeTableSection extends DiffDataTypeTableSection implements EnumDataTypeTableSection {

    public DiffEnumDataTypeTableSection(List<DiffTableRow> rows) {
        super(rows);
    }
}
