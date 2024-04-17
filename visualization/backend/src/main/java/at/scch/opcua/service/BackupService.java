package at.scch.opcua.service;

import at.scch.nodedoc.db.repository.NodeDescriptionRepository;
import at.scch.nodedoc.util.StreamUtils;
import at.scch.opcua.config.NodeDocConfiguration;
import at.scch.opcua.exception.NodeDocUserException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class BackupService {

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

    private final int DOC_ENTRIES_PER_CHUNK = 5000;

    public File createNewBackupZIP() {
        File backupFile = generateBackupFile();
        var zipParameters = createAndConfigureZipParameters();

        log.info("Start creating backup at {}", backupFile);
        try (var zipFile = new ZipFile(backupFile)) {
            saveMetaFile(zipFile);
            addFolderContents(zipFile, config.getDirectory().getNodesets(), BackupCommons.NODESETS_FOLDER);
            log.info("Saved NodeSets and generated documentation");
            addFolderContents(zipFile, config.getDirectory().getDiffs(), BackupCommons.DIFFS_FOLDER);
            log.info("Saved diffs");
            addFolderContents(zipFile, config.getDirectory().getTemplates(), BackupCommons.TEMPLATES_FOLDER);
            log.info("Saved templates");
            saveDocs(zipFile);
        } catch (IOException e) {
            throw new NodeDocUserException("Unable to create backup: " + backupFile, e);
        }

        log.info("Finished creating backup at {}", backupFile);
        return backupFile;
    }

    private void addFolderContents(ZipFile zipFile, String sourceFolder, String folderInZip) throws ZipException {
        var zipParameters = createAndConfigureZipParameters();
        zipParameters.setRootFolderNameInZip(folderInZip);
        var filesInSourceFolder = Optional.ofNullable(new File(sourceFolder).listFiles()).stream()
                .flatMap(Arrays::stream)
                .collect(Collectors.toList());
        for (var file : filesInSourceFolder) {
            if (file.isDirectory()) {
                zipFile.addFolder(file, zipParameters);
            } else {
                zipFile.addFile(file, zipParameters);
            }
        }
    }

    private ZipParameters createAndConfigureZipParameters() {
        var zipParameters = new ZipParameters();
        zipParameters.setCompressionMethod(CompressionMethod.DEFLATE);
        zipParameters.setCompressionLevel(CompressionLevel.ULTRA);
        return zipParameters;
    }

    private void saveMetaFile(ZipFile zipFile) throws JsonProcessingException, ZipException {
        var meta = new BackupCommons.Meta(applicationVersion);
        var metaAsString = objectMapper.writeValueAsString(meta);
        writeStringContentToZipFile(BackupCommons.META_FILE_NAME, zipFile, metaAsString);
        log.info("Saved meta info");
    }

    private void saveDocs(ZipFile zipFile) throws JsonProcessingException, ZipException {
        long docsSaved = 0;
        for (var page : StreamUtils.toIterable(nodeDescriptionRepository.getAllNodeSetTexts(DOC_ENTRIES_PER_CHUNK))) {
            var documentsAsString = objectMapper.writeValueAsString(page.getDocs());
            var fileName = BackupCommons.DOC_ENTRIES_FOLDER + "/chunk-" + page.getPageNumber() + ".json";
            writeStringContentToZipFile(fileName, zipFile, documentsAsString);
            docsSaved += page.getDocs().size();
        }

        log.info("Saved {} documentation entries to the backup", docsSaved);
    }

    private void writeStringContentToZipFile(String fileNameInZip, ZipFile zipFile, String metaAsString) throws ZipException {
        var zipParameters = createAndConfigureZipParameters();
        zipParameters.setFileNameInZip(fileNameInZip);
        zipFile.addStream(new ByteArrayInputStream(metaAsString.getBytes(StandardCharsets.UTF_8)), zipParameters);
    }

    private File generateBackupFile() {
        return IntStream.iterate(0, x -> x + 1)
                .mapToObj(attemptNumber -> {
                    String result;
                    var timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_kk-mm-ss"));
                    if (attemptNumber > 0) {
                        result = "backup_" + timestamp + "_" + String.format("%03d", attemptNumber) + ".zip";
                    } else {
                        result = "backup_" + timestamp + ".zip";
                    }
                    return new File(backupCommons.getBackupsDirectory(), result);
                })
                .filter(Predicate.not(File::exists))
                .findFirst().orElseThrow(() -> new NodeDocUserException("Unable to generate ZIP file name in the backups directory"));
    }

    public List<File> listBackups() {
        return List.of(Objects.requireNonNull(backupCommons.getBackupsDirectory().listFiles()));
    }

    public void deleteBackup(String fileName) {
        var backupFile = new File(backupCommons.getBackupsDirectory(), fileName);
        if (!backupFile.delete()) {
            log.error("Unable to delete backup {} at file {}", fileName, backupFile);
            throw new NodeDocUserException("Unable to delete backup " + fileName);
        }
        log.info("Deleted backup at {}", backupFile);
    }



}
