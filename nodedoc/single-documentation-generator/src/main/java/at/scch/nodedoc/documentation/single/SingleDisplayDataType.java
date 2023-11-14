package at.scch.nodedoc.documentation.single;

import at.scch.nodedoc.documentation.displaymodel.DisplayDataType;
import at.scch.nodedoc.documentation.displaymodel.DisplaySection;
import at.scch.nodedoc.documentation.displaymodel.GraphFigure;
import at.scch.nodedoc.documentation.displaymodel.table.DataTypeDefinitionTable;
import at.scch.nodedoc.nodeset.NodeId;
import org.jsoup.nodes.Element;

import java.util.List;

public class SingleDisplayDataType extends SingleDisplayType<DataTypeDefinitionTable> implements DisplayDataType {
    public SingleDisplayDataType(NodeId<?> nodeId, String headingText, String anchorValue, Element documentationEditor, Element descriptionEditor, GraphFigure graphFigure, DataTypeDefinitionTable definitionTable, List<? extends DisplaySection> subSections) {
        super(nodeId, headingText, anchorValue, documentationEditor, descriptionEditor, graphFigure, definitionTable, subSections);
    }
}
