package at.scch.nodedoc.documentation.displaymodel;

import at.scch.nodedoc.documentation.template.DisplayInstanceTemplate;
import com.fizzed.rocker.RockerContent;
import com.fizzed.rocker.runtime.DefaultRockerModel;

import java.util.Optional;

public interface DisplayInstance extends DisplayNode {

    @Override
    default DefaultRockerModel structureTemplate(RockerContent content) {
        return DisplayInstanceTemplate.template(this, content);
    }

    Optional<Renderable> getTypeDefinition();
}
