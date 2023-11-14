package at.scch.opcua.service;

import at.scch.nodedoc.NodeSetAnnotator;
import at.scch.nodedoc.XMLWriter;
import at.scch.opcua.exception.NodeDocUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.transform.TransformerException;
import java.io.OutputStream;

@Service
public class NodeSetExportService {

    @Autowired
    private NodeSetAnnotator nodeSetAnnotator;

    @Autowired
    private NodesetService nodesetService;

    @Autowired
    private NodeSetUniverseService nodeSetUniverseService;

    @Autowired
    private XMLWriter xmlWriter;

    public void exportNodeSetXML(String path, OutputStream outputStream) {
        var modelMetaData = nodesetService.getModelMetaDataFromRelativePath(path);
        var nodeSetUniverse = nodeSetUniverseService.loadNodeSetUniverse(modelMetaData);
        var nodeSet = nodeSetUniverse.getNodeSetByNamespaceUri(modelMetaData.getModelUri());
        nodeSetAnnotator.annotateNodeSetWithCurrentDocumentation(nodeSet);
        try {
            xmlWriter.writeXML(nodeSet, outputStream);
        } catch (TransformerException e) {
            throw new NodeDocUserException("NodeSet export failed", e);
        }
    }
}
