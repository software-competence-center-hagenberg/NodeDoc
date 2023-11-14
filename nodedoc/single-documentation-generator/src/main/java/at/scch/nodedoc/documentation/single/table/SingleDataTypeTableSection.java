package at.scch.nodedoc.documentation.single.table;

import at.scch.nodedoc.documentation.displaymodel.table.DataTypeTableSection;
import at.scch.nodedoc.documentation.single.RawElementTemplate;
import at.scch.nodedoc.documentation.template.StringTemplate;
import com.fizzed.rocker.RockerContent;
import com.fizzed.rocker.runtime.DefaultRockerModel;
import lombok.Getter;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.function.Function;

public abstract class SingleDataTypeTableSection implements DataTypeTableSection {

    @Getter
    private final List<SingleTableRow> rows;

    protected SingleDataTypeTableSection(List<SingleTableRow> rows) {
        this.rows = rows;
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
