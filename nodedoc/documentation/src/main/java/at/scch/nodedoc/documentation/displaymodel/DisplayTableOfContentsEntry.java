package at.scch.nodedoc.documentation.displaymodel;

import at.scch.nodedoc.documentation.template.DisplayTableOfContentsEntryTemplate;
import com.fizzed.rocker.RockerContent;
import com.fizzed.rocker.runtime.DefaultRockerModel;

import java.util.List;

public interface DisplayTableOfContentsEntry extends Renderable {

    DisplayModelHeadingNumber getHeadingNumber();
    DisplaySection.DisplayHeadingText getHeadingText();

    DisplaySection getDisplaySection();

    List<? extends DisplayTableOfContentsEntry> getSubEntries();

    @Override
    default DefaultRockerModel structureTemplate(RockerContent content) {
        return DisplayTableOfContentsEntryTemplate.template(this, content);
    }
}
