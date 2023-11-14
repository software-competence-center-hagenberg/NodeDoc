package at.scch.nodedoc.documentation.single.table;

import at.scch.nodedoc.documentation.displaymodel.table.EnumDataTypeTableSection;

import java.util.List;

public class SingleEnumDataTypeTableSection extends SingleDataTypeTableSection implements EnumDataTypeTableSection {

    public SingleEnumDataTypeTableSection(List<SingleTableRow> rows) {
        super(rows);
    }
}
