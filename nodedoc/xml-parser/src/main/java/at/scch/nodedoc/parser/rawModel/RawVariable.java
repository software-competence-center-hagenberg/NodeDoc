package at.scch.nodedoc.parser.rawModel;

import at.scch.nodedoc.parser.QueryUtils;
import at.scch.nodedoc.parser.XMLUtils;
import lombok.Getter;
import org.w3c.dom.Element;

import java.util.List;
import java.util.stream.Collectors;

public class RawVariable extends RawInstance {

    public RawVariable(Element element) {
        super(element);

        this.arguments = query("Value", "ListOfExtensionObject", "ExtensionObject", "Body", "Argument")
                .map(Argument::new)
                .collect(Collectors.toList());
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

    public int getAccessLevel() {
        return getAttributeOrDefault("AccessLevel", Integer::parseInt, 1);
    }

    public int getUserAccessLevel() {
        return getAttributeOrDefault("UserAccessLevel", Integer::parseInt, 1);
    }

    public double getMinimumSamplingInterval() {
        return getAttributeOrDefault("MinimumSamplingInterval", Double::parseDouble, 0.0);
    }

    public boolean isHistorizing() {
        return getAttributeOrDefault("Historizing", Boolean::parseBoolean, false);
    }

    @Getter
    private final List<Argument> arguments;

    public static class Argument extends DOMProxy {

        public Argument(Element element) {
            super(element);
        }

        public String getName() {
            return query("Name")
                    .map(Element::getTextContent)
                    .findAny().orElse(null);
        }

        public String getDataType() {
            return query("DataType", "Identifier")
                    .map(Element::getTextContent)
                    .findAny().orElse(null);
        }

        public int getValueRank() {
            return query("ValueRank")
                    .map(Element::getTextContent)
                    .map(Integer::parseInt)
                    .findAny().orElse(-1);
        }

        public String getArrayDimension() {
            return query("ArrayDimensions")
                    .map(Element::getTextContent)
                    .findAny().orElse(null);
        }

        public String getDescription() {
            return query("Description")
                    .map(Element::getTextContent)
                    .findAny().orElse(null);
        }

        public void setDescription(String description) {
            var descriptionElement = query("Description")
                    .findFirst()
                    .orElseGet(() -> createElementBeforeWithSameNamespace("Description", List.of()));

            var textElement = QueryUtils.queryPath(descriptionElement, "Text")
                    .findFirst()
                    .orElseGet(() -> XMLUtils.createElementBeforeWithSameNamespace(descriptionElement, "Text", List.of()));

            textElement.setTextContent(description);
        }

    }
}
