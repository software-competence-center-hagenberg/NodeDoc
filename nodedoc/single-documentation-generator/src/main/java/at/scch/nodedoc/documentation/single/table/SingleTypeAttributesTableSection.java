package at.scch.nodedoc.documentation.single.table;

import at.scch.nodedoc.documentation.displaymodel.table.TypeAttributesTableSection;
import at.scch.nodedoc.nodeset.UAType;

public class SingleTypeAttributesTableSection<Node extends UAType> extends SingleNodeAttributesTableSection<Node> implements TypeAttributesTableSection<String> {

    public SingleTypeAttributesTableSection(Node node) {
        super(node);
    }

    @Override
    public String getIsAbstractValue() {
        return Boolean.toString(node.isAbstract());
    }
}
