package at.scch.opcua.service;

import at.scch.opcua.config.NodeDocConfiguration;
import at.scch.opcua.dto.DiffInfo;
import at.scch.opcua.exception.InternalException;
import at.scch.opcua.metadata.DiffMetadata;
import at.scch.opcua.util.PathUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class NodesetDiffStorageService {

    private final String METADATA_FILENAME = "metadata.json";
    private final String DIFF_FILENAME = "diff.html";

    @Autowired
    private NodeDocConfiguration config;

    @Autowired
    private ObjectMapper objectMapper;

    public List<DiffInfo> getStoredDiffs() {
        log.info("Load stored diffs");
        return Arrays.stream(getDiffDirectory().listFiles())
                .filter(File::isDirectory)
                .flatMap(directory -> {
                    var metadataFile = new File(directory, METADATA_FILENAME);
                    if (metadataFile.exists()) {
                        var metadata = readMetadata(metadataFile);
                        var diffFile = new File(directory, DIFF_FILENAME);
                        return Stream.of(new DiffInfo(
                                metadata.getBaseNodeset(),
                                metadata.getCompareNodeset(),
                                PathUtils.getRelativePath(getDiffDirectory(), diffFile).toString(),
                                metadata.getGenerated()
                        ));
                    } else {
                        return Stream.of();
                    }
                })
                .collect(Collectors.toList());
    }

    private File getDiffDirectory() {
        var diffDirectory = new File(config.getDirectory().getDiffs());
        diffDirectory.mkdirs();
        return diffDirectory;
    }

    public File createNewDiff() {
        log.info("Create new diff");
        var directory = createRandomDirectory();
        return new File(directory, DIFF_FILENAME);
    }

    private File createRandomDirectory() {
        File directory;
        do {
            var name = UUID.randomUUID().toString();
            directory = new File(config.getDirectory().getDiffs(), name);
        } while (directory.exists());
        directory.mkdirs();
        return directory;
    }

    public void saveMetadataForDiff(File diffFile, DiffMetadata metadata) {
        log.info("Save meta data for diff {}", diffFile);
        var metadataFile = new File(diffFile.getParentFile(), METADATA_FILENAME);
        writeMetadata(metadata, metadataFile);
    }

    private DiffMetadata readMetadata(File file) {
        try {
            return objectMapper.readValue(file, DiffMetadata.class);
        } catch (IOException e) {
            throw new InternalException("Cannot access meta data file " + file, e);
        }
    }

    private void writeMetadata(DiffMetadata metadata, File file) {
        try {
            objectMapper.writeValue(file, metadata);
        } catch (IOException e) {
            throw new InternalException("Cannot write meta data for diff to " + file, e);
        }
    }
}
