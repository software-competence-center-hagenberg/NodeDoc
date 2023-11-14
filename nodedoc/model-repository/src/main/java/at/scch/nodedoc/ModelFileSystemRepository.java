package at.scch.nodedoc;

import at.scch.nodedoc.parser.NodeSetXMLParser;
import at.scch.nodedoc.parser.rawModel.RawNodeSet;
import org.apache.commons.io.IOUtils;
import org.xml.sax.SAXException;

import java.io.*;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

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
            if (targetDirectory.exists() && Arrays.stream(targetDirectory.listFiles()).anyMatch(file -> file.getName().endsWith(".xml"))) {
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
            var versionDirectories = Optional.ofNullable(getNodeSetVersionsDirectory(metaData).listFiles()).stream()
                    .flatMap(Arrays::stream);

            var publicationDateDirectories = versionDirectories
                    .map(File::listFiles)
                    .filter(Objects::nonNull)
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
        return new File(nodeSetDirectory, Arrays.stream(nodeSetDirectory.list()).filter(f -> f.endsWith(".xml")).findFirst().orElseThrow());
    }

    @Override
    public File getFileForNodeSet(ModelMetaData metaData) {
        return getNodeSetFileFromMetaData(metaData);
    }
}
