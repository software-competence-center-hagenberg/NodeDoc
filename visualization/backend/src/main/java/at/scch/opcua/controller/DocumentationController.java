package at.scch.opcua.controller;

import at.scch.opcua.service.DocuService;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@CrossOrigin
@AllArgsConstructor
public class DocumentationController {
    private final DocuService docuService;

    @ApiOperation(value = "Deletes a specified documentation by its relative path")
    @DeleteMapping("/documentation/")
    public ResponseEntity deleteDocumentationByRelativePath(@RequestParam String path) {
        return docuService.deleteDocumentationFileByRelativePath(path);
    }

}
