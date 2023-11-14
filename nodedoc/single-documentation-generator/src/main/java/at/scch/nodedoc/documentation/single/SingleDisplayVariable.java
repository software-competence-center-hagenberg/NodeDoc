package at.scch.nodedoc.documentation.single;

import at.scch.nodedoc.documentation.displaymodel.DisplaySection;
import at.scch.nodedoc.documentation.displaymodel.DisplayVariable;
import at.scch.nodedoc.documentation.displaymodel.Renderable;
import at.scch.nodedoc.documentation.template.StringTemplate;
import at.scch.nodedoc.nodeset.NodeId;
import lombok.Getter;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.Optional;

@Getter
public class SingleDisplayVariable extends SingleDisplayInstance implements DisplayVariable {

    private final String dataTypeBrowseName;

    public SingleDisplayVariable(NodeId<?> nodeId, String headingTextValue, String anchorValue, Element documentationEditor, Element descriptionEditor, String typeDefinitionBrowseName, String dataTypeBrowseName, List<? extends DisplaySection> subSections) {
        super(nodeId, headingTextValue, anchorValue, documentationEditor, descriptionEditor, typeDefinitionBrowseName, subSections);
        this.dataTypeBrowseName = dataTypeBrowseName;
    }

    @Override
    public Optional<Renderable> getDataType() {
        if (dataTypeBrowseName != null) {
            return Optional.of(content -> StringTemplate.template(dataTypeBrowseName));
        } else {
            return Optional.empty();
        }
    }
}
