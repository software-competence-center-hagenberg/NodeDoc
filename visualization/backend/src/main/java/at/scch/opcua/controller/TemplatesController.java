package at.scch.opcua.controller;

import at.scch.opcua.demomode.RestrictInDemoMode;
import at.scch.opcua.service.TemplatesService;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@CrossOrigin
@AllArgsConstructor
public class TemplatesController {

    private final TemplatesService templatesService;

    @ApiOperation(value = "Returns an array of templates")
    @RequestMapping(value = "/templates/", method = RequestMethod.GET)
    public ResponseEntity getTemplates() {
        return templatesService.getTemplates();
    }

    @RestrictInDemoMode
    @ApiOperation(value = "Deletes a specified nodeset by its relative path")
    @DeleteMapping("/template/")
    public ResponseEntity deleteTemplateByRelativePath(@RequestParam String path) {
        return templatesService.deleteTemplateByRelativePath(path);
    }

    @RestrictInDemoMode
    @ApiOperation(value = "Saves a new html template")
    @PostMapping("/template/")
    public ResponseEntity saveTemplate(
            @RequestParam("htmlTemplate") MultipartFile htmlTemplate,
            @RequestParam(required = false) String templateDescription
    ) {
        return templatesService.saveTemplate(htmlTemplate, templateDescription);
    }
}
