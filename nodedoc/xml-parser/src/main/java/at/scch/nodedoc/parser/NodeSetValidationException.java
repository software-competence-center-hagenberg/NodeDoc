package at.scch.nodedoc.parser;

public class NodeSetValidationException extends RuntimeException {

    public NodeSetValidationException(String message) {
        super(message);
    }

    public NodeSetValidationException(Throwable cause) {
        super(cause);
    }
}
