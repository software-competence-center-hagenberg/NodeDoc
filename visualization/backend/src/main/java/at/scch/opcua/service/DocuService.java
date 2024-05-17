package at.scch.opcua.service;

import at.scch.opcua.config.NodeDocConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Slf4j
public class DocuService {

    @Autowired
    private NodeDocConfiguration config;


    /**
     * Deletes a documentation of a nodeset, by first deleting the scripts and then the html file. Then all parent
     * directories are deleted if they are empty.
     *
     * @param relativePath - path to the documentation
     * @return - response containing information about whether the documentation could be deleted or not.
     */
    public ResponseEntity deleteDocumentationFileByRelativePath(String relativePath) {
        log.info("Delete documentation at {}", relativePath);
        Path path = Paths.get(config.getDirectory().getNodesets(), relativePath);
        File file = path.toFile();
        String message = "documentation deleted";
        if (file.exists()) {
            Path js = Paths.get(file.getParentFile().getAbsolutePath(), "js");
            try {
                FileUtils.deleteDirectory(js.toFile());
            } catch (IOException e) {
                message += " / could not delete JavaScript files";
            }
            // file.delete() only deletes a file/directory if it has no children
            do {
                file.delete();
                file = file.getParentFile();
            } while (!file.getPath().equals(config.getDirectory().getNodesets()));
        }
        if (!path.toFile().exists()) {
            return ResponseEntity.ok(message);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Could not delete documentation");
        }
    }
}
