package at.scch.nodedoc.documentation.single;

import at.scch.nodedoc.documentation.displaymodel.DisplayModelHeadingNumber;
import at.scch.nodedoc.documentation.displaymodel.DisplayNode;
import at.scch.nodedoc.documentation.displaymodel.DisplaySection;
import at.scch.nodedoc.documentation.displaymodel.Renderable;
import at.scch.nodedoc.documentation.template.StringTemplate;
import at.scch.nodedoc.nodeset.NodeId;
import com.fizzed.rocker.runtime.DefaultRockerModel;
import lombok.Getter;
import org.jsoup.nodes.Element;

import java.util.List;

@Getter
public abstract class SingleDisplayNode implements DisplayNode {

    public class DiffDisplayHeadingText implements DisplayHeadingText {

        @Override
        public DefaultRockerModel displayTemplate(DefaultRockerModel content) {
            return StringTemplate.template(headingTextValue);
        }
    }

    private final NodeId<?> nodeId;
    private final String headingTextValue;
    private final String anchorValue;
    private final Element documentationEditor;
    private final Element descriptionEditor;

    public SingleDisplayNode(NodeId<?> nodeId, String headingTextValue, String anchorValue, Element documentationEditor, Element descriptionEditor, List<? extends DisplaySection> subSections) {
        this.nodeId = nodeId;
        this.headingTextValue = headingTextValue;
        this.anchorValue = anchorValue;
        this.documentationEditor = documentationEditor;
        this.descriptionEditor = descriptionEditor;
        this.subSections = subSections;
    }

    public DisplayHeadingText getHeadingText() {
        return new DiffDisplayHeadingText();
    }

    private final DisplayModelHeadingNumber headingNumber = new DisplayModelHeadingNumber();

    private final List<? extends DisplaySection> subSections;

    @Override
    public DefaultRockerModel displayTemplate(DefaultRockerModel content) {
        return SingleDisplayNodeTemplate.template(this, content);
    }

    @Override
    public Renderable getDocumentation() {
        return x -> RawElementTemplate.template(documentationEditor);
    }

    @Override
    public Renderable getDescription() {
        return x -> RawElementTemplate.template(descriptionEditor);
    }
}
