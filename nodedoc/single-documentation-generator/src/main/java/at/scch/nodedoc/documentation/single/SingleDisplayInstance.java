package at.scch.nodedoc.documentation.single;

import at.scch.nodedoc.documentation.displaymodel.DisplayInstance;
import at.scch.nodedoc.documentation.displaymodel.DisplaySection;
import at.scch.nodedoc.documentation.displaymodel.Renderable;
import at.scch.nodedoc.documentation.template.StringTemplate;
import at.scch.nodedoc.nodeset.NodeId;
import lombok.Getter;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.Optional;

@Getter
public class SingleDisplayInstance extends SingleDisplayNode implements DisplayInstance {

    private final String typeDefinitionBrowseName;

    public SingleDisplayInstance(NodeId<?> nodeId, String headingTextValue, String anchorValue, Element documentationEditor, Element descriptionEditor, String typeDefinitionBrowseName, List<? extends DisplaySection> subSections) {
        super(nodeId, headingTextValue, anchorValue, documentationEditor, descriptionEditor, subSections);
        this.typeDefinitionBrowseName = typeDefinitionBrowseName;
    }

    @Override
    public Optional<Renderable> getTypeDefinition() {
        if (typeDefinitionBrowseName != null) {
            return Optional.of(content -> StringTemplate.template(typeDefinitionBrowseName));
        } else {
            return Optional.empty();
        }
    }
}
