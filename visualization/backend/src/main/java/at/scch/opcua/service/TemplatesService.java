package at.scch.opcua.service;

import at.scch.opcua.config.NodeDocConfiguration;
import at.scch.opcua.data.TemplateInfo;
import at.scch.opcua.exception.NodeDocUserException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

@Component
@Slf4j
public class TemplatesService {

    public static final String DEFAULT_TEMPLATE_NAME = "defaultTemplate.html";

    @Autowired
    private NodeDocConfiguration config;

    public ResponseEntity getTemplates() {
        log.info("Get templates");
        try {
            return ResponseEntity.ok(getTemplatesArray());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Could not read templates");
        }
    }

    public ResponseEntity saveTemplate(MultipartFile file, String description) {
        log.info("Save template from multipart file with description \"{}\"", description);
        Path saveFile = Paths.get(config.getDirectory().getTemplates(), file.getOriginalFilename());
        boolean exists = saveFile.toFile().exists();

        try {
            OutputStream os = Files.newOutputStream(saveFile);
            os.write(file.getBytes());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Could not save template file");
        }
        try {
            ArrayList<TemplateInfo> templates = new ArrayList<>(Arrays.asList(getTemplatesArray()));
            if (!exists) {
                templates.add(new TemplateInfo(file.getOriginalFilename(), description));
            } else {
                TemplateInfo update = templates.stream()
                        .filter(t -> t.getTemplateName().equals(file.getOriginalFilename()))
                        .findFirst().orElse(null);
                if (update != null) {
                    update.setTemplateDescription(description);
                }
            }

            writeTemplatesArray(templates);

            if (exists)
                return ResponseEntity.ok("Template file overwritten");
            return ResponseEntity.ok("Saved template file");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Could not add template file");
        }
    }

    public ResponseEntity deleteTemplateByRelativePath(String templateName) {
        log.info("Delete template at {}", templateName);
        Path path = Paths.get(config.getDirectory().getTemplates(), templateName);
        File file = path.toFile();
        if (file.exists()) {
            try {
                ArrayList<TemplateInfo> templates = new ArrayList<>(Arrays.asList(getTemplatesArray()));
                TemplateInfo toDelete = templates.stream()
                        .filter(template -> template.getTemplateName().equals(templateName))
                        .findFirst().orElse(null);

                if (toDelete != null) {
                    templates.remove(toDelete);
                    writeTemplatesArray(templates);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Could not delete template");
            }
            if (file.delete()) {
                return ResponseEntity.ok("HtmlTemplate (" + templateName + ") deleted");
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Could not delete HtmlTemplate " + templateName);
    }

    private TemplateInfo[] getTemplatesArray() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File templateInfos = Paths.get(config.getDirectory().getTemplates(), "templateInfos.json").toFile();
        return objectMapper.readValue(templateInfos, TemplateInfo[].class);
    }

    /**
     * Writes the template info array to the json file
     *
     * @param templates
     * @throws IOException
     */
    private void writeTemplatesArray(ArrayList<TemplateInfo> templates) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File templateFile = Paths.get(config.getDirectory().getTemplates(), "templateInfos.json").toFile();

        objectMapper.writeValue(templateFile, templates.toArray());
    }

    public InputStream getTemplateStreamFromTemplatePath(String htmlTemplatePath) {
        log.info("Get template from path {}", htmlTemplatePath);
        try {
            return Files.newInputStream(Paths.get(config.getDirectory().getTemplates(), htmlTemplatePath));
        } catch (IOException e) {
            throw new NodeDocUserException("Could not access html template");
        }
    }

    public InputStream getDefaultTemplateStream() {
        log.info("Get default template");
        return getTemplateStreamFromTemplatePath(DEFAULT_TEMPLATE_NAME);
    }
}
