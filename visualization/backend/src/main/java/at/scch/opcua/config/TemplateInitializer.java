package at.scch.opcua.config;

import at.scch.opcua.service.TemplatesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Component
@Profile("!test") // TODO
@Slf4j
public class TemplateInitializer implements CommandLineRunner {

    private final NodeDocConfiguration config;

    @Autowired
    public TemplateInitializer(NodeDocConfiguration config) {
        this.config = config;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Initialize templates");
        var defaultTemplateFile = new File(config.getDirectory().getTemplates(), TemplatesService.DEFAULT_TEMPLATE_NAME);
        try (var emptyTemplate = TemplateInitializer.class.getResourceAsStream("/emptyTemplate.html")) {
            Files.copy(emptyTemplate, defaultTemplateFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
