package at.scch.opcua.service;

import at.scch.nodedoc.NodeSetAnnotator;
import at.scch.nodedoc.XMLWriter;
import at.scch.opcua.exception.NodeDocUserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.transform.TransformerException;
import java.io.OutputStream;

@Service
@Slf4j
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
        log.info("Export NodeSet XML for {} to OutputStream", path);
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
