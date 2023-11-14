package at.scch.nodedoc.documentation.diff;

import at.scch.nodedoc.diffengine.DiffContext;
import at.scch.nodedoc.documentation.displaymodel.GraphFigure;
import at.scch.nodedoc.documentation.displaymodel.Renderable;
import at.scch.nodedoc.documentation.template.StringTemplate;
import lombok.Getter;

@Getter
public class DiffGraphFigure extends GraphFigure {

    private final Renderable caption;

    public DiffGraphFigure(DiffContext.DiffView<String> captionText) {
        this.caption = content -> InlineDifferencePropertyTemplate.template(captionText, StringTemplate::template);
    }
}
