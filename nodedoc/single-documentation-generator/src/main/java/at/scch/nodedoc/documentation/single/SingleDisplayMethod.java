package at.scch.nodedoc.documentation.single;

import at.scch.nodedoc.documentation.displaymodel.DisplayMethod;
import at.scch.nodedoc.documentation.displaymodel.DisplaySection;
import at.scch.nodedoc.documentation.displaymodel.Renderable;
import at.scch.nodedoc.documentation.displaymodel.table.MethodArgumentsTable;
import at.scch.nodedoc.documentation.template.StringTemplate;
import at.scch.nodedoc.nodeset.NodeId;
import at.scch.nodedoc.nodeset.UAVariable;
import com.fizzed.rocker.RockerContent;
import com.fizzed.rocker.runtime.DefaultRockerModel;
import lombok.Getter;
import org.jsoup.nodes.Element;

import java.util.List;

@Getter
public class SingleDisplayMethod extends SingleDisplayInstance implements DisplayMethod {

    private final Renderable methodName;
    private final List<SingleDisplayArgument> arguments;
    private final MethodArgumentsTable methodArgumentsTable;

    public SingleDisplayMethod(NodeId<?> nodeId, String headingTextValue, String anchorValue, Element documentationEditor, Element descriptionEditor, String methodName, List<SingleDisplayArgument> arguments, String typeDefinitionBrowseName, MethodArgumentsTable methodArgumentsTable, List<? extends DisplaySection> subSections) {
        super(nodeId, headingTextValue, anchorValue, documentationEditor, descriptionEditor, typeDefinitionBrowseName, subSections);
        this.methodName = content -> StringTemplate.template(methodName);
        this.arguments = arguments;
        this.methodArgumentsTable = methodArgumentsTable;
    }

    @Getter
    public static class SingleDisplayArgument implements DisplayArgument {
        private final Direction direction;
        private final Renderable argumentType;
        private final Renderable argumentName;
        private final Renderable description;
        private final UAVariable.Argument argument;

        public SingleDisplayArgument(Direction direction, String argumentType, String argumentName, String description, UAVariable.Argument argument) {
            this.direction = direction;
            this.argumentType = content -> StringTemplate.template(argumentType);
            this.argumentName = content -> StringTemplate.template(argumentName);
            this.description = content -> StringTemplate.template(description);
            this.argument = argument;
        }

        @Override
        public DefaultRockerModel structureTemplate(RockerContent content) {
            return MethodArgumentTemplate.template(this);
        }
    }
}
