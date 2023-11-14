package at.scch.nodedoc.documentation.single;

import at.scch.nodedoc.documentation.displaymodel.DisplaySection;
import at.scch.nodedoc.documentation.displaymodel.GenericDisplayType;
import at.scch.nodedoc.documentation.displaymodel.GraphFigure;
import at.scch.nodedoc.documentation.displaymodel.table.GenericTypeDefinitionTable;
import at.scch.nodedoc.nodeset.NodeId;
import org.jsoup.nodes.Element;

import java.util.List;

public class GenericSingleDisplayType extends SingleDisplayType<GenericTypeDefinitionTable> implements GenericDisplayType {

    public GenericSingleDisplayType(NodeId<?> nodeId, String headingText, String anchorValue, Element documentationEditor, Element descriptionEditor, GraphFigure graphFigure, GenericTypeDefinitionTable definitionTable, List<? extends DisplaySection> subSections) {
        super(nodeId, headingText, anchorValue, documentationEditor, descriptionEditor, graphFigure, definitionTable, subSections);
    }
}
