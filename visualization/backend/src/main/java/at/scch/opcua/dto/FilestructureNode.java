package at.scch.opcua.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class FilestructureNode {

    private int id;
    private String name;

    public FilestructureNode(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
