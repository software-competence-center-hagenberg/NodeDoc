package at.scch.opcua.exception;

public class NodeDocUserException extends RuntimeException {

    public NodeDocUserException(String message) {
        super(message);
    }

    public NodeDocUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
