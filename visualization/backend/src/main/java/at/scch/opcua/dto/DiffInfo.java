package at.scch.opcua.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class DiffInfo {

    private String base;
    private String compare;
    private String diffPath;
    private LocalDateTime generated;

    public DiffInfo(String base, String compare, String diffPath, LocalDateTime generated) {
        this.base = base;
        this.compare = compare;
        this.diffPath = diffPath;
        this.generated = generated;
    }

    @JsonProperty("base")
    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    @JsonProperty("compare")
    public String getCompare() {
        return compare;
    }

    public void setCompare(String compare) {
        this.compare = compare;
    }

    @JsonProperty("diffPath")
    public String getDiffPath() {
        return diffPath;
    }

    public void setDiffPath(String diffPath) {
        this.diffPath = diffPath;
    }

    @JsonProperty("generated")
    public LocalDateTime getGenerated() {
        return generated;
    }

    public void setGenerated(LocalDateTime generated) {
        this.generated = generated;
    }
}
