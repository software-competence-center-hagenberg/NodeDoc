
package at.scch.nodedoc.documentation.displaymodel.table;

import at.scch.nodedoc.documentation.displaymodel.Renderable;
import at.scch.nodedoc.documentation.template.ConcatTemplate;
import at.scch.nodedoc.documentation.template.StringTemplate;
import lombok.Getter;

import java.util.List;

@Getter
public class MethodArgumentsTable extends DisplayTable {

    private final MethodArgumentTableSection methodArgumentTableSection;

    public MethodArgumentsTable(Renderable methodName, MethodArgumentTableSection methodArgumentTableSection) {
        super(content -> ConcatTemplate.template(methodName, x -> StringTemplate.template(" Method Arguments")));
        this.methodArgumentTableSection = methodArgumentTableSection;
    }

    @Override
    public List<Section> getSections() {
        return List.of(
                methodArgumentTableSection
        );
    }
}
