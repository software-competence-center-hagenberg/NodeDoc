package at.scch.nodedoc.documentation.diff;

import at.scch.nodedoc.diffengine.DiffContext;
import at.scch.nodedoc.documentation.displaymodel.DisplaySection;
import at.scch.nodedoc.documentation.displaymodel.GenericDisplayType;
import at.scch.nodedoc.documentation.displaymodel.GraphFigure;
import at.scch.nodedoc.documentation.displaymodel.table.GenericTypeDefinitionTable;
import at.scch.nodedoc.nodeset.NodeId;

import java.util.List;

public class GenericDiffDisplayType extends DiffDisplayType<GenericTypeDefinitionTable> implements GenericDisplayType {

    public GenericDiffDisplayType(NodeId<?> nodeId, DiffContext.DiffView<String> headingText, DisplayDifferenceType displayDifferenceType, String anchorValue, DiffContext.DiffView<String> documentationTextValue, DiffContext.DiffView<String> descriptionTextValue, GraphFigure graphFigure, GenericTypeDefinitionTable definitionTable, List<? extends DisplaySection> subSections) {
        super(nodeId, headingText, displayDifferenceType, anchorValue, documentationTextValue, descriptionTextValue, graphFigure, definitionTable, subSections);
    }
}
