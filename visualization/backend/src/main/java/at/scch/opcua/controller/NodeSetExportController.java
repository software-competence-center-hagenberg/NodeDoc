package at.scch.opcua.controller;

import at.scch.opcua.service.NodeSetExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@CrossOrigin
public class NodeSetExportController {

    @Autowired
    private NodeSetExportService nodeSetExportService;

    @GetMapping(value = "/export/**", produces = "application/xml")
    public void exportNodeSet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var pathToNodeSet = request.getRequestURI().substring("/export/".length());
        nodeSetExportService.exportNodeSetXML(pathToNodeSet, response.getOutputStream());
    }
}
