package at.scch.nodedoc.documentation.diff.generator;

import at.scch.nodedoc.diffengine.DiffContext;
import at.scch.nodedoc.documentation.diff.DiffDisplayInstance;
import at.scch.nodedoc.documentation.diff.DiffDisplayVariable;
import at.scch.nodedoc.documentation.diff.DisplayDifferenceType;
import at.scch.nodedoc.html.Utilities;
import at.scch.nodedoc.nodeset.*;
import at.scch.nodedoc.util.UAModelUtils;

import java.util.List;

public class DiffDisplayInstanceGenerator {

    private final DiffDisplayMethodGenerator diffDisplayMethodGenerator;

    public DiffDisplayInstanceGenerator(DiffDisplayMethodGenerator diffDisplayMethodGenerator) {
        this.diffDisplayMethodGenerator = diffDisplayMethodGenerator;
    }

    public DiffDisplayInstance generateInstance(NodeId<?> parentNodeId, DiffContext.EntryDiffType entryDiffType, DiffContext.DiffView<? extends UAInstance> instanceEntry, RenderMode mode) {
        var headingText = mode.apply(instanceEntry.getProperty(UANode::getBrowseName));

        var anchorValue = Utilities.getNodeIdAnchorForInstance(parentNodeId, instanceEntry.getKeyProperty(UANode::getNodeId));

        var displayDifferenceType = DisplayDifferenceType.of(entryDiffType);
        var typeDefinitionBrowseName = mode.apply(instanceEntry.navigate(UAModelUtils::getTypeDefinition).getMergedProperty(UANode::getBrowseName));

        var documentationTextValue = mode.apply(instanceEntry.getProperty(UANode::getDocumentation).replaceNull(""));
        var descriptionTextValue = mode.apply(instanceEntry.getProperty(UANode::getDescription).replaceNull(""));

        var instanceInnerDiffType = DiffContext.ValueDiffType.getCombinedDiffType(
                headingText.getDiffType(),
                typeDefinitionBrowseName.getDiffType(),
                documentationTextValue.getDiffType(),
                descriptionTextValue.getDiffType()
        );

        if (instanceEntry.getBaseOrElseCompareValue() instanceof UAMethod) {
            return diffDisplayMethodGenerator.generateMethod(headingText, instanceInnerDiffType, entryDiffType, anchorValue, documentationTextValue, descriptionTextValue, typeDefinitionBrowseName, instanceEntry.anyCast(UAMethod.class).get(), mode);
        } else if (instanceEntry.getBaseOrElseCompareValue() instanceof UAVariable) {
            var variableEntry = instanceEntry.anyCast(UAVariable.class).get();
            var dataTypeBrowseName = mode.apply(variableEntry.navigate(UAVariable::getDataType).getMergedProperty(UANode::getBrowseName));
            displayDifferenceType = includeInnerDiffType(displayDifferenceType, instanceInnerDiffType);
            return new DiffDisplayVariable(instanceEntry.getKeyProperty(UANode::getNodeId), headingText, displayDifferenceType, anchorValue, documentationTextValue, descriptionTextValue, typeDefinitionBrowseName, dataTypeBrowseName, List.of());
        } else {
            displayDifferenceType = includeInnerDiffType(displayDifferenceType, instanceInnerDiffType);
            return new DiffDisplayInstance(instanceEntry.getKeyProperty(UANode::getNodeId), headingText, displayDifferenceType, anchorValue, documentationTextValue, descriptionTextValue, typeDefinitionBrowseName, List.of());
        }
    }

    private static DisplayDifferenceType includeInnerDiffType(DisplayDifferenceType displayDifferenceType, DiffContext.ValueDiffType instanceInnerDiffType) {
        if (displayDifferenceType == DisplayDifferenceType.UNCHANGED && instanceInnerDiffType == DiffContext.ValueDiffType.CHANGED) {
            displayDifferenceType = DisplayDifferenceType.CHANGED;
        }
        return displayDifferenceType;
    }
}
