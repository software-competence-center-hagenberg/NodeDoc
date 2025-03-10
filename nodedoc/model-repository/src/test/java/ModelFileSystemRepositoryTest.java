import at.scch.nodedoc.ModelFileSystemRepository;
import at.scch.nodedoc.ModelMetaData;
import at.scch.nodedoc.ModelRepository;
import at.scch.nodedoc.parser.*;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ModelFileSystemRepositoryTest {

    private ModelRepository repository;
    private File nodeSetDirectory;

    @BeforeEach
    public void setup() throws IOException {
        var validator = new RawNodeSetValidator(List.of(
                new SimpleNodeIdValidator(),
                new ModelValidator(),
                new BrowseNameValidator()
        ));
        var parser = new NodeSetXMLParser(validator);
        nodeSetDirectory = Files.createTempDirectory("nodeset").toFile();
        repository = new ModelFileSystemRepository(parser, nodeSetDirectory);
    }

    @AfterEach
    public void cleanup() throws IOException {
        FileUtils.deleteDirectory(nodeSetDirectory);
    }

    @Test
    public void testSaveLoadNodeSet() {
        var modelMetaData = repository.saveNodeSet(getClass().getResourceAsStream("/nodesets/Euromap_test_1_00.xml"));
        assertEquals("http://www.difftest.org/euromap83/", modelMetaData.getModelUri());
        assertEquals("www.difftest.org/euromap83/", modelMetaData.getModelUriNoHttp());
        assertEquals("1.00", modelMetaData.getVersion());
        assertEquals(OffsetDateTime.parse("2019-01-28T08:00:00Z"), modelMetaData.getPublicationDate());

        var uaNodeSet = repository.loadNodeSet(modelMetaData);
        assertEquals(46, uaNodeSet.getAliases().size());
    }

    @Test
    public void testLoadNodeSet() {
        var modelMetaData = repository.saveNodeSet(getClass().getResourceAsStream("/nodesets/Euromap_test_1_00.xml"));
        assertEquals("http://www.difftest.org/euromap83/", modelMetaData.getModelUri());
        assertEquals("www.difftest.org/euromap83/", modelMetaData.getModelUriNoHttp());
        assertEquals("1.00", modelMetaData.getVersion());
        assertEquals(OffsetDateTime.parse("2019-01-28T08:00:00Z"), modelMetaData.getPublicationDate());

        var earlierModelMetaData = new ModelMetaData(modelMetaData.getModelUri(), modelMetaData.getVersion(), modelMetaData.getPublicationDate().minusYears(2));
        assertEquals(OffsetDateTime.parse("2017-01-28T08:00:00Z"), earlierModelMetaData.getPublicationDate());

        var uaNodeSet = repository.loadNodeSet(earlierModelMetaData);
        assertEquals(OffsetDateTime.parse("2019-01-28T08:00:00Z"), uaNodeSet.getModels().get(0).getPublicationDate());

        var laterModelMetaData = new ModelMetaData(modelMetaData.getModelUri(), modelMetaData.getVersion(), modelMetaData.getPublicationDate().plusYears(2));
        assertEquals(OffsetDateTime.parse("2021-01-28T08:00:00Z"), laterModelMetaData.getPublicationDate());

        assertThrows(RuntimeException.class, () -> repository.loadNodeSet(laterModelMetaData));

    }
}
