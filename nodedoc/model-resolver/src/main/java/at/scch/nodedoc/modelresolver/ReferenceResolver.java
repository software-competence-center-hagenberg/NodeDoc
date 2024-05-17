package at.scch.nodedoc.modelresolver;

import at.scch.nodedoc.nodeset.*;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ReferenceResolver {

    @Getter
    public static class ResolveContext {
        private final Map<String, UANodeSet> modelsByNamespaceUri;
        private final List<String> namespaceUris;
        private final Map<String, String> aliases;

        public ResolveContext(Map<String, UANodeSet> modelsByNamespaceUri, List<String> namespaceUris, Map<String, String> aliases) {
            this.modelsByNamespaceUri = modelsByNamespaceUri;
            this.namespaceUris = namespaceUris;
            this.aliases = aliases;
        }
    }

    private final NodeIdParser nodeIdParser;

    public ReferenceResolver(NodeIdParser nodeIdParser) {
        this.nodeIdParser = nodeIdParser;
    }

    public Stream<NodeSetUniverse.Reference> resolveReferences(UANodeImpl<?> node, ResolveContext resolveContext) {
        if (node instanceof UAInstanceImpl<?>) {
            resolveAdditionalInstanceReferences((UAInstanceImpl<?>) node, resolveContext);
        }

        if (node instanceof UAMethodImpl) {
            resolveAdditionalMethodReferences((UAMethodImpl) node, resolveContext);
        }

        if (node instanceof UAVariableImpl) {
            resolveAdditionalVariableReferences((UAVariableImpl) node, resolveContext);
        }

        if (node instanceof UADataTypeImpl) {
            resolveAdditionalDataTypeReferences((UADataTypeImpl) node, resolveContext);
        }

        if (node instanceof UAVariableTypeImpl) {
            resolveAdditionalVariableTypeReferences((UAVariableTypeImpl) node, resolveContext);
        }

        return node.getRawNode().getReferences().stream().map(rawReference -> {
            var referenceTypeUncasted = resolveReference(rawReference.getReferenceType(), resolveContext);
            if (!(referenceTypeUncasted instanceof UAReferenceType)) {
                throw new NodeSetResolveException("Non-ReferenceType " + referenceTypeUncasted.getBrowseName() + " (" + referenceTypeUncasted.getNodeId() + ") used as ReferenceType in references of " + node.getBrowseName() + "(" + node.getNodeId() + ")");
            }

            var referencedNode = resolveReference(rawReference.getReferencedId(), resolveContext);

            if (referencedNode == null) {
                throw new NodeSetResolveException("Referenced node (" + rawReference.getReferencedId() + ") referenced in " + node.getBrowseName() + "(" + node.getNodeId() + ")" + " does not exist");
            }

            var referenceType = ((UAReferenceType) referenceTypeUncasted);

            if (rawReference.isForward()) {
                return new NodeSetUniverse.Reference(node, referencedNode, referenceType);
            } else {
                return new NodeSetUniverse.Reference(referencedNode, node, referenceType);
            }
        });
    }

    private void resolveAdditionalVariableTypeReferences(UAVariableTypeImpl node, ResolveContext resolveContext) {
        var dataTypeReference = node.getRawNode().getDataType();
        var dataType = resolveReference(dataTypeReference, resolveContext);
        if (!(dataType instanceof UADataType)) {
            throw new NodeSetResolveException("Non-DataType " + dataType.getBrowseName() + "(" + dataType.getNodeId() + ") referenced as DataType in " + node.getBrowseName() + "(" + node.getNodeId() + ")");
        }
        node.setDataType(((UADataType) dataType));
    }

    private void resolveAdditionalDataTypeReferences(UADataTypeImpl node, ResolveContext resolveContext) {
        for (var definitionField : node.getDefinition()) {
            resolveDefinitionFieldReferences((DefinitionFieldImpl) definitionField, resolveContext);
        }
    }

    private void resolveDefinitionFieldReferences(DefinitionFieldImpl definitionField, ResolveContext resolveContext) {
        var dataTypeReference = definitionField.getRawDefinitionField().getDataType();
        if (dataTypeReference != null) {
            var dataType = resolveReference(dataTypeReference, resolveContext);
            if (!(dataType instanceof UADataType)) {
                throw new NodeSetResolveException("Non-DataType " + dataType.getBrowseName() + "(" + dataType.getNodeId() + ") referenced as DataType in field " + definitionField.getName());
            }
            definitionField.setDataType((UADataType) dataType);
        }
    }

    private void resolveAdditionalVariableReferences(UAVariableImpl node, ResolveContext resolveContext) {
        var datatype = node.getRawNode().getDataType();

        if (datatype != null) {
            node.setDataType((UADataType) resolveReference(datatype, resolveContext));
        }

        for (var argument : node.getArguments()) {
            resolveArgumentReferences((UAVariableImpl.ArgumentImpl) argument, resolveContext);
        }
    }

    private void resolveArgumentReferences(UAVariableImpl.ArgumentImpl argument, ResolveContext resolveContext) {
        var argDataTypeReference = argument.getRawArgument().getDataType();
        argument.setDataType((UADataType) resolveReference(argDataTypeReference, resolveContext));
    }

    private void resolveAdditionalMethodReferences(UAMethodImpl node, ResolveContext resolveContext) {
        var methodDeclarationReference = node.getRawNode().getMethodDeclarationId();

        if (methodDeclarationReference != null) {
            node.setMethodDeclaration((UAMethod) resolveReference(methodDeclarationReference, resolveContext));
        }
    }

    private void resolveAdditionalInstanceReferences(UAInstanceImpl<?> node, ResolveContext resolveContext) {
        var parentReference = node.getRawNode().getParentNodeId();

        if (parentReference != null) {
            node.setParent(resolveReference(parentReference, resolveContext));
        }
    }

    private UANode resolveReference(String reference, ResolveContext resolveContext) {
        if (!nodeIdParser.isNodeId(reference)) {
            reference = resolveContext.aliases.get(reference);
        }

        var nodeId = nodeIdParser.parseNodeId(reference, resolveContext.namespaceUris);
        var modelOfReferencedNode = resolveContext.modelsByNamespaceUri.get(nodeId.getNamespaceUri());
        return modelOfReferencedNode.getNodeById(nodeId);
    }
}
