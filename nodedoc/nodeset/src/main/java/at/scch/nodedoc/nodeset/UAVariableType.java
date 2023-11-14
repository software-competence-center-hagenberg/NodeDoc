package at.scch.nodedoc.nodeset;

public interface UAVariableType extends UAType {

    UADataType getDataType();
    String getArrayDimensions();
    int getValueRank();
}
