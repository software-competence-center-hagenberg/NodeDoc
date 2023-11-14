package at.scch.nodedoc.parser.rawModel;

import org.w3c.dom.Element;

import java.util.List;

public class RawDefinitionField extends DOMProxy {

    public RawDefinitionField(Element element) {
        super(element);
    }

    public String getDisplayName() {
        return query("DisplayName")
                .map(Element::getTextContent)
                .findAny().orElse(null);
    }

    public String getDescription() {
        return query("Description")
                .map(Element::getTextContent)
                .findAny().orElse(null);
    }

    public void setDescription(String description) {
        var element = query("Description")
                .findFirst()
                .orElseGet(() -> createElementBeforeWithSameNamespace("Description", List.of()));
        element.setTextContent(description);
    }

    public String getName() {
        return getAttribute("Name");
    }

    public String getSymbolicName() {
        return getAttributeOrDefault("SymbolicName", null);
    }

    public String getDataType() {
        return getAttributeOrDefault("DataType", "i=24");
    }

    public int getValueRank() {
        return getAttributeOrDefault("ValueRank", Integer::parseInt, -1);
    }

    // TODO: ArrayDimensions?

    // TODO: MaxStringLength?

    public int getValue() {
        return getAttributeOrDefault("Value", Integer::parseInt, -1);
    }

    public boolean isOptional() {
        return getAttributeOrDefault("IsOptional", Boolean::parseBoolean, false);
    }

    // TODO: AllowSubtypes?
}
