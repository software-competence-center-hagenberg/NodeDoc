package at.scch.nodedoc.documentation.displaymodel;

import at.scch.nodedoc.documentation.template.DisplayNodeTemplate;
import com.fizzed.rocker.RockerContent;
import com.fizzed.rocker.runtime.DefaultRockerModel;

public interface DisplayNode extends DisplaySection {

    default DefaultRockerModel structureTemplate(RockerContent content) {
        return DisplayNodeTemplate.template(this, content);
    }

    Renderable getDocumentation();
    Renderable getDescription();
}
