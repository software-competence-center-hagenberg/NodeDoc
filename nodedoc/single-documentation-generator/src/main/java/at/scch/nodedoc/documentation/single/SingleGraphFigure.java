package at.scch.nodedoc.documentation.single;

import at.scch.nodedoc.documentation.displaymodel.GraphFigure;
import at.scch.nodedoc.documentation.displaymodel.Renderable;
import at.scch.nodedoc.documentation.template.StringTemplate;
import lombok.Getter;

@Getter
public class SingleGraphFigure extends GraphFigure {

    private final Renderable caption;

    public SingleGraphFigure(String captionText) {
        this.caption = content -> StringTemplate.template(captionText);
    }
}
