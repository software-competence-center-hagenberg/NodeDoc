package at.scch.nodedoc.documentation.diff.generator;

import at.scch.nodedoc.diffengine.DiffContext;
import at.scch.nodedoc.documentation.diff.DiffDisplayMethod;
import at.scch.nodedoc.documentation.diff.DisplayDifferenceType;
import at.scch.nodedoc.documentation.diff.table.DiffMethodArgumentTableSection;
import at.scch.nodedoc.documentation.diff.table.DiffTableRow;
import at.scch.nodedoc.documentation.diff.table.DiffTableUtils;
import at.scch.nodedoc.documentation.displaymodel.DisplayMethod;
import at.scch.nodedoc.documentation.displaymodel.table.MethodArgumentsTable;
import at.scch.nodedoc.nodeset.UAMethod;
import at.scch.nodedoc.nodeset.UANode;
import at.scch.nodedoc.nodeset.UAVariable;
import at.scch.nodedoc.uaStandard.BrowseNames;
import at.scch.nodedoc.uaStandard.Nodes;
import at.scch.nodedoc.util.StreamUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DiffDisplayMethodGenerator {

    public DiffDisplayMethod generateMethod(DiffContext.DiffView<String> headingText, DiffContext.ValueDiffType instanceInnerDiffType, DiffContext.EntryDiffType entryDiffType, String anchorValue, DiffContext.DiffView<String> documentationTextValue, DiffContext.DiffView<String> descriptionTextValue, DiffContext.DiffView<String> typeDefinitionBrowseName, DiffContext.DiffView<? extends UAMethod> methodEntry, RenderMode mode) {
        var inputArguments = generateArgumentList(methodEntry, BrowseNames.ArgumentNames.INPUT_ARGUMENT, DisplayMethod.DisplayArgument.Direction.IN, mode);
        var outputArguments = generateArgumentList(methodEntry, BrowseNames.ArgumentNames.OUTPUT_ARGUMENT, DisplayMethod.DisplayArgument.Direction.OUT, mode);
        var arguments = Stream.concat(inputArguments.stream(), outputArguments.stream()).collect(Collectors.toList());

        var methodArgumentsSection = generateArgumentTableSection(arguments, mode);

        var argChangeType = arguments.stream()
                .allMatch(arg -> arg.getDiffType().equals(DisplayDifferenceType.UNCHANGED))
                ? DiffContext.ValueDiffType.UNCHANGED : DiffContext.ValueDiffType.CHANGED;

        var innerDiffType = DiffContext.ValueDiffType.getCombinedDiffType(
                instanceInnerDiffType,
                argChangeType,
                methodArgumentsSection.getDiffType()
        );

        var displayDifferenceType = DisplayDifferenceType.of(entryDiffType);
        if (displayDifferenceType == DisplayDifferenceType.UNCHANGED && innerDiffType == DiffContext.ValueDiffType.CHANGED) {
            displayDifferenceType = DisplayDifferenceType.CHANGED;
        }

        var methodArgumentsTable = new MethodArgumentsTable(DiffDisplayUtils.toRenderable(headingText), methodArgumentsSection);
        return new DiffDisplayMethod(methodEntry.getKeyProperty(UANode::getNodeId), headingText, displayDifferenceType, anchorValue, documentationTextValue, descriptionTextValue, headingText, arguments, typeDefinitionBrowseName, methodArgumentsTable, List.of());
    }

    private static List<UAVariable.Argument> getArgumentsFromMethod(UAMethod method, String argumentBrowseName) {
        return method.getForwardReferencedNodes(Nodes.ReferenceTypes.HAS_PROPERTY).stream()
                .flatMap(StreamUtils.filterCast(UAVariable.class))
                .filter(variable -> variable.getBrowseName().equals(argumentBrowseName))
                .flatMap(variable -> variable.getArguments().stream())
                .collect(Collectors.toList());
    }

    private List<DiffDisplayMethod.DiffDisplayArgument> generateArgumentList(DiffContext.DiffView<? extends UAMethod> methodEntry, String argumentBrowseName, DisplayMethod.DisplayArgument.Direction direction, RenderMode mode) {
        var argumentDiff = methodEntry.getDiffListWithValues(method -> getArgumentsFromMethod(method, argumentBrowseName), UAVariable.Argument::getName, String.class);
        return argumentDiff.stream()
                .filter(argument -> {
                    switch (mode) {
                        case SHOW_CHANGES:
                            return true;
                        case SHOW_AS_UNCHANGED_WITH_BASE_VERSION:
                            return argument.getEntryDiffType() != DiffContext.EntryDiffType.ADDED;
                        case SHOW_AS_UNCHANGED_WITH_COMPARE_VERSION:
                            return argument.getEntryDiffType() != DiffContext.EntryDiffType.REMOVED;
                        default:
                            throw new IllegalStateException("Unexpected value: " + mode);
                    }
                })
                .map(argument -> generateArgument(direction, argument, mode))
                .collect(Collectors.toList());
    }

    private DiffDisplayMethod.DiffDisplayArgument generateArgument(DisplayMethod.DisplayArgument.Direction direction, DiffContext.DiffCollectionEntry<UAVariable.Argument> argumentEntry, RenderMode mode) {
        var argumentType = mode.apply(argumentEntry.getValue().navigate(UAVariable.Argument::getDataType).getMergedProperty(UANode::getBrowseName));
        var argumentName = mode.apply(argumentEntry.getValue().getProperty(UAVariable.Argument::getName));
        var description = mode.apply(argumentEntry.getValue().getProperty(UAVariable.Argument::getDescription));

        var displayDiffType = DisplayDifferenceType.UNCHANGED;
        if (mode == RenderMode.SHOW_CHANGES) {
            displayDiffType = DisplayDifferenceType.of(argumentEntry.getEntryDiffType());
            if (displayDiffType.equals(DisplayDifferenceType.UNCHANGED)) {
                displayDiffType = DisplayDifferenceType.of(DiffContext.ValueDiffType.getCombinedDiffType(
                        argumentType.getDiffType(),
                        argumentName.getDiffType()
                ));
            }
        }

        return new DiffDisplayMethod.DiffDisplayArgument(direction, displayDiffType, argumentType, argumentName, description, argumentEntry.getValue());
    }

    private static DiffMethodArgumentTableSection generateArgumentTableSection(List<DiffDisplayMethod.DiffDisplayArgument> arguments, RenderMode mode) {
        var rows = arguments.stream()
                .map(diffDisplayArgument -> {
                    return new DiffTableRow(DisplayDifferenceType.UNCHANGED, List.of(
                            DiffTableUtils.diffViewToTableCell(mode.apply(diffDisplayArgument.getArgument().getProperty(UAVariable.Argument::getName))),
                            DiffTableUtils.diffViewToTableCell(mode.apply(diffDisplayArgument.getArgument().getProperty(UAVariable.Argument::getDescription)))
                    ));
                }).collect(Collectors.toList());
        return new DiffMethodArgumentTableSection(rows);
    }
}
