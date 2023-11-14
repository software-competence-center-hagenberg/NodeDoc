package at.scch.nodedoc.documentation.diff;

import at.scch.nodedoc.diffengine.DiffContext;
import at.scch.nodedoc.documentation.displaymodel.DisplaySection;
import at.scch.nodedoc.documentation.displaymodel.DisplayType;
import at.scch.nodedoc.documentation.displaymodel.GraphFigure;
import at.scch.nodedoc.documentation.displaymodel.table.TypeDefinitionTable;
import at.scch.nodedoc.nodeset.NodeId;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class DiffDisplayType<TypeDefTable extends TypeDefinitionTable<?>> extends DiffDisplayNode implements DisplayType<TypeDefTable> {

    private final GraphFigure graphFigure;
    private final TypeDefTable definitionTable;

    public DiffDisplayType(NodeId<?> nodeId, DiffContext.DiffView<String> headingText, DisplayDifferenceType displayDifferenceType, String anchorValue, DiffContext.DiffView<String> documentationTextValue, DiffContext.DiffView<String> descriptionTextValue, GraphFigure graphFigure, TypeDefTable definitionTable, List<? extends DisplaySection> subSections) {
        super(nodeId, headingText, displayDifferenceType, anchorValue, documentationTextValue, descriptionTextValue, subSections);
        this.definitionTable = definitionTable;
        this.graphFigure = graphFigure;
    }
}
