package at.scch.opcua.controller;

import at.scch.opcua.demomode.RestrictInDemoMode;
import at.scch.opcua.dto.FilestructureDirectoryNode;
import at.scch.opcua.service.NodesetService;
import at.scch.opcua.service.TemplatesService;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@CrossOrigin
@AllArgsConstructor
public class NodesetController {

    private final NodesetService nodesetService;
    private final TemplatesService templatesService;

    //region fileManipulation
    @RestrictInDemoMode
    @ApiOperation(value = "Saves a nodeset file to be referenced by other nodesets.")
    @PostMapping("/nodeset/")
    public ResponseEntity saveNodesetWithoutGeneratingDocumentation(@RequestParam("nodeset") MultipartFile nodeset) {
        nodesetService.saveNodeSetFromMultipartFile(nodeset);
        return ResponseEntity.ok("Saved nodeset");
    }

    @RestrictInDemoMode
    @ApiOperation(value = "Deletes a specified nodeset by its relative path")
    @DeleteMapping("/nodeset/")
    public ResponseEntity deleteNodesetByRelativePath(@RequestParam String path) {
        boolean deleted = nodesetService.deleteFileOrDirectoryByRelativePath(path);
        if (deleted)
            return ResponseEntity.ok("File deleted");
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Could not delete file");
    }

    @ApiOperation(value = "Returns a json object representing the file structure")
    @RequestMapping(value = "/nodesets/", method = RequestMethod.GET)
    public ResponseEntity<FilestructureDirectoryNode> getNodesetFiles() {
        return ResponseEntity.ok(nodesetService.getNodeSetFiles());
    }

    //endregion

    //region generateForNewNodeset
    @RestrictInDemoMode
    @ApiOperation(value = "Saves a nodeset file from an URL.")
    @PostMapping("/nodeset/url/")
    public ResponseEntity saveNodeset(
            @RequestParam("url") String nodesetUrl,
            @RequestParam(value = "authorization", required = false) String authorization
    ) {
        nodesetService.saveNodeSetFromUrl(nodesetUrl, authorization);
        return ResponseEntity.ok("");
    }

    @RestrictInDemoMode
    @ApiOperation(value = "Saves a nodeset file from an URL and generates a documentation with the default html template")
    @PostMapping("/nodeset/generate-from-url/default-html-template/")
    public ResponseEntity generateFromUrlDefaultHtml(
            @RequestParam("url") String nodesetUrl,
            @RequestParam(value = "authorization", required = false) String authorization
    ) {
        return responseEntityFromReplacedModels(nodesetService.generateDocumentationFromNodeSetUrl(nodesetUrl, authorization, TemplatesService.DEFAULT_TEMPLATE_NAME));
    }

    @RestrictInDemoMode
    @ApiOperation(value = "Saves a nodeset file from an URL and generates a documentation with a new html template")
    @PostMapping("/nodeset/generate-from-url/")
    public ResponseEntity generateFromUrlDefaultHtml(
            @RequestParam("url") String nodesetUrl,
            @RequestParam(value = "authorization", required = false) String authorization,
            @RequestParam("htmlTemplate") MultipartFile htmlTemplate,
            @RequestParam(required = false) String templateDescription
    ) {
        ResponseEntity saving = templatesService.saveTemplate(htmlTemplate, templateDescription);
        if (!saving.getStatusCode().is2xxSuccessful())
            return saving;
        return responseEntityFromReplacedModels(nodesetService.generateDocumentationFromNodeSetUrl(nodesetUrl, authorization, htmlTemplate));
    }

    @RestrictInDemoMode
    @ApiOperation(value = "Saves a nodeset file from an URL and generates a documentation with an existing html template")
    @PostMapping("/nodeset/generate-from-url/existing-html-template/")
    public ResponseEntity generateFromUrlExistingHtml(
            @RequestParam("url") String nodesetUrl,
            @RequestParam(value = "authorization", required = false) String authorization,
            @RequestParam("htmlTemplate") String htmlTemplate
    ) {
        return responseEntityFromReplacedModels(nodesetService.generateDocumentationFromNodeSetUrl(nodesetUrl, authorization, htmlTemplate));
    }

