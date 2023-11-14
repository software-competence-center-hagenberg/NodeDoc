package at.scch.nodedoc.documentation.diff;

import at.scch.nodedoc.diffengine.DiffContext;
import at.scch.nodedoc.documentation.displaymodel.DisplayInstance;
import at.scch.nodedoc.documentation.displaymodel.DisplaySection;
import at.scch.nodedoc.documentation.displaymodel.Renderable;
import at.scch.nodedoc.documentation.template.StringTemplate;
import at.scch.nodedoc.nodeset.NodeId;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Getter
public class DiffDisplayInstance extends DiffDisplayNode implements DisplayInstance {

    private final DiffContext.DiffView<String> typeDefinitionBrowseName;

    public DiffDisplayInstance(NodeId<?> nodeId, DiffContext.DiffView<String> headingTextValue, DisplayDifferenceType displayDifferenceType, String anchorValue, DiffContext.DiffView<String> documentationTextValue, DiffContext.DiffView<String> descriptionTextValue, DiffContext.DiffView<String> typeDefinitionBrowseName, List<? extends DisplaySection> subSections) {
        super(nodeId, headingTextValue, displayDifferenceType, anchorValue, documentationTextValue, descriptionTextValue, subSections);
        this.typeDefinitionBrowseName = typeDefinitionBrowseName;
    }

    @Override
    public Optional<Renderable> getTypeDefinition() {
        if (typeDefinitionBrowseName.getBaseOrElseCompareValue() != null) {
            return Optional.of(content -> InlineDifferencePropertyTemplate.template(typeDefinitionBrowseName, StringTemplate::template));
        } else {
            return Optional.empty();
        }
    }
}
