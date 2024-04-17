package at.scch.opcua.controller;

import at.scch.opcua.demomode.RestrictInDemoMode;
import at.scch.opcua.service.BackupService;
import at.scch.opcua.service.RestoreService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class BackupController {

    @Autowired
    private BackupService backupService;

    @Autowired
    private RestoreService restoreService;

    @RestrictInDemoMode
    @ApiOperation("Creates a backup")
    @PostMapping("/createBackup")
    public void createBackup(HttpServletResponse response, @RequestParam(name = "keepOnServer", defaultValue = "false") boolean keepOnServer) {
        var zipFile = backupService.createNewBackupZIP();
        response.setHeader("Content-Disposition", "attachment; filename=\"" + zipFile.getName() + "\"");
        sendFile(response, zipFile);
        if (!keepOnServer) {
            backupService.deleteBackup(zipFile.getName());
        }
    }

    private void sendFile(HttpServletResponse response, File zipFile) {
        try (var out = response.getOutputStream(); FileInputStream in = new FileInputStream(zipFile)) {
            IOUtils.copy(in, out);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write file " + zipFile.getName() + " to HttpServletResponse", e);
        }
    }

    @ApiOperation("Lists backups on the server")
    @GetMapping("/backups")
    public List<String> listBackups() {
        return backupService.listBackups().stream()
                .map(File::getName)
                .sorted()
                .collect(Collectors.toList());
    }

    @RestrictInDemoMode
    @ApiOperation("Deletes a given backup file from the server")
    @DeleteMapping("/backups/{fileName}")
    public void deleteBackup(@PathVariable String fileName) {
        backupService.deleteBackup(fileName);
    }

    @RestrictInDemoMode
    @ApiOperation("Restores from an uploaded backup")
    @PostMapping("/restoreBackup")
    public void uploadAndRestoreBackup(
            @RequestParam(name = "zipFile") MultipartFile zipFile,
            @RequestParam(name = "overrideZip", defaultValue = "false") boolean overrideZip,
            @RequestParam(name = "ignoreVersionMismatch", defaultValue = "false") boolean ignoreVersionMismatch,
            @RequestParam(name = "keepZipOnServer", defaultValue = "false") boolean keepZipOnServer) {

        var zipFileName = zipFile.getOriginalFilename();
        restoreService.importZipFile(zipFileName, overrideZip, destination -> transferFileToDestination(zipFile, destination));
        try {
            restoreService.restoreFromBackup(zipFileName, ignoreVersionMismatch);
        } finally {
            if (!keepZipOnServer) {
                backupService.deleteBackup(zipFileName);
            }
        }
    }

    private static void transferFileToDestination(MultipartFile zipFile, File destination) {
        try {
            zipFile.transferTo(destination);
        } catch (IOException e) {
            throw new RuntimeException("Unable to save ZIP file at " + destination, e);
        }
    }

    @RestrictInDemoMode
    @ApiOperation("Restores from a saved backup")
    @PostMapping("/restoreBackupFromServer")
    public void restoreBackup(
            @RequestParam(name = "zipFile") String zipFile,
            @RequestParam(name = "ignoreVersionMismatch", defaultValue = "false") boolean ignoreVersionMismatch) {
        restoreService.restoreFromBackup(zipFile, ignoreVersionMismatch);
    }
}
