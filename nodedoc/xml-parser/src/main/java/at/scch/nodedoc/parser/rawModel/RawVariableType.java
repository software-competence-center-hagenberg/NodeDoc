package at.scch.nodedoc.parser.rawModel;

import org.w3c.dom.Element;

public class RawVariableType extends RawType {

    public RawVariableType(Element element) {
        super(element);
    }

    // TODO: Value?

    public String getDataType() {
        return getAttributeOrDefault("DataType", "i=24");
    }

    public int getValueRank() {
        return getAttributeOrDefault("ValueRank", Integer::parseInt, -1);
    }

    public String getArrayDimensions() {
        return getAttributeOrDefault("ArrayDimensions", "");
    }

}
