package at.scch.nodedoc.parser.rawModel;

import org.w3c.dom.Element;

public class RawMethod extends RawInstance {

    public RawMethod(Element element) {
        super(element);
    }

    // TODO: ArgumentDescription?

    public boolean isExecutable() {
        return getAttributeOrDefault("Executable", Boolean::parseBoolean, true);
    }

    public boolean isUserExecutable() {
        return getAttributeOrDefault("UserExecutable", Boolean::parseBoolean, true);
    }

    public String getMethodDeclarationId() {
        return getAttributeOrDefault("MethodDeclarationId", null);
    }
}
