package at.scch.nodedoc.nodeset;

public interface DefinitionField {

    String getDescription();
    void setDescription(String description);
    String getName();
    String getSymbolicName();

    UADataType getDataType();

    int getValueRank();
    int getValue();
    boolean isOptional();
}
