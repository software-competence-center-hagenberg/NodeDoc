package at.scch.nodedoc.documentation.displaymodel;

import at.scch.nodedoc.documentation.template.DisplayVariableTemplate;
import com.fizzed.rocker.RockerContent;
import com.fizzed.rocker.runtime.DefaultRockerModel;

import java.util.Optional;

public interface DisplayVariable extends DisplayInstance {

    @Override
    default DefaultRockerModel structureTemplate(RockerContent content) {
        return DisplayVariableTemplate.template(this, content);
    }

    Optional<Renderable> getDataType();
}
