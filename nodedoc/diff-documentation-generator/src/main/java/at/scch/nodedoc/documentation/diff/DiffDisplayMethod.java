package at.scch.nodedoc.documentation.diff;

import at.scch.nodedoc.diffengine.DiffContext;
import at.scch.nodedoc.documentation.diff.table.DiffDisplayTableCellValueTemplate;
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

import java.util.List;

@Getter
public class DiffDisplayMethod extends DiffDisplayInstance implements DisplayMethod {

    private final Renderable methodName;
    private final List<DiffDisplayArgument> arguments;
    private final MethodArgumentsTable methodArgumentsTable;

    public DiffDisplayMethod(NodeId<?> nodeId, DiffContext.DiffView<String> headingTextValue, DisplayDifferenceType displayDifferenceType, String anchorValue, DiffContext.DiffView<String> documentationTextValue, DiffContext.DiffView<String> descriptionTextValue, DiffContext.DiffView<String> methodName, List<DiffDisplayArgument> arguments, DiffContext.DiffView<String> typeDefinitionBrowseName, MethodArgumentsTable methodArgumentsTable, List<? extends DisplaySection> subSections) {
        super(nodeId, headingTextValue, displayDifferenceType, anchorValue, documentationTextValue, descriptionTextValue, typeDefinitionBrowseName, subSections);
        this.methodName = content -> InlineDifferencePropertyTemplate.template(methodName, StringTemplate::template);
        this.arguments = arguments;
        this.methodArgumentsTable = methodArgumentsTable;
    }

    @Getter
    public static class DiffDisplayArgument implements DisplayArgument {
        private final Direction direction;
        private final DisplayDifferenceType diffType;
        private final Renderable argumentType;
        private final Renderable argumentName;
        private final Renderable description;
        private final DiffContext.DiffView<UAVariable.Argument> argument;

        public DiffDisplayArgument(Direction direction, DisplayDifferenceType diffType, DiffContext.DiffView<String> argumentType, DiffContext.DiffView<String> argumentName, DiffContext.DiffView<String> description, @Deprecated DiffContext.DiffView<UAVariable.Argument> argument) {
            this.direction = direction;
            this.diffType = diffType;
            this.argumentType = content -> DiffDisplayTableCellValueTemplate.template(
                    argumentType,
                    StringTemplate::template);
            this.argumentName = content -> DiffDisplayTableCellValueTemplate.template(
                    argumentName,
                    StringTemplate::template);
            this.description = content -> DiffDisplayTableCellValueTemplate.template(
                    description,
                    StringTemplate::template
            );
            this.argument = argument;
        }

        @Override
        public DefaultRockerModel structureTemplate(RockerContent content) {
            return MethodArgumentDifferencePropertyTemplate.template(this);
        }
    }
}
