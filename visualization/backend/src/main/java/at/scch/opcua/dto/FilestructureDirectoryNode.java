package at.scch.opcua.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class FilestructureDirectoryNode extends FilestructureNode {

    private String fullPath;
    private List<FilestructureNode> children;

    public FilestructureDirectoryNode(int id, String name, String fullPath, List<FilestructureNode> children) {
        super(id, name);
        this.fullPath = fullPath;
        this.children = children;
    }

    public FilestructureDirectoryNode(int id, String name, String fullPath) {
        this(id, name, fullPath, new ArrayList<>());
    }

    @JsonProperty("path")
    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    @JsonProperty("children")
    public List<FilestructureNode> getChildren() {
        return children;
    }

    public void setChildren(List<FilestructureNode> children) {
        this.children = children;
    }
}
