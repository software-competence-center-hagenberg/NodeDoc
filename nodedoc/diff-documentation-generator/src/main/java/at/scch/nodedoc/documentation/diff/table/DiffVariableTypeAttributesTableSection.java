package at.scch.nodedoc.documentation.diff.table;

import at.scch.nodedoc.diffengine.DiffContext;
import at.scch.nodedoc.documentation.displaymodel.table.VariableTypeAttributesTableSection;
import at.scch.nodedoc.nodeset.UADataType;
import at.scch.nodedoc.nodeset.UAVariableType;
import at.scch.nodedoc.util.UAModelUtils;

public class DiffVariableTypeAttributesTableSection extends DiffTypeAttributesTableSection<UAVariableType> implements VariableTypeAttributesTableSection<DiffContext.DiffView<?>> {
    
    public DiffVariableTypeAttributesTableSection(DiffContext.DiffView<UAVariableType> diffView) {
        super(diffView);
    }

    @Override
    public DiffContext.DiffView<?> getDataTypeBrowseNameValue() {
        return diffView.navigate(UAVariableType::getDataType).getMergedProperty(UADataType::getBrowseName);
    }

    @Override
    public DiffContext.DiffView<?> getValueRankAsStringValue() {
        return diffView.getProperty(UAModelUtils::valueRankAsString);
    }

    @Override
    public DiffContext.DiffView<?> getArrayDimensionsValue() {
        return diffView.getProperty(UAVariableType::getArrayDimensions);
    }

    @Override
    public DiffContext.ValueDiffType getDiffType() {
        return DiffContext.ValueDiffType.getCombinedDiffType(
                super.getDiffType(),
                getDataTypeBrowseNameValue().getDiffType(),
                getValueRankAsStringValue().getDiffType(),
                getArrayDimensionsValue().getDiffType()
        );
    }
}
