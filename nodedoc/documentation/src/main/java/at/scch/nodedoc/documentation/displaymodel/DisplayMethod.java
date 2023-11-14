package at.scch.nodedoc.documentation.displaymodel;

import at.scch.nodedoc.documentation.displaymodel.table.MethodArgumentsTable;
import at.scch.nodedoc.documentation.template.DisplayMethodTemplate;
import com.fizzed.rocker.RockerContent;
import com.fizzed.rocker.runtime.DefaultRockerModel;

import java.util.List;

public interface DisplayMethod extends DisplayInstance {

    interface DisplayArgument extends Renderable {
        enum Direction {
            IN, OUT;

            public String htmlText() {
                switch (this) {
                    case IN:
                        return "[in]";
                    case OUT:
                        return "[out]";
                    default:
                        throw new IllegalStateException("Unexpected value: " + this);
                }
            }
        }

        @Override
        DefaultRockerModel structureTemplate(RockerContent content);

        Direction getDirection();

        Renderable getArgumentType();

        Renderable getArgumentName();

        Renderable getDescription();
    }

    Renderable getMethodName();

    List<? extends DisplayArgument> getArguments();

    MethodArgumentsTable getMethodArgumentsTable();

    @Override
    default DefaultRockerModel structureTemplate(RockerContent content) {
        return DisplayMethodTemplate.template(this, content);
    }

}
