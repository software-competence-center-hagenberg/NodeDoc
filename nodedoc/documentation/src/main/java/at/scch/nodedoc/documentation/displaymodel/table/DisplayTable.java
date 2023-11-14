package at.scch.nodedoc.documentation.displaymodel.table;

import at.scch.nodedoc.documentation.displaymodel.Renderable;
import at.scch.nodedoc.documentation.template.table.DisplayTableSectionTemplate;
import at.scch.nodedoc.documentation.template.table.DisplayTableTemplate;
import com.fizzed.rocker.RockerContent;
import com.fizzed.rocker.runtime.DefaultRockerModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.function.Function;

public abstract class DisplayTable implements Renderable {

    @Getter
    @Setter
    private int tableNumber;

    @Getter
    private final Renderable caption;

    public abstract List<Section> getSections();

    public DisplayTable(Renderable caption) {
        this.caption = caption;
    }

    @Override
    public DefaultRockerModel structureTemplate(RockerContent content) {
        return DisplayTableTemplate.template(this, content);
    }

    public interface Section extends Renderable {

        List<ColumnDefinition> getColumnDefinitions();
        List<? extends Row> getRows();

        DefaultRockerModel getRowTemplate(Object row, RockerContent content);

        @Override
        default DefaultRockerModel structureTemplate(RockerContent content) {
            return DisplayTableSectionTemplate.template(this, content);
        }
    }

    @Getter
    public static class ColumnDefinition {

        private final String name;
        private final int colSpan;

        private final Function<Object, DefaultRockerModel> cellValueTemplateFunction;

        public DefaultRockerModel getCellValueTemplate(Object cellValue) {
            return cellValueTemplateFunction.apply(cellValue);
        }

        public ColumnDefinition(String name, int colSpan, Function<Object, DefaultRockerModel> cellValueTemplateFunction) {
            this.name = name;
            this.colSpan = colSpan;
            this.cellValueTemplateFunction = cellValueTemplateFunction;
        }
    }

    public interface Cell<T> {
        T getValue();
        String getCssClass();
    }

    public interface Row {
        List<? extends Cell<?>> getCells();
    }

}
