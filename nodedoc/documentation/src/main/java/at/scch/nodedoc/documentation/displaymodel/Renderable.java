package at.scch.nodedoc.documentation.displaymodel;

import com.fizzed.rocker.RockerContent;
import com.fizzed.rocker.runtime.DefaultRockerModel;

public interface Renderable {

    DefaultRockerModel structureTemplate(RockerContent content);
    default DefaultRockerModel displayTemplate(DefaultRockerModel content) {
        return content;
    }

    default DefaultRockerModel template() {
        return displayTemplate(structureTemplate(RockerContent.NONE));
    }
}
