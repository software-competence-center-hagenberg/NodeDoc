package at.scch.nodedoc.documentation.single.table;

import at.scch.nodedoc.documentation.displaymodel.table.TypeSubTypeTableSection;
import at.scch.nodedoc.documentation.template.StringTemplate;
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

public class SingleSubTypeTableSection<Node extends UAType> implements TypeSubTypeTableSection {

    protected final Node node;

    @Getter
    private final List<SingleTableRow> subtypeReferenceRows;

    public SingleSubTypeTableSection(Node node) {
        this.node = node;
        this.subtypeReferenceRows = Stream.concat(
                        node.getBackwardReferencedNodes(Nodes.ReferenceTypes.HAS_SUBTYPE).stream()
                                        .sorted(Comparator.comparing(UANode::getBrowseName))
                                        .map(n -> mapNodeToRow(n, true)),
                        node.getForwardReferencedNodes(Nodes.ReferenceTypes.HAS_SUBTYPE).stream()
                                .sorted(Comparator.comparing(UANode::getBrowseName))
                                .map(n -> mapNodeToRow(n, false))
                )
                .collect(Collectors.toList());
    }

    @Override
    public DefaultRockerModel getRowTemplate(Object row, RockerContent content) {
        return SingleDisplayTableRowTemplate.template((SingleTableRow) row, content);
    }

    @Override
    public Function<Object, DefaultRockerModel> getCellValueTemplateFunction() {
        return (cellValue) -> SingleDisplayTableCellValueTemplate.template((String) cellValue, StringTemplate::template);
    }

    private SingleTableRow mapNodeToRow(UANode node, boolean isForward) {
        return new SingleTableRow(List.of(
                new SingleTableCell<>(node.getBrowseName()),
                new SingleTableCell<>(Boolean.toString(isForward)),
                new SingleTableCell<>(node.getNodeId().getNamespaceUri())
        ));
    }

}
