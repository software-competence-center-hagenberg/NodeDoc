package at.scch.nodedoc.documentation.diff.table;

import at.scch.nodedoc.diffengine.DiffContext;
import at.scch.nodedoc.documentation.diff.DisplayDifferenceType;
import at.scch.nodedoc.documentation.displaymodel.table.DisplayTable;
import at.scch.nodedoc.documentation.displaymodel.table.TypeSubTypeTableSection;
import at.scch.nodedoc.documentation.template.StringTemplate;
import at.scch.nodedoc.nodeset.NodeId;
import at.scch.nodedoc.nodeset.NodeSetUniverse;
import at.scch.nodedoc.nodeset.UANode;
import at.scch.nodedoc.nodeset.UAType;
import at.scch.nodedoc.uaStandard.Nodes;
import com.fizzed.rocker.RockerContent;
import com.fizzed.rocker.runtime.DefaultRockerModel;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DiffSubTypeTableSection<Node extends UAType> implements TypeSubTypeTableSection {

    protected final DiffContext.DiffView<Node> diffView;

    @Getter
    private final List<DiffTableRow> subtypeReferenceRows;

    private final boolean showAllRowsAsUnchanged;

    public DiffSubTypeTableSection(DiffContext.DiffView<Node> diffView, boolean showAllRowsAsUnchanged) {
        this.diffView = diffView;
        this.showAllRowsAsUnchanged = showAllRowsAsUnchanged;
        this.subtypeReferenceRows = Stream.concat(
                        diffView.getDiffContext().getDiffReferencesToNode(diffView.getKeyProperty(UANode::getNodeId)).stream()
                                .filter(ref -> ref.getValue().getProperty(NodeSetUniverse.Reference::getReferenceType).getKeyProperty(UANode::getNodeId).equals(Nodes.ReferenceTypes.HAS_SUBTYPE))
                                .sorted(Comparator.comparing(entry -> entry.getValue().getProperty(NodeSetUniverse.Reference::getSource).getProperty(UANode::getBrowseName).getBaseOrElseCompareValue()))
                                .map(entry -> mapEntryToRow(entry, NodeSetUniverse.Reference::getSource, true)),

                        diffView.getDiffContext().getDiffReferencesFromNode(diffView.getKeyProperty(UANode::getNodeId)).stream()
                                .filter(ref -> ref.getValue().getProperty(NodeSetUniverse.Reference::getReferenceType).getKeyProperty(UANode::getNodeId).equals(Nodes.ReferenceTypes.HAS_SUBTYPE))
                                .sorted(Comparator.comparing(entry -> entry.getValue().getProperty(NodeSetUniverse.Reference::getTarget).getProperty(UANode::getBrowseName).getBaseOrElseCompareValue()))
                                .map(entry -> mapEntryToRow(entry, NodeSetUniverse.Reference::getTarget, false))
                )
                .collect(Collectors.toList());
    }

    @Override
    public DefaultRockerModel getRowTemplate(Object row, RockerContent content) {
        return DiffDisplayTableRowTemplate.template((DiffTableRow) row, content);
    }

    @Override
    public Function<Object, DefaultRockerModel> getCellValueTemplateFunction() {
        return (cellValue) -> DiffDisplayTableCellValueTemplate.template((DiffContext.DiffView<?>) cellValue, StringTemplate::template);
    }

    public DiffTableRow buildRow(DisplayDifferenceType rowDiffType, List<? extends DisplayTable.Cell<?>> cells) {
        return new DiffTableRow(rowDiffType, cells);
    }

    // TODO: check for better solutions
    private DiffTableRow mapEntryToRow(DiffContext.DiffCollectionEntry<NodeSetUniverse.Reference> referenceDiffCollectionEntry, Function<NodeSetUniverse.Reference, UANode> nodeViewExtractor, boolean isForward) {
        var rowDiffType = showAllRowsAsUnchanged
                ? DisplayDifferenceType.UNCHANGED
                : DisplayDifferenceType.of(referenceDiffCollectionEntry.getEntryDiffType());
        var referencedNode = referenceDiffCollectionEntry.getValue().getProperty(nodeViewExtractor);
        return buildRow(
                rowDiffType,
                List.of(
                        new DiffTableCell<>(referencedNode.getProperty(UANode::getBrowseName), DisplayDifferenceType.UNCHANGED),
                        new DiffTableCell<>(diffView.getDiffContext().diffViewOf(isForward), DisplayDifferenceType.UNCHANGED),
                        new DiffTableCell<>(referencedNode.getProperty(UANode::getNodeId).getProperty(NodeId::getNamespaceUri), DisplayDifferenceType.UNCHANGED)
                ));
    }

    public DiffContext.ValueDiffType getDiffType() {
        return subtypeReferenceRows.stream()
                .map(DiffTableRow::getDisplayDifferenceType)
                .allMatch(displayDifferenceType -> displayDifferenceType == DisplayDifferenceType.UNCHANGED)
                ? DiffContext.ValueDiffType.UNCHANGED
                : DiffContext.ValueDiffType.CHANGED;
    }
}
