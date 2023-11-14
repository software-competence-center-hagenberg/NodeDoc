package at.scch.opcua.service;

import at.scch.nodedoc.MatchingNodeSetWithMinimumPublicationDateNotFoundException;
import at.scch.nodedoc.ModelMetaData;
import at.scch.nodedoc.ModelRepository;
import at.scch.nodedoc.documentation.DocumentationGenerator;
import at.scch.nodedoc.documentation.single.generator.SingleDisplayNodeSetGenerator;
import at.scch.nodedoc.nodeset.NodeSetUniverse;
import at.scch.nodedoc.parser.NodeSetXMLParser;
import at.scch.nodedoc.parser.rawModel.RawNodeSet;
import at.scch.opcua.config.NodeDocConfiguration;
import at.scch.opcua.dto.FilestructureDirectoryNode;
import at.scch.opcua.exception.NodeDocUserException;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class NodesetService {

    private final NodeDocConfiguration config;
    private final FileStructureService fileStructureService;
    private final ModelRepository modelRepository;
    private final NodeSetXMLParser nodeSetXmlParser;
    private final SingleDisplayNodeSetGenerator singleDisplayNodeSetGenerator;
    private final DocumentationGenerator documentationGenerator;
    private final NodeSetUniverseService nodeSetUniverseService;
    private final TemplatesService templatesService;


    // region save nodeset

    /**
     * Saves a nodeset under its URI, version, publication date
     *
     * @param nodeset - the nodeset which should be saved
     * @return - a response giving information whether the nodeset could be saved or not. Also gives information why the
     * nodeset could not be saved (invalid files, exception while saving the file, etc.)
     */
    public ModelMetaData saveNodeSetFromMultipartFile(MultipartFile nodeset) {
        try {
            return modelRepository.saveNodeSet(nodeset.getInputStream());
        } catch (IOException e) {
            throw new NodeDocUserException("Could not save nodesetFile - " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new NodeDocUserException(e.getMessage(), e);
        }
    }

    public ModelMetaData saveNodeSetFromFileContent(String fileContent) {
        return modelRepository.saveNodeSet(new ByteArrayInputStream(fileContent.getBytes()));
    }

    /**
     * Saves a nodeset file from an url to the file system.
     *
     * @param url           - location of the file
     * @param authorization - optional parameter for accessing a restricted url
     * @return whether the file could be saved or not
     */
    public ModelMetaData saveNodeSetFromUrl(String url, String authorization) {
        if (!url.endsWith(".xml")) {
            throw new NodeDocUserException("URL does not end with .xml");
        }

        Unirest.setTimeouts(0, 0);
        try {
            GetRequest request = Unirest.get(url);
            request.header("Content-Type", "application/xml");
            if (authorization != null)
                request.header("Authorization", authorization);

            String fileContent = request.asString().getBody();

            return saveNodeSetFromFileContent(fileContent);
        } catch (UnirestException e) {
            e.printStackTrace();
            throw new NodeDocUserException(e.getMessage(), e);
        }
    }

    // endregion


    // region generate documentation from nodeset url

    public Map<String, String> generateDocumentationFromNodeSetUrl(String nodeSetUrl, String authorization, String htmlTemplatePath) {
        InputStream templateStream = templatesService.getTemplateStreamFromTemplatePath(htmlTemplatePath);
        return generateDocumentationFromNodeSetUrl(nodeSetUrl, authorization, templateStream);
    }

    public Map<String, String> generateDocumentationFromNodeSetUrl(String nodeSetUrl, String authorization, MultipartFile htmlTemplateFile) {
        InputStream templateStream = getTemplateStreamFromTemplateFile(htmlTemplateFile);
        return generateDocumentationFromNodeSetUrl(nodeSetUrl, authorization, templateStream);
    }

    private Map<String, String> generateDocumentationFromNodeSetUrl(String nodeSetUrl, String authorization, InputStream htmlTemplateStream) {
        var modelMetaData = saveNodeSetFromUrl(nodeSetUrl, authorization);
        return generateDocumentationFromExistingNodeSet(modelMetaData, htmlTemplateStream);
    }

    // endregion


    //region generate documentation for new nodeset

    /**
     * Generates a documentation for a new nodeset file and an existing html template
     *
     * @param nodeset          - the nodeset file which should be documented
     * @param htmlTemplatePath - the path to the html template
     * @return - a response with info if a documentation could be generated or not (and why)
     */
    public Map<String, String> generateDocumentationFromNewNodeSetWithExistingTemplate(MultipartFile nodeset, String htmlTemplatePath) {
        return generateDocumentationFromNewNodeSet(nodeset, templatesService.getTemplateStreamFromTemplatePath(htmlTemplatePath));
    }

    /**
     * Generates a documentation for a new nodeset with a new html template
     *
     * @param nodeset          - new nodeset file
     * @param htmlTemplateFile - new template file
     * @return - whether the documentation could be generated or not (and why)
     */
    public Map<String, String> generateDocumentationFromNewNodeSetWithNewTemplate(MultipartFile nodeset, MultipartFile htmlTemplateFile) {
        return generateDocumentationFromNewNodeSet(nodeset, getTemplateStreamFromTemplateFile(htmlTemplateFile));
    }

    /**
     * Generates a documentation for a nodeset and an html template. First the nodeset and its required models are read
     * in, then the nodeset is saved to the file structure. If the nodeset references other nodesets that are not
     * present in the current publication date, then another publication date is used and the differences are returned.
     * The last step is generating the documentation with the main model, html template and the URI to the database.
     *
     * @param nodesetFile        - nodeset file for which the documentation should be generated
     * @param htmlTemplateStream - a stream of the html template
     * @return - whether the documentation could be generated or not (and why)
     */
    private Map<String, String> generateDocumentationFromNewNodeSet(MultipartFile nodesetFile, InputStream htmlTemplateStream) {
        ModelMetaData modelMetaData = saveNodeSetFromMultipartFile(nodesetFile);
        return generateDocumentationFromExistingNodeSet(modelMetaData, htmlTemplateStream);
    }

    // endregion


    // region generate documentation from existing nodeset

    /**
     * Generates a documentation for an existing nodeset with a new html template
     *
     * @param relativePath     - the path to the nodeset
     * @param htmlTemplateFile - the new html template
     * @return - whether the documentation could be generated or not (and why)
     */
    public Map<String, String> generateDocumentationFromExistingNodeSetWithNewTemplate(String relativePath, MultipartFile htmlTemplateFile) {
        return generateDocumentationFromExistingNodeSet(getModelMetaDataFromRelativePath(relativePath), getTemplateStreamFromTemplateFile(htmlTemplateFile));
    }

    /**
     * Generates a documentation for an existing nodeset and an existing html template
     *
     * @param relativePath     - path to the nodeset
     * @param htmlTemplatePath - path to the html template
     * @return - whether the documentation could be generated or not (and why)
     */
    public Map<String, String> generateDocumentationFromExistingNodeSetWithExistingTemplate(String relativePath, String htmlTemplatePath) {
        return generateDocumentationFromExistingNodeSet(getModelMetaDataFromRelativePath(relativePath), templatesService.getTemplateStreamFromTemplatePath(htmlTemplatePath));
    }

    public Map<String, String> generateDocumentationFromExistingNodeSet(ModelMetaData modelMetaData, InputStream htmlTemplateStream) {
        NodeSetUniverse nodeSetUniverse = null;
        try {
            nodeSetUniverse = nodeSetUniverseService.loadNodeSetUniverse(modelMetaData);
        } catch (MatchingNodeSetWithMinimumPublicationDateNotFoundException e) {
            throw new NodeDocUserException("Missing NodeSet: " + e.getModelMetaData().getModelUri() + "(" + e.getModelMetaData().getVersion() + " / >= " + e.getModelMetaData().getPublicationDate() + ")", e);
        }
        var singleDisplayNodeSet = singleDisplayNodeSetGenerator.generateSingleDocumentation(nodeSetUniverse, modelMetaData.getModelUri());
        var nodeSetFile = modelRepository.getFileForNodeSet(modelMetaData);
        var outputDir = nodeSetFile.getParentFile();
        var htmlFile = new File(outputDir, nodeSetFile.getName().replace(".xml", ".html"));

        try {
            documentationGenerator.generateDocumentation(singleDisplayNodeSet, new DocumentationGenerator.Config(config.getDocumentationGeneration().getCouchDbUri()), new FileOutputStream(htmlFile), htmlTemplateStream);
        } catch (IOException e) {
            throw new NodeDocUserException("Failed to write documentation to file", e);
        }

        return new HashMap<>(); // TODO
    }

    // endregion


    // region helpers

    // TODO: remove workaround from relative path
    public ModelMetaData getModelMetaDataFromRelativePath(String relativePath) {
        RawNodeSet rawNodeSet;

        try {
            rawNodeSet = nodeSetXmlParser.parseXML(Files.newInputStream(Paths.get(config.getDirectory().getNodesets(), relativePath)));
        } catch (IOException | SAXException e) {
            throw new NodeDocUserException("Could not read NodeSet");
        }

        return new ModelMetaData(
                rawNodeSet.getModels().get(0).getModelUri(),
                rawNodeSet.getModels().get(0).getVersion(),
                rawNodeSet.getModels().get(0).getPublicationDate());
    }

    private InputStream getTemplateStreamFromTemplateFile(MultipartFile htmlTemplateFile) {
        try {
            return htmlTemplateFile.getInputStream();
        } catch (IOException e) {
            throw new NodeDocUserException("Could not access html template");
        }
    }

    private void deleteEmptyDocumentationFolder(Path docuPath) {
        File file = docuPath.toFile();
        Path js = Paths.get(docuPath.toString(), "js");
        try {
            FileUtils.deleteDirectory(js.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // file.delete() only deletes a file/directory if it has no children
        do {
            file.delete();
            file = file.getParentFile();
        } while (!file.getPath().equals(config.getDirectory().getNodesets()));
    }

    /**
     * Deletes a nodeset file specified by its path. Also deletes all parent folders that are empty.
     *
     * @param relativePath - the path to a nodeset consisting of the URI, version and publication date
     * @return - returns whether the file could be deleted or not
     */
    public boolean deleteNodesetFileByRelativePath(String relativePath) {
        Path path = Paths.get(config.getDirectory().getNodesets(), relativePath);
        File file = path.toFile();
        if (file.exists() && !file.getPath().equals(config.getDirectory().getNodesets())) {
            try {
                if (!file.isFile())
                    FileUtils.deleteDirectory(file);
                do {
                    file.delete();
                    file = file.getParentFile();
                } while (!file.getPath().equals(config.getDirectory().getNodesets()));
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            // file.delete() only deletes a file/directory if it has no children

            return !path.toFile().exists();
        }
        return false;
    }

    /**
     * Calls the file structure service which returns a json object as string.
     *
     * @return - json object representing the file structure
     */
    public FilestructureDirectoryNode getNodeSetFiles() {
        return fileStructureService.getFileStructure(new File(config.getDirectory().getNodesets()));
    }

    // endregion
}
