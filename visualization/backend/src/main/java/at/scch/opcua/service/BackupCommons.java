package at.scch.opcua.service;

import at.scch.opcua.config.NodeDocConfiguration;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class BackupCommons {

    @Autowired
    private NodeDocConfiguration config;

    public static final String NODESETS_FOLDER = "nodesets";
    public static final String DIFFS_FOLDER = "diffs";
    public static final String TEMPLATES_FOLDER = "templates";
    public static final String DOC_ENTRIES_FOLDER = "docentries";
    public static final String META_FILE_NAME = "meta.json";

    public static class Meta {

        @Getter(onMethod_ = {@JsonProperty("nodeDocVersion")})
        private final String nodeDocVersion;

        @JsonCreator
        public Meta(@JsonProperty("nodeDocVersion") String nodeDocVersion) {
            this.nodeDocVersion = nodeDocVersion;
        }
    }

    public File getBackupsDirectory() {
        var directory = new File(config.getDirectory().getBackups());
        directory.mkdirs();
        return directory;
    }
}
