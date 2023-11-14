package at.scch.nodedoc.documentation.single;

import at.scch.nodedoc.documentation.displaymodel.DisplaySection;
import at.scch.nodedoc.documentation.displaymodel.DisplayType;
import at.scch.nodedoc.documentation.displaymodel.GraphFigure;
import at.scch.nodedoc.documentation.displaymodel.table.TypeDefinitionTable;
import at.scch.nodedoc.nodeset.NodeId;
import lombok.Getter;
import org.jsoup.nodes.Element;

import java.util.List;

@Getter
public abstract class SingleDisplayType<TypeDefTable extends TypeDefinitionTable<?>> extends SingleDisplayNode implements DisplayType<TypeDefTable> {

    private final GraphFigure graphFigure;
    private final TypeDefTable definitionTable;

    public SingleDisplayType(NodeId<?> nodeId, String headingText, String anchorValue, Element documentationEditor, Element descriptionEditor, GraphFigure graphFigure, TypeDefTable definitionTable, List<? extends DisplaySection> subSections) {
        super(nodeId, headingText, anchorValue, documentationEditor, descriptionEditor, subSections);
        this.definitionTable = definitionTable;
        this.graphFigure = graphFigure;
    }
}
