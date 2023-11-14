package at.scch.nodedoc.documentation.displaymodel;

import at.scch.nodedoc.documentation.template.StringTemplate;
import com.fizzed.rocker.runtime.DefaultRockerModel;
import lombok.Getter;

import java.util.List;

@Getter
public class DisplayMainChapter implements DisplaySection {

    public class HeadingText implements DisplayHeadingText {

        @Override
        public DefaultRockerModel displayTemplate(DefaultRockerModel content) {
            return StringTemplate.template(title);
        }
    }
    private final DisplayModelHeadingNumber headingNumber = new DisplayModelHeadingNumber();
    private final String title;
    private final List<? extends DisplaySection> subSections;

    public DisplayMainChapter(String title, List<? extends DisplaySection> subSections) {
        this.title = title;
        this.subSections = subSections;
    }

    @Override
    public DisplayHeadingText getHeadingText() {
        return new HeadingText();
    }

    @Override
    public String getAnchorValue() {
        return "ch:" + title.replaceAll(" ", "_"); // TODO
    }
}
