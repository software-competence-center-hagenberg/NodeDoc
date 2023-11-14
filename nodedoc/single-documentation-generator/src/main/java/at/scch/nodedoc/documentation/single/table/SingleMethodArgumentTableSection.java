package at.scch.nodedoc.documentation.single.table;

import at.scch.nodedoc.documentation.displaymodel.table.DisplayTable;
import at.scch.nodedoc.documentation.displaymodel.table.MethodArgumentTableSection;
import at.scch.nodedoc.documentation.single.RawElementTemplate;
import at.scch.nodedoc.documentation.template.StringTemplate;
import com.fizzed.rocker.RockerContent;
import com.fizzed.rocker.runtime.DefaultRockerModel;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.function.Function;

public class SingleMethodArgumentTableSection implements MethodArgumentTableSection {

    private final List<SingleTableRow> rows;

    public SingleMethodArgumentTableSection(List<SingleTableRow> rows) {
        this.rows = rows;
    }

    @Override
    public List<? extends DisplayTable.Row> getRows() {
        return rows;
    }

    @Override
    public DefaultRockerModel getRowTemplate(Object row, RockerContent content) {
        return SingleDisplayTableRowTemplate.template((SingleTableRow) row, content);
    }

    @Override
    public Function<Object, DefaultRockerModel> getCellValueTemplateFunction() {
        return (cellValue) -> StringTemplate.template((String) cellValue);
    }

    @Override
    public Function<Object, DefaultRockerModel> getDescriptionCellTemplateFunction() {
        return (cellValue) -> RawElementTemplate.template((Element) cellValue);
    }
}
