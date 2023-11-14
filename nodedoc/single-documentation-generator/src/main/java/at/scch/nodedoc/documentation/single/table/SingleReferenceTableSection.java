package at.scch.nodedoc.documentation.single.table;

import at.scch.nodedoc.documentation.displaymodel.table.TypeReferencesTableSection;
import at.scch.nodedoc.documentation.template.StringTemplate;
import com.fizzed.rocker.RockerContent;
import com.fizzed.rocker.runtime.DefaultRockerModel;
import lombok.Getter;

import java.util.List;
import java.util.function.Function;

public class SingleReferenceTableSection implements TypeReferencesTableSection {

    @Getter
    private final List<SingleTableRow> rows;

    public SingleReferenceTableSection(List<SingleTableRow> rows) {
        this.rows = rows;
    }

    @Override
    public DefaultRockerModel getRowTemplate(Object row, RockerContent content) {
        return SingleDisplayTableRowTemplate.template((SingleTableRow) row, content);
    }

    @Override
    public Function<Object, DefaultRockerModel> getCellValueTemplateFunction() {
        return (cellValue) -> SingleDisplayTableCellValueTemplate.template((String) cellValue, StringTemplate::template);
    }
}

