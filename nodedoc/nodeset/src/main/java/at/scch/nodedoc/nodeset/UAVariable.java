package at.scch.nodedoc.nodeset;

import java.util.List;

public interface UAVariable extends UAInstance {

    UADataType getDataType();
    List<Argument> getArguments();
    int getValueRank();
    int getAccessLevel();
    int getUserAccessLevel();
    double getMinimumSamplingInterval();
    boolean isHistorizing();
    String getArrayDimensions();

    interface Argument {

        UADataType getDataType();
        String getName();
        int getValueRank();
        String getArrayDimension();
        String getDescription();
        void setDescription(String description);
    }
}
