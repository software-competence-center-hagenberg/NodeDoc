package at.scch.opcua.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FilestructureFileNode extends FilestructureNode {

    private String nodesetPath;
    private String docuPath;

    public FilestructureFileNode(int id, String name, String nodesetPath, String docuPath) {
        super(id, name);
        this.nodesetPath = nodesetPath;
        this.docuPath = docuPath;
    }

    @JsonProperty("nodesetPath")
    public String getNodesetPath() {
        return nodesetPath;
    }

    public void setNodesetPath(String nodesetPath) {
        this.nodesetPath = nodesetPath;
    }

    @JsonProperty("docuPath")
    public String getDocuPath() {
        return docuPath;
    }

    public void setDocuPath(String docuPath) {
        this.docuPath = docuPath;
    }
}
