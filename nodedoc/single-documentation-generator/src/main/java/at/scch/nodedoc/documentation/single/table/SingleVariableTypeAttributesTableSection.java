package at.scch.nodedoc.documentation.single.table;

import at.scch.nodedoc.documentation.displaymodel.table.VariableTypeAttributesTableSection;
import at.scch.nodedoc.nodeset.UAVariableType;
import at.scch.nodedoc.util.UAModelUtils;

public class SingleVariableTypeAttributesTableSection extends SingleTypeAttributesTableSection<UAVariableType> implements VariableTypeAttributesTableSection<String> {
    
    public SingleVariableTypeAttributesTableSection(UAVariableType node) {
        super(node);
    }

    @Override
    public String getDataTypeBrowseNameValue() {
        return node.getDataType().getBrowseName();
    }

    @Override
    public String getValueRankAsStringValue() {
        return UAModelUtils.valueRankAsString(node);
    }

    @Override
    public String getArrayDimensionsValue() {
        return node.getArrayDimensions();
    }
}
