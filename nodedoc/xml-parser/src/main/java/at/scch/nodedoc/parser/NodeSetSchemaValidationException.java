package at.scch.nodedoc.parser;

import org.xml.sax.SAXParseException;

public class NodeSetSchemaValidationException extends NodeSetValidationException {

    public NodeSetSchemaValidationException(SAXParseException cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return getCause().getMessage();
    }

    public int getLineNumber() {
        return ((SAXParseException) getCause()).getLineNumber();
    }

    public int getColumnNumber() {
        return ((SAXParseException) getCause()).getColumnNumber();
    }
}
