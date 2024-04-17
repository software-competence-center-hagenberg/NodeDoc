package at.scch.nodedoc;

import at.scch.nodedoc.parser.NodeSetXMLParser;
import at.scch.nodedoc.parser.rawModel.RawNodeSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.javatuples.Pair;
import org.xml.sax.SAXException;

import java.io.*;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public class ModelFileSystemRepository implements ModelRepository {

    public static final String NODESET_FILENAME = "nodeset.xml";
    public static final DateTimeFormatter PUBLICATION_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final NodeSetXMLParser parser;
    private final File nodeSetRootDirectory;

    public ModelFileSystemRepository(NodeSetXMLParser parser, File nodeSetRootDirectory) {
        this.parser = parser;
        this.nodeSetRootDirectory = nodeSetRootDirectory;
    }

    private File getNodeSetDirectory(ModelMetaData metaData) {
        return Paths.get(
                getNodeSetVersionsDirectory(metaData).toString(),
                metaData.getVersion(),
                metaData.getPublicationDate().format(PUBLICATION_DATE_FORMATTER)
        ).toFile();
    }

    private File getNodeSetVersionsDirectory(ModelMetaData metaData) {
        return Paths.get(
                nodeSetRootDirectory.toString(),
                metaData.getModelUriNoHttp(),
                "versions"
        ).toFile();
    }

    @Override
    public RawNodeSet loadNodeSet(ModelMetaData metaData) {
        try {
            var nodeSetFile = getNodeSetFileFromMetaData(metaData);
            var inputStream = new FileInputStream(nodeSetFile);
            return parser.parseXML(inputStream);
        } catch (IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ModelMetaData saveNodeSet(InputStream contents) {
        try {
            var contentString = IOUtils.toString(contents);
            var uaNodeSet = parser.parseXML(IOUtils.toInputStream(contentString));
            var model = uaNodeSet.getModels().get(0);
            var modelMetaData = new ModelMetaData(model.getModelUri(), model.getVersion(), model.getPublicationDate());
            var targetDirectory = getNodeSetDirectory(modelMetaData);
            if (targetDirectory.exists() && Arrays.stream(listFilesOrEmpty(targetDirectory)).anyMatch(file -> file.getName().endsWith(".xml"))) {
                throw new IllegalArgumentException("An XML-file already exists with the metadata: \n" + modelMetaData);
            }
            targetDirectory.mkdirs();
            try (var outputStream = new FileOutputStream(new File(targetDirectory, NODESET_FILENAME))) {
                IOUtils.write(contentString, outputStream);
            }
            return modelMetaData;
        } catch (IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private File getNodeSetFileFromMetaData(ModelMetaData metaData) {
        var nodeSetDirectory = getNodeSetDirectory(metaData);

        if (!nodeSetDirectory.exists()) {
            var versionDirectories = Arrays.stream(listFilesOrEmpty(getNodeSetVersionsDirectory(metaData)));

            var publicationDateDirectories = versionDirectories
                    .map(this::listFilesOrEmpty)
                    .flatMap(Arrays::stream);

            nodeSetDirectory = publicationDateDirectories
                    .filter(dir ->
                            {
                                var odt = LocalDate
                                        .parse(dir.getName(), PUBLICATION_DATE_FORMATTER)
                                        .atTime(OffsetTime.MIN);
                                return !odt.isBefore(metaData.getPublicationDate());
                            }
                    )
                    .min(Comparator.naturalOrder()).orElseThrow(() ->
                            new MatchingNodeSetWithMinimumPublicationDateNotFoundException(metaData)
                    );
        }
        return new File(nodeSetDirectory, Arrays.stream(listOrEmpty(nodeSetDirectory)).filter(f -> f.endsWith(".xml")).findFirst().orElseThrow());
    }

    @Override
    public File getFileForNodeSet(ModelMetaData metaData) {
        return getNodeSetFileFromMetaData(metaData);
    }

    @Override
    public boolean nodeSetWithModelUriExists(ModelMetaData modelMetaData) {
        var versionsDirectory = getNodeSetVersionsDirectory(modelMetaData);
        return versionsDirectory.exists() && Arrays.stream(listFilesOrEmpty(versionsDirectory)) // directories of versions
                .map(this::listFilesOrEmpty)
                .flatMap(Arrays::stream) // directories of publication dates
                .map(this::listFilesOrEmpty)
                .flatMap(Arrays::stream) // NodeSet files (and more)
                .anyMatch(f -> f.getName().equals(NODESET_FILENAME));
    }

    private File[] listFilesOrEmpty(File file) {
        return Objects.requireNonNullElseGet(file.listFiles(), () -> new File[0]);
    }

    private String[] listOrEmpty(File file) {
        return Objects.requireNonNullElseGet(file.list(), () -> new String[0]);
    }

    @Override
    public Pair<Boolean, Collection<ModelMetaData>> deleteAllNodeSetsStartingAt(String relativePath) {
        var result = new ArrayList<ModelMetaData>();
        var file = new File(nodeSetRootDirectory, relativePath);
        deleteFileRecursively(file, result);
        return Pair.with(!file.exists(), result);
    }

    private void deleteFileRecursively(File file, Collection<ModelMetaData> deletedNodeSets) {
        if (file.exists() && file.exists() && !file.equals(nodeSetRootDirectory)) {
            ModelMetaData modelMetaData = null;
            if (file.isFile()) {
                if (file.getName().endsWith(".xml")) {
                    modelMetaData = getModelMetaDataFromFile(file);
                }
            } else {
                Arrays.stream(listFilesOrEmpty(file))
                        .forEach(f -> deleteFileRecursively(f, deletedNodeSets));
            }

            if (file.delete()) {
                if (modelMetaData != null) {
                    deletedNodeSets.add(modelMetaData);
                }
            } else {
                log.warn("Unable to delete {}", file.getAbsoluteFile());
            }
        }
    }

    private ModelMetaData getModelMetaDataFromFile(File file) {
        try (var inputStream = new FileInputStream(file)) {
            RawNodeSet rawNodeSet = parser.parseXML(inputStream);
            return new ModelMetaData(
                    rawNodeSet.getModels().get(0).getModelUri(),
                    rawNodeSet.getModels().get(0).getVersion(),
                    rawNodeSet.getModels().get(0).getPublicationDate());
        } catch (IOException | SAXException e) {
            return null;
        }
    }
}