    @RestrictInDemoMode
    @ApiOperation(value = "Saves a nodeset file and generates a documentation with the default html template.")
    @PostMapping("/nodeset/generate/default-html-template/")
    public ResponseEntity generateDocumentation(@RequestParam("nodeset") MultipartFile nodeset) {
        return responseEntityFromReplacedModels(nodesetService.generateDocumentationFromNewNodeSetWithExistingTemplate(nodeset, TemplatesService.DEFAULT_TEMPLATE_NAME));
    }

    @RestrictInDemoMode
    @ApiOperation(value = "Saves a nodeset file and generates a documentation with the given html template.")
    @PostMapping("/nodeset/generate/")
    public ResponseEntity generateDocumentation(
            @RequestParam("nodeset") MultipartFile nodeset,
            @RequestParam("htmlTemplate") MultipartFile htmlTemplate,
            @RequestParam(required = false) String templateDescription
    ) {
        ResponseEntity saving = templatesService.saveTemplate(htmlTemplate, templateDescription);
        if (!saving.getStatusCode().is2xxSuccessful())
            return saving;
        return responseEntityFromReplacedModels(nodesetService.generateDocumentationFromNewNodeSetWithNewTemplate(nodeset, htmlTemplate));
    }

    @RestrictInDemoMode
    @ApiOperation(value = "Saves a nodeset file and generates a documentation with an existing html template.")
    @PostMapping("/nodeset/generate/existing-template/")
    public ResponseEntity generateDocumentation(
            @RequestParam("nodeset") MultipartFile nodeset,
            @RequestParam("htmlTemplate") String htmlTemplate
    ) {
        return responseEntityFromReplacedModels(nodesetService.generateDocumentationFromNewNodeSetWithExistingTemplate(nodeset, htmlTemplate));
    }

    //endregion

    //region generateForExistingNodeset
    @ApiOperation(value = "Generates a documentation for an existing file with the default html template.")
    @PostMapping("/nodeset/generate-for-existing-nodeset/default-html-template/")
    public ResponseEntity generateDocumentationForExistingNodeset(@RequestParam("relativePath") String relativePath) {
        return responseEntityFromReplacedModels(nodesetService.generateDocumentationFromExistingNodeSetWithExistingTemplate(relativePath, TemplatesService.DEFAULT_TEMPLATE_NAME));
    }

    @RestrictInDemoMode
    @ApiOperation(value = "Generates a documentation for an existing nodeset file, with a new html template.")
    @PostMapping("/nodeset/generate-for-existing-nodeset/new-template/")
    public ResponseEntity generateDocumentationForExistingNodeset(
            @RequestParam("relativePath") String relativePath,
            @RequestParam("htmlTemplate") MultipartFile htmlTemplate,
            @RequestParam(required = false) String templateDescription
    ) {
        ResponseEntity saving = templatesService.saveTemplate(htmlTemplate, templateDescription);
        if (!saving.getStatusCode().is2xxSuccessful())
            return saving;
        return responseEntityFromReplacedModels(nodesetService.generateDocumentationFromExistingNodeSetWithNewTemplate(relativePath, htmlTemplate));
    }

    @ApiOperation(value = "Generates a documentation for an existing nodeset file, with an existing html template.")
    @PostMapping("/nodeset/generate-for-existing-nodeset/existing-template/")
    public ResponseEntity generateDocumentationForExistingNodeset(
            @RequestParam("relativePath") String relativePath,
            @RequestParam("htmlTemplate") String htmlTemplate
    ) {
        return responseEntityFromReplacedModels(nodesetService.generateDocumentationFromExistingNodeSetWithExistingTemplate(relativePath, htmlTemplate));
    }

    //endregion

    private ResponseEntity<?> responseEntityFromReplacedModels(Map<String, String> replacedModels) {
        if (replacedModels.isEmpty()) {
            return ResponseEntity.ok("Generated documentation");
        } else {
            return ResponseEntity.ok(replacedModels);
        }
    }
}
