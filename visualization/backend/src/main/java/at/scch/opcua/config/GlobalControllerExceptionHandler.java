package at.scch.opcua.config;

import at.scch.nodedoc.NodeSetNotFoundException;
import at.scch.nodedoc.NodeSetSaveException;
import at.scch.nodedoc.html.InvalidAccessLevelException;
import at.scch.nodedoc.modelresolver.NodeNotFoundException;
import at.scch.nodedoc.modelresolver.NodeSetResolveException;
import at.scch.opcua.exception.NodeDocUserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler({NodeDocUserException.class, NodeSetResolveException.class, NodeSetNotFoundException.class, NodeSetSaveException.class, NodeNotFoundException.class, InvalidAccessLevelException.class})
    public ResponseEntity<String> handleNodeDocUserException(NodeDocUserException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAnyException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
    }
}
