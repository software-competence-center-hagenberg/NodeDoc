package at.scch.nodedoc.documentation.single.table;

import at.scch.nodedoc.documentation.displaymodel.table.DataTypeTableSection;

import java.util.List;

public class SingleDataTypeDefaultTableSection extends SingleDataTypeTableSection implements DataTypeTableSection {
    public SingleDataTypeDefaultTableSection(List<SingleTableRow> rows) {
        super(rows);
    }
}
