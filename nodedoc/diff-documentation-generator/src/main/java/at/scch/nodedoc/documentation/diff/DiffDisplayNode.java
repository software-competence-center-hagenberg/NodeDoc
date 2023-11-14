package at.scch.nodedoc.documentation.diff;

import at.scch.nodedoc.diffengine.DiffContext;
import at.scch.nodedoc.documentation.displaymodel.DisplayModelHeadingNumber;
import at.scch.nodedoc.documentation.displaymodel.DisplayNode;
import at.scch.nodedoc.documentation.displaymodel.DisplaySection;
import at.scch.nodedoc.documentation.displaymodel.Renderable;
import at.scch.nodedoc.nodeset.NodeId;
import com.fizzed.rocker.runtime.DefaultRockerModel;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class DiffDisplayNode implements DisplayNode {

    public class DiffDisplayHeadingText implements DisplayHeadingText {

        public DiffContext.DiffView<String> getText() {
            return headingTextValue;
        }

        @Override
        public DefaultRockerModel displayTemplate(DefaultRockerModel content) {
            return DiffDisplayHeadingTextTemplate.template(this, content);
        }
    }

    private final NodeId<?> nodeId;
    private final DiffContext.DiffView<String> headingTextValue;
    private final DisplayDifferenceType displayDifferenceType;
    private final String anchorValue;
    private final DiffContext.DiffView<String> documentationTextValue;
    private final DiffContext.DiffView<String> descriptionTextValue;

    public DiffDisplayNode(NodeId<?> nodeId, DiffContext.DiffView<String> headingTextValue, DisplayDifferenceType displayDifferenceType, String anchorValue, DiffContext.DiffView<String> documentationTextValue, DiffContext.DiffView<String> descriptionTextValue, List<? extends DisplaySection> subSections) {
        this.nodeId = nodeId;
        this.headingTextValue = headingTextValue;
        this.displayDifferenceType = displayDifferenceType;
        this.anchorValue = anchorValue;
        this.documentationTextValue = documentationTextValue;
        this.descriptionTextValue = descriptionTextValue;
        this.subSections = subSections;
    }

    public DisplayHeadingText getHeadingText() {
        return new DiffDisplayHeadingText();
    }

    private final DisplayModelHeadingNumber headingNumber = new DisplayModelHeadingNumber();

    private final List<? extends DisplaySection> subSections;

    @Override
    public DefaultRockerModel displayTemplate(DefaultRockerModel content) {
        return DiffDisplayNodeTemplate.template(this, content);
    }

    @Override
    public Renderable getDocumentation() {
        return new DiffDocEntry(documentationTextValue, DisplayDifferenceType.of(documentationTextValue.getDiffType()), "Documentation:");
    }

    @Override
    public Renderable getDescription() {
        return new DiffDocEntry(descriptionTextValue, DisplayDifferenceType.of(descriptionTextValue.getDiffType()), "Description:");
    }
}
