package at.scch.nodedoc.documentation.single;

import at.scch.nodedoc.documentation.displaymodel.DisplaySection;
import at.scch.nodedoc.documentation.displaymodel.DisplayVariableType;
import at.scch.nodedoc.documentation.displaymodel.GraphFigure;
import at.scch.nodedoc.documentation.displaymodel.table.VariableTypeDefinitionTable;
import at.scch.nodedoc.nodeset.NodeId;
import org.jsoup.nodes.Element;

import java.util.List;

public class SingleDisplayVariableType extends SingleDisplayType<VariableTypeDefinitionTable> implements DisplayVariableType {
    public SingleDisplayVariableType(NodeId<?> nodeId, String headingText, String anchorValue, Element documentationEditor, Element descriptionEditor, GraphFigure graphFigure, VariableTypeDefinitionTable definitionTable, List<? extends DisplaySection> subSections) {
        super(nodeId, headingText, anchorValue, documentationEditor, descriptionEditor, graphFigure, definitionTable, subSections);
    }
}
