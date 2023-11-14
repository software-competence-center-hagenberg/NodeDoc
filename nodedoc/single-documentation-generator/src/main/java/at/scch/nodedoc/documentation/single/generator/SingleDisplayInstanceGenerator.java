package at.scch.nodedoc.documentation.single.generator;

import at.scch.nodedoc.documentation.single.SingleDisplayInstance;
import at.scch.nodedoc.documentation.single.SingleDisplayVariable;
import at.scch.nodedoc.html.Utilities;
import at.scch.nodedoc.nodeset.*;

import java.util.List;

public class SingleDisplayInstanceGenerator {

    private final SingleDisplayMethodGenerator singleDisplayMethodGenerator;
    private final DocEntryEditorGenerator docEntryEditorGenerator;

    public SingleDisplayInstanceGenerator(SingleDisplayMethodGenerator singleDisplayMethodGenerator, DocEntryEditorGenerator docEntryEditorGenerator) {
        this.singleDisplayMethodGenerator = singleDisplayMethodGenerator;
        this.docEntryEditorGenerator = docEntryEditorGenerator;
    }

    public SingleDisplayInstance generateInstance(NodeId<?> parentNodeId, UAInstance instance) {
        var headingText = instance.getBrowseName();

        var anchorValue = Utilities.getNodeIdAnchorForInstance(parentNodeId, instance.getNodeId());

        var typeDefinitionBrowseName = instance.getTypeDefinition().map(UANode::getBrowseName).orElse(null);

        var documentationEditor = docEntryEditorGenerator.getDivForNodeDocumentation(instance, 4);
        var descriptionEditor = docEntryEditorGenerator.getDivForNodeDescription(instance, 4);

        if (instance instanceof UAMethod) {
            return singleDisplayMethodGenerator.generateMethod(headingText, anchorValue, typeDefinitionBrowseName, ((UAMethod) instance), documentationEditor, descriptionEditor);
        } else if (instance instanceof UAVariable) {
            var variable = ((UAVariable) instance);
            var dataTypeBrowseName = variable.getDataType().getBrowseName();
            return new SingleDisplayVariable(instance.getNodeId(), headingText, anchorValue, documentationEditor, descriptionEditor, typeDefinitionBrowseName, dataTypeBrowseName, List.of());
        } else {
            return new SingleDisplayInstance(instance.getNodeId(), headingText, anchorValue, documentationEditor, descriptionEditor, typeDefinitionBrowseName, List.of());
        }
    }
}
