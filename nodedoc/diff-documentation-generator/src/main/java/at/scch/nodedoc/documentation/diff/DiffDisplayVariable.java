package at.scch.nodedoc.documentation.diff;

import at.scch.nodedoc.diffengine.DiffContext;
import at.scch.nodedoc.documentation.displaymodel.DisplaySection;
import at.scch.nodedoc.documentation.displaymodel.DisplayVariable;
import at.scch.nodedoc.documentation.displaymodel.Renderable;
import at.scch.nodedoc.documentation.template.StringTemplate;
import at.scch.nodedoc.nodeset.NodeId;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Getter
public class DiffDisplayVariable extends DiffDisplayInstance implements DisplayVariable {

    private final DiffContext.DiffView<String> dataTypeBrowseName;

    public DiffDisplayVariable(NodeId<?> nodeId, DiffContext.DiffView<String> headingTextValue, DisplayDifferenceType displayDifferenceType, String anchorValue, DiffContext.DiffView<String> documentationTextValue, DiffContext.DiffView<String> descriptionTextValue, DiffContext.DiffView<String> typeDefinitionBrowseName, DiffContext.DiffView<String> dataTypeBrowseName, List<? extends DisplaySection> subSections) {
        super(nodeId, headingTextValue, displayDifferenceType, anchorValue, documentationTextValue, descriptionTextValue, typeDefinitionBrowseName, subSections);
        this.dataTypeBrowseName = dataTypeBrowseName;
    }

    @Override
    public Optional<Renderable> getDataType() {
        if (dataTypeBrowseName.getBaseOrElseCompareValue() != null) {
            return Optional.of(content -> InlineDifferencePropertyTemplate.template(dataTypeBrowseName, StringTemplate::template));
        } else {
            return Optional.empty();
        }
    }
}
