package at.scch.nodedoc.documentation.diff.generator;

import at.scch.nodedoc.diffengine.DiffContext;
import at.scch.nodedoc.documentation.diff.InlineDifferencePropertyTemplate;
import at.scch.nodedoc.documentation.displaymodel.Renderable;
import at.scch.nodedoc.documentation.template.StringTemplate;

public class DiffDisplayUtils {

    public static Renderable toRenderable(DiffContext.DiffView<String> diffView) {
        return content -> InlineDifferencePropertyTemplate.template(diffView, StringTemplate::template);
    }
}
