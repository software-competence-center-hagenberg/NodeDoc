package at.scch.nodedoc.documentation.displaymodel;

import at.scch.nodedoc.documentation.displaymodel.table.TypeDefinitionTable;
import at.scch.nodedoc.documentation.template.DisplayTypeTemplate;
import com.fizzed.rocker.RockerContent;
import com.fizzed.rocker.runtime.DefaultRockerModel;

public interface DisplayType<TypeDefTable extends TypeDefinitionTable<?>> extends DisplayNode {

    TypeDefTable getDefinitionTable();
    GraphFigure getGraphFigure();

    @Override
    default DefaultRockerModel structureTemplate(RockerContent content) {
        return DisplayTypeTemplate.template(this, content);
    }
}
