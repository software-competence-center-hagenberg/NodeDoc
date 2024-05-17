package at.scch.opcua.service;

import at.scch.opcua.dto.FilestructureDirectoryNode;
import at.scch.opcua.dto.FilestructureFileNode;
import at.scch.opcua.dto.FilestructureNode;
import at.scch.opcua.util.PathUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class FileStructureService {

    private final ThreadLocal<Integer> currentId = new ThreadLocal<>();

    private void resetId() {
        currentId.set(0);
    }

    private int nextId() {
        var nextId = currentId.get() + 1;
        currentId.set(nextId);
        return nextId;
    }

    /**
     * Builds a tree structure of the nodeset directory.
     * The structure does not contain empty directories, the "versions" directory is hidden and any directories
     * containing only one subdirectory are flattened
     *
     * @param rootDirectory     root directory (directory containing all nodesets)
     * @return                  tree structure of the nodeset directory
     */
    public FilestructureDirectoryNode getFileStructure(File rootDirectory) {
        log.info("Load file structure starting at {}", rootDirectory);
        resetId();
        var structure = buildDirectoryStructure(rootDirectory, rootDirectory);
        removeEmptyDirectories(structure);
        removeVersionsDirectory(structure);
        flattenStructure(structure);
        sortSubdirectoriesByName(structure);
        return structure;
    }

    private FilestructureDirectoryNode buildDirectoryStructure(File rootDirectory, File directory) {
        var filesInCurrentDirectory = directory.listFiles();

        var nodesetFile = Arrays.stream(filesInCurrentDirectory)
                .filter(file -> file.isFile() && file.getName().endsWith(".xml"))
                .findFirst();

        var documentationFile = Arrays.stream(filesInCurrentDirectory)
                .filter(file -> file.isFile() && file.getName().endsWith(".html"))
                .findFirst();

        var fullPathOfCurrentDirectory = PathUtils.getRelativePath(rootDirectory, directory).toString();
        var directoryNode = new FilestructureDirectoryNode(nextId(), directory.getName(), fullPathOfCurrentDirectory);

        // Traverse subdirectories
        var subdirectories = Arrays.stream(filesInCurrentDirectory)
                .filter(File::isDirectory)
                .map(file -> buildDirectoryStructure(rootDirectory, file)).collect(Collectors.<FilestructureNode>toList());
        directoryNode.setChildren(subdirectories);

        if (nodesetFile.isPresent() || documentationFile.isPresent()) {
            // Found HTML or XML => Leaf node
            var nodeSetFileName = nodesetFile.orElseGet(documentationFile::get).getName();
            nodeSetFileName = nodeSetFileName.substring(0, nodeSetFileName.lastIndexOf(".")); // remove extension

            var nodesetPath = nodesetFile
                    .map(file -> PathUtils.getRelativePath(rootDirectory, file))
                    .map(Path::toString)
                    .orElse(null);
            var documentationPath = documentationFile
                    .map(file -> PathUtils.getRelativePath(rootDirectory, file))
                    .map(Path::toString)
                    .orElse(null);

            var fileNode = new FilestructureFileNode(nextId(), nodeSetFileName, nodesetPath, documentationPath);
            directoryNode.getChildren().add(fileNode);
        }

        return directoryNode;
    }

    private void removeEmptyDirectories(FilestructureDirectoryNode directoryNode) {
        var newChildren = directoryNode.getChildren().stream()
                .peek(childNode -> {
                    if (childNode instanceof FilestructureDirectoryNode) {
                        removeEmptyDirectories((FilestructureDirectoryNode) childNode);
                    }
                })
                .filter(childNode -> childNode instanceof FilestructureFileNode
                        || !((FilestructureDirectoryNode) childNode).getChildren().isEmpty())
                .collect(Collectors.toList());
        directoryNode.setChildren(newChildren);
    }

    private void removeVersionsDirectory(FilestructureDirectoryNode directoryNode) {
        var versionsDirectoryNode = getSubdirectories(directoryNode)
                .filter(node -> node.getName().equals("versions"))
                .findFirst().orElse(null);

        if (versionsDirectoryNode != null) {
            // Check if this directory contains only nodeset nodes three levels below
            var versionsDirectoryCanBeDeleted = getSubdirectories(versionsDirectoryNode)
                    .flatMap(this::getSubdirectories)
                    .flatMap(this::getSubdirectories)
                    .flatMap(this::getSubdirectories)
                    .flatMap(directory -> directory.getChildren().stream())
                    .allMatch(node -> node instanceof FilestructureFileNode);

            if (versionsDirectoryCanBeDeleted) {
                directoryNode.getChildren().remove(versionsDirectoryNode);
                directoryNode.getChildren().addAll(versionsDirectoryNode.getChildren());
            }
        } else {
            getSubdirectories(directoryNode)
                    .forEach(this::removeVersionsDirectory);
        }
    }

    private Stream<FilestructureDirectoryNode> getSubdirectories(FilestructureDirectoryNode directoryNode) {
        return directoryNode.getChildren().stream()
                .filter(node -> node instanceof FilestructureDirectoryNode)
                .map(node -> (FilestructureDirectoryNode)node);
    }

    private void flattenStructure(FilestructureDirectoryNode rootNode) {
        var newChildren = rootNode.getChildren().stream()
                .map(child -> flattenDirectoryNode((FilestructureDirectoryNode) child))
                .collect(Collectors.toList());
        rootNode.setChildren(newChildren);
    }

    private FilestructureNode flattenDirectoryNode(FilestructureDirectoryNode directoryNode) {
        var newChildren = directoryNode.getChildren().stream()
                .map(node -> {
                    if (node instanceof FilestructureDirectoryNode) {
                        return flattenDirectoryNode((FilestructureDirectoryNode) node);
                    } else {
                        return node;
                    }
                })
                .collect(Collectors.toList());

        if (newChildren.size() == 1) {
            // directory contains exactly one child
            var newDirectoryNode = newChildren.get(0);
            if (newDirectoryNode instanceof FilestructureDirectoryNode) {
                // if child is a directory, flatten it
                newDirectoryNode.setName(directoryNode.getName() + "/" + newDirectoryNode.getName());
                return newDirectoryNode;
            } else {
                // if child is a file, don't flatten it, keep the directory above the file
                return directoryNode;
            }
        } else {
            directoryNode.setChildren(newChildren);
            return directoryNode;
        }
    }

    private void sortSubdirectoriesByName(FilestructureDirectoryNode directoryNode) {
        directoryNode.getChildren().sort(Comparator.comparing(FilestructureNode::getName));
        getSubdirectories(directoryNode).forEach(this::sortSubdirectoriesByName);
    }
}
