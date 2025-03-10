package at.scch.nodedoc.documentation.single.generator;

import at.scch.nodedoc.db.documents.TextId;
import at.scch.nodedoc.documentation.displaymodel.DisplayMethod;
import at.scch.nodedoc.documentation.displaymodel.table.MethodArgumentsTable;
import at.scch.nodedoc.documentation.single.SingleDisplayMethod;
import at.scch.nodedoc.documentation.single.table.SingleMethodArgumentTableSection;
import at.scch.nodedoc.documentation.single.table.SingleTableCell;
import at.scch.nodedoc.documentation.single.table.SingleTableRow;
import at.scch.nodedoc.documentation.template.StringTemplate;
import at.scch.nodedoc.nodeset.UADataType;
import at.scch.nodedoc.nodeset.UAMethod;
import at.scch.nodedoc.nodeset.UANode;
import at.scch.nodedoc.nodeset.UAVariable;
import at.scch.nodedoc.uaStandard.BrowseNames;
import at.scch.nodedoc.util.UAModelUtils;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SingleDisplayMethodGenerator {

    private final DocEntryEditorGenerator docEntryEditorGenerator;

    public SingleDisplayMethodGenerator(DocEntryEditorGenerator docEntryEditorGenerator) {
        this.docEntryEditorGenerator = docEntryEditorGenerator;
    }

    public SingleDisplayMethod generateMethod(String headingText, String anchorValue, String typeDefinitionBrowseName, UAMethod method, Element documentationEditor, Element descriptionEditor) {
        var referencedNodes = method.getForwardReferences().values();
        var inputArguments = generateArgumentList(referencedNodes.stream(), BrowseNames.ArgumentNames.INPUT_ARGUMENT, DisplayMethod.DisplayArgument.Direction.IN, method.getNodeSet().getNamespaceIndexTable());
        var outputArguments = generateArgumentList(referencedNodes.stream(), BrowseNames.ArgumentNames.OUTPUT_ARGUMENT, DisplayMethod.DisplayArgument.Direction.OUT, method.getNodeSet().getNamespaceIndexTable());
        var arguments = Stream.concat(inputArguments.stream(), outputArguments.stream()).collect(Collectors.toList());

        var methodArgumentsSection = generateArgumentTableSection(method, arguments);

        var methodArgumentsTable = new MethodArgumentsTable(x -> StringTemplate.template(headingText), methodArgumentsSection);
        return new SingleDisplayMethod(method.getNodeId(), headingText, anchorValue, documentationEditor, descriptionEditor, headingText, arguments, typeDefinitionBrowseName, methodArgumentsTable, List.of());
    }

    private List<SingleDisplayMethod.SingleDisplayArgument> generateArgumentList(Stream<UANode> referencedNodes, String argumentBrowseName, DisplayMethod.DisplayArgument.Direction direction, List<String> namespaceIndices) {
        return referencedNodes
                .filter(node -> node.getBrowseName().equals(argumentBrowseName))
                .map(node -> (UAVariable) node)
                .flatMap(variable -> variable.getArguments().stream())
                .map(argument -> generateArgument(direction, argument, namespaceIndices))
                .collect(Collectors.toList());
    }

    private SingleDisplayMethod.SingleDisplayArgument generateArgument(DisplayMethod.DisplayArgument.Direction direction, UAVariable.Argument argument, List<String> namespaceIndices) {
        var argumentType = formatArgumentType(argument.getDataType(), namespaceIndices);
        var argumentName = argument.getName();
        var description = argument.getDescription();

        return new SingleDisplayMethod.SingleDisplayArgument(direction, argumentType, argumentName, description, argument);
    }

    private String formatArgumentType(UADataType dataType, List<String> namespaceIndices) {
        var structuredBrowseName = dataType.getStructuredBrowseName();
        var namespaceIndex = structuredBrowseName.computeNamespaceIndex(namespaceIndices);
        if (namespaceIndex > 0) {
            return namespaceIndex + ":" + structuredBrowseName.getBrowseName();
        } else {
            return structuredBrowseName.getBrowseName();
        }
    }

    private SingleMethodArgumentTableSection generateArgumentTableSection(UAMethod method, List<SingleDisplayMethod.SingleDisplayArgument> arguments) {
        var rows = arguments.stream()
                .map(displayArgument -> {
                    var editor = docEntryEditorGenerator.getDivForIdentifier(
                            TextId.forArgument(method.getNodeId(), displayArgument.getArgument()),
                            4
                    );
                    return new SingleTableRow(List.of(
                            new SingleTableCell<>(displayArgument.getArgument().getName()),
                            new SingleTableCell<>(formatArgumentType(displayArgument.getArgument().getDataType(), method.getNodeSet().getNamespaceIndexTable())),
                            new SingleTableCell<>(UAModelUtils.valueRankAsString(displayArgument.getArgument().getValueRank())),
                            new SingleTableCell<>(displayArgument.getArgument().getArrayDimension()),
                            new SingleTableCell<>(editor)
                    ));
                }).collect(Collectors.toList());
        return new SingleMethodArgumentTableSection(rows);
    }
}
