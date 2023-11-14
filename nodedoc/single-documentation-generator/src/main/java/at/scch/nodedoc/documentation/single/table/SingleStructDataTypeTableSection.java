package at.scch.nodedoc.documentation.single.table;

import at.scch.nodedoc.documentation.displaymodel.table.StructDataTypeTableSection;

import java.util.List;

public class SingleStructDataTypeTableSection extends SingleDataTypeTableSection implements StructDataTypeTableSection {
    public SingleStructDataTypeTableSection(List<SingleTableRow> rows) {
        super(rows);
    }
}
