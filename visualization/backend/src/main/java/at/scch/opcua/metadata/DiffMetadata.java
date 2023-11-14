package at.scch.opcua.metadata;

import java.time.LocalDateTime;

public class DiffMetadata {

    private String baseNodeset;
    private String compareNodeset;
    private LocalDateTime generated;

    public DiffMetadata(String baseNodeset, String compareNodeset, LocalDateTime generated) {
        this.baseNodeset = baseNodeset;
        this.compareNodeset = compareNodeset;
        this.generated = generated;
    }

    public String getBaseNodeset() {
        return baseNodeset;
    }

    public void setBaseNodeset(String baseNodeset) {
        this.baseNodeset = baseNodeset;
    }

    public String getCompareNodeset() {
        return compareNodeset;
    }

    public void setCompareNodeset(String compareNodeset) {
        this.compareNodeset = compareNodeset;
    }

    public LocalDateTime getGenerated() {
        return generated;
    }

    public void setGenerated(LocalDateTime generated) {
        this.generated = generated;
    }
}
