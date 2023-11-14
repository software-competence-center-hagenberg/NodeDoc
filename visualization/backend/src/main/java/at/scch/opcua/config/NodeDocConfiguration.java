package at.scch.opcua.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("nodedoc")
public class NodeDocConfiguration {

    @Getter
    @Setter
    public static class DirectoryConfiguration {
        private String templates;
        private String nodesets;
        private String diffs;
    }

    @Getter
    @Setter
    public static class CouchDbConfiguration {
        private String uri;
        private String username;
        private String password;
        private String databaseName;
        private boolean createDb;

        public boolean shouldCreateDb() {
            return createDb;
        }
    }
    @Getter
    @Setter
    public static class DocumentationGenerationConfig {
        private String couchDbUri;
    }

    private CouchDbConfiguration couchDb;
    private DirectoryConfiguration directory;
    private DocumentationGenerationConfig documentationGeneration;
    private String motd;
    private boolean demo;

}
