package at.scch.nodedoc.parser.rawModel;

import org.w3c.dom.Element;

import java.util.List;
import java.util.stream.Collectors;

public class RawDataType extends RawType {

    public RawDataType(Element element) {
        super(element);
    }

    // TODO: Definition as own object?

    public List<RawDefinitionField> getDefinition() {
        return query("Definition", "Field")
                .map(RawDefinitionField::new)
                .collect(Collectors.toList());
    }

    // TODO: Purpose?

}
