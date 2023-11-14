package at.scch.nodedoc.documentation.diff.table;

import at.scch.nodedoc.diffengine.DiffContext;
import at.scch.nodedoc.documentation.displaymodel.table.TypeAttributesTableSection;
import at.scch.nodedoc.nodeset.UAType;

public class DiffTypeAttributesTableSection<Node extends UAType> extends DiffNodeAttributesTableSection<Node> implements TypeAttributesTableSection<DiffContext.DiffView<?>> {

    public DiffTypeAttributesTableSection(DiffContext.DiffView<Node> diffView) {
        super(diffView);
    }

    @Override
    public DiffContext.DiffView<?> getIsAbstractValue() {
        return diffView.getProperty(UAType::isAbstract);
    }

    @Override
    public DiffContext.ValueDiffType getDiffType() {
        return DiffContext.ValueDiffType.getCombinedDiffType(super.getDiffType(), getIsAbstractValue().getDiffType());
    }
}
