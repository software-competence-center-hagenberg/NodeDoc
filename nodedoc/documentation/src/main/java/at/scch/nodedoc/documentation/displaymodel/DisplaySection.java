package at.scch.nodedoc.documentation.displaymodel;

import at.scch.nodedoc.documentation.template.DisplaySectionTemplate;
import com.fizzed.rocker.RockerContent;
import com.fizzed.rocker.runtime.DefaultRockerModel;

import java.util.List;

public interface DisplaySection extends Renderable {

    interface DisplayHeadingText extends Renderable {

        @Override
        default DefaultRockerModel structureTemplate(RockerContent content) {
            return new DefaultRockerModel();
        }
    }

    DisplayModelHeadingNumber getHeadingNumber();

    DisplayHeadingText getHeadingText();

    String getAnchorValue();

    List<? extends DisplaySection> getSubSections();

    @Override
    default DefaultRockerModel structureTemplate(RockerContent content) {
        return DisplaySectionTemplate.template(this, content);
    }
}
