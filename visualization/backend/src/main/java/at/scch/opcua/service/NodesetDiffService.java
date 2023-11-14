package at.scch.opcua.service;

import at.scch.nodedoc.ModelMetaData;
import at.scch.nodedoc.NodeSetAnnotator;
import at.scch.nodedoc.diffengine.DiffContext;
import at.scch.nodedoc.documentation.DocumentationGenerator;
import at.scch.nodedoc.documentation.diff.generator.DiffDisplayNodeSetGenerator;
import at.scch.nodedoc.modelresolver.DependencyResolver;
import at.scch.nodedoc.parser.NodeSetXMLParser;
import at.scch.nodedoc.parser.rawModel.RawNodeSet;
import at.scch.opcua.config.NodeDocConfiguration;
import at.scch.opcua.exception.NodeDocUserException;
import at.scch.opcua.metadata.DiffMetadata;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class NodesetDiffService {

    private final NodeDocConfiguration config;
    private final DocumentationGenerator documentationGenerator;
    private final NodesetDiffStorageService diffStorageService;
    private final NodeSetXMLParser nodeSetXmlParser;
    private final DependencyResolver dependencyResolver;
    private final NodeSetUniverseService nodeSetUniverseService;
    private final DiffDisplayNodeSetGenerator diffDisplayNodeSetGenerator;
    private final NodeSetAnnotator nodeSetAnnotator;
    private final TemplatesService templatesService;

    public void generateDiff(String baseFilePath, String compareFilePath) {
        var baseNodeSetMetaData = getModelMetaDataFromPath(baseFilePath);
        var compareNodeSetMetaData = getModelMetaDataFromPath(compareFilePath);

        var baseNodeSetDependencies = dependencyResolver.collectDependencies(baseNodeSetMetaData);
        var compareNodeSetDependencies = dependencyResolver.collectDependencies(compareNodeSetMetaData);
        var commonDependencies = dependencyResolver.getNewestModelMetaData(
                Stream.concat(baseNodeSetDependencies.stream(), compareNodeSetDependencies.stream())
        );

        var baseNodeSetUniverse = nodeSetUniverseService.loadNodeSetUniverse(baseNodeSetMetaData, commonDependencies);
        var compareNodeSetUniverse = nodeSetUniverseService.loadNodeSetUniverse(compareNodeSetMetaData, commonDependencies);

        nodeSetAnnotator.annotateNodeSetWithCurrentDocumentation(baseNodeSetUniverse.getNodeSetByNamespaceUri(baseNodeSetMetaData.getModelUri()));
        nodeSetAnnotator.annotateNodeSetWithCurrentDocumentation(compareNodeSetUniverse.getNodeSetByNamespaceUri(compareNodeSetMetaData.getModelUri()));

        var diffContext = new DiffContext(baseNodeSetUniverse, compareNodeSetUniverse);
        var diffDisplayNodeSet = diffDisplayNodeSetGenerator.generateDiffDocumentation(diffContext, baseNodeSetMetaData.getModelUri());

        var diffFile = diffStorageService.createNewDiff();
        try {
            documentationGenerator.generateDocumentation(diffDisplayNodeSet, new DocumentationGenerator.Config(config.getDocumentationGeneration().getCouchDbUri()), new FileOutputStream(diffFile), templatesService.getDefaultTemplateStream());
        } catch (IOException e) {
            throw new NodeDocUserException("Failed to write diff to file", e);
        }

        var metadata = new DiffMetadata(
                buildModelStringForMetadata(baseNodeSetMetaData),
                buildModelStringForMetadata(compareNodeSetMetaData),
                LocalDateTime.now());

        diffStorageService.saveMetadataForDiff(diffFile, metadata);
    }

    private String buildModelStringForMetadata(ModelMetaData modelMetaData) {
        return modelMetaData.getModelUri() + " (" + modelMetaData.getVersion() + " / " + modelMetaData.getPublicationDate() + ")";
    }

    private ModelMetaData getModelMetaDataFromPath(String filePath) {
        // TODO: workaround

        InputStream nodesetStream;

        try {
            nodesetStream = Files.newInputStream(Paths.get(config.getDirectory().getNodesets(), filePath));
        } catch (IOException e) {
            throw new NodeDocUserException("Could not access " + filePath, e);
        }

        RawNodeSet nodeSet;

        try {
            nodeSet = nodeSetXmlParser.parseXML(nodesetStream);
        } catch (IOException | SAXException e) {
            throw new NodeDocUserException("Could not access nodeset", e); // TODO
        }

        return new ModelMetaData(
                nodeSet.getModels().get(0).getModelUri(),
                nodeSet.getModels().get(0).getVersion(),
                nodeSet.getModels().get(0).getPublicationDate()
        );
    }

    public boolean deleteDiff(String diffPath) {
        Path path = Paths.get(config.getDirectory().getDiffs(), diffPath);
        File file = path.getParent().toFile();
        System.out.println(file.getAbsolutePath());
        if (file.exists()) {

            try {
                FileUtils.deleteDirectory(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return !path.toFile().exists();
    }
}
