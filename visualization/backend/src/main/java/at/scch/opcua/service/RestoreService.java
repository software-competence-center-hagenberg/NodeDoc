package at.scch.opcua.service;

import at.scch.nodedoc.db.documents.NodeSetText;
import at.scch.nodedoc.db.repository.NodeDescriptionRepository;
import at.scch.nodedoc.util.StreamUtils;
import at.scch.opcua.config.NodeDocConfiguration;
import at.scch.opcua.exception.NodeDocUserException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RestoreService {

    @Value("${application.version}")
    private String applicationVersion;

    @Autowired
    private NodeDocConfiguration config;

    @Autowired
    private NodeDescriptionRepository nodeDescriptionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BackupCommons backupCommons;

    private final int DOC_ENTRIES_DELETION_CHUNK_SIZE = 5000;

    public void importZipFile(String fileName, boolean overrideIfExists, Consumer<File> fileWriter) {
        var destinationFile = new File(backupCommons.getBackupsDirectory(), fileName);
        if (!overrideIfExists && destinationFile.exists()) {
            throw new NodeDocUserException("Cannot save file: " + fileName + " already exists");
        }
        if (!destinationFile.exists()) {
            log.info("Start importing ZIP at {}", destinationFile);
        } else {
            log.info("Start importing ZIP at {}, overriding existing file", destinationFile);
        }
        fileWriter.accept(destinationFile);
        log.info("Imported ZIP {}", fileName);
    }

    public void restoreFromBackup(String fileName, boolean ignoreVersionMismatch) {
        var backupFile = new File(backupCommons.getBackupsDirectory(), fileName);
        if (!backupFile.exists()) {
            throw new NodeDocUserException("Cannot restore from " + fileName + ", file does not exist");
        }
        log.info("Start restoring backup from {}", fileName);
        try (var zipFile = new ZipFile(backupFile)) {
            validateMetaInfoOrThrow(zipFile, ignoreVersionMismatch);
            restoreFolder(zipFile, BackupCommons.NODESETS_FOLDER, config.getDirectory().getNodesets());
            log.info("Restored NodeSets and generated documentation");
            restoreFolder(zipFile, BackupCommons.DIFFS_FOLDER, config.getDirectory().getDiffs());
            log.info("Restored diffs");
            restoreFolder(zipFile, BackupCommons.TEMPLATES_FOLDER, config.getDirectory().getTemplates());
            log.info("Restored templates");
            restoreDocs(zipFile);
            log.info("Finished restore from backup");
        } catch (IOException e) {
            throw new NodeDocUserException("Unable to read backup: " + backupFile, e);
        }
    }

    private void validateMetaInfoOrThrow(ZipFile zipFile, boolean ignoreVersionMismatch) {
        var meta = readMetaFile(zipFile);
        if (!meta.getNodeDocVersion().equals(applicationVersion) && !ignoreVersionMismatch) {
            throw new NodeDocUserException("Unable to restore backup: NodeDoc version in backup (" + meta.getNodeDocVersion() + ") does not match current version (" + applicationVersion + ") and exact match was required");
        }
        log.info("Successfully validated meta info");
    }

    private BackupCommons.Meta readMetaFile(ZipFile zipFile) {
        var metaAsString = readFileContent(zipFile, BackupCommons.META_FILE_NAME);
        try {
            var metaInfo = objectMapper.readValue(metaAsString, BackupCommons.Meta.class);
            return metaInfo;
        } catch (JsonProcessingException e) {
            throw new NodeDocUserException("Unable to restore backup: Unprocessable meta info file " + BackupCommons.META_FILE_NAME, e);
        }
    }

    private String readFileContent(ZipFile zipFile, String fileName) {
        try {
            var fileHeader = zipFile.getFileHeader(fileName);
            var inputStream = zipFile.getInputStream(fileHeader);
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new NodeDocUserException("Unable to read " + fileName + " from ZIP file", e);
        }
    }

    private void restoreFolder(ZipFile zipFile, String folderInZip, String targetFolder) throws ZipException {
        // Clear target folder
        Optional.ofNullable(new File(targetFolder).listFiles()).stream()
                .flatMap(Arrays::stream)
                .forEach(FileUtils::deleteQuietly);

        // Find and restore files and folders in relevant folder in ZIP
        var fileHeadersInFolder = zipFile.getFileHeaders().stream()
                .filter(fileHeader -> fileHeader.getFileName().matches(folderInZip + "/[^/]+/?"))
                .collect(Collectors.toList());
        for (var fileHeader : fileHeadersInFolder) {
            var fileName = fileHeader.getFileName().split("/")[1];
            zipFile.extractFile(fileHeader, targetFolder, fileName);
        }
    }

    private void restoreDocs(ZipFile zipFile) throws ZipException, JsonProcessingException {
        var docsDeleted = nodeDescriptionRepository.deleteAllNodeSetTexts(DOC_ENTRIES_DELETION_CHUNK_SIZE);
        log.info("Deleted {} documentation entries in database", docsDeleted);
        var fileHeaders = StreamUtils.toIterable(zipFile.getFileHeaders().stream()
                .filter(fileHeader -> fileHeader.getFileName().startsWith(BackupCommons.DOC_ENTRIES_FOLDER + "/")));
        var docsRestored = 0;
        for (var fileHeader : fileHeaders) {
            var documentsAsString = readFileContent(zipFile, fileHeader.getFileName());
            var documents = objectMapper.readValue(documentsAsString, new TypeReference<List<NodeSetText>>() {});
            nodeDescriptionRepository.save(documents);
            docsRestored += documents.size();
        }
        log.info("Restored {} documentation entries into the database", docsRestored);
    }
}
