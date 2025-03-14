package at.scch.nodedoc.util;

import at.scch.nodedoc.nodeset.*;
import at.scch.nodedoc.uaStandard.BrowseNames;
import at.scch.nodedoc.uaStandard.Nodes;

public class UAModelUtils {

    public static boolean isContainerType(UAObjectType type) {
        // has number component with modelling rule
        var hasNumberComponentWithModellingRule = type.getForwardReferencedNodes(Nodes.ReferenceTypes.HAS_COMPONENT)
                .stream()
                .filter(UAModelUtils::isNumberComponentNode)
                .anyMatch(UAModelUtils::hasModellingRule);

        var hasNodeVersionRef = type.getForwardReferencedNodes(Nodes.ReferenceTypes.HAS_PROPERTY)
                .stream()
                .anyMatch(property -> property.getBrowseName().equals(BrowseNames.NODE_VERSION));

        return hasNumberComponentWithModellingRule && hasNodeVersionRef;
    }

    private static boolean isNumberComponentNode(UANode node) {
        return node.getBrowseName().matches(".*<.+>.*");
    }

    private static boolean hasModellingRule(UANode numberComponent) {
        return numberComponent.getModellingRules().stream()
                .anyMatch(modellingRule -> modellingRule.getNodeId().equals(Nodes.Objects.OPTIONAL_PLACEHOLDER)
                        || modellingRule.getNodeId().equals(Nodes.Objects.MANDATORY_PLACEHOLDER));
    }

    public static boolean isEnumDataType(UADataType type) {
        return isSubtypeOf(type, Nodes.DataTypes.ENUMERATION);
    }

    public static boolean isStructureDataType(UADataType type) {
        return isSubtypeOf(type, Nodes.DataTypes.STRUCTURE);
    }

    private static boolean isSubtypeOf(UADataType type, NodeId<?> baseTypeId) {
        if (type.getNodeId().equals(baseTypeId)) {
            return true;
        } else if (type.getNodeId().equals(Nodes.DataTypes.BASE_DATATYPE)) {
            return false;
        } else {
            return type.getBackwardReferencedNodes(Nodes.ReferenceTypes.HAS_SUBTYPE).stream()
                    .filter(node -> node instanceof UADataType)
                    .map(node -> ((UADataType) node))
                    .anyMatch(node -> isSubtypeOf(node, baseTypeId));
        }
    }

    public static String valueRankAsString(UAVariableType variableType) {
        return valueRankAsString(variableType.getValueRank());
    }

    public static String valueRankAsString(int valueRank) {
        if (valueRank > 1)
            return valueRank + " Dimensions";
        else if (valueRank == 1)
            return "OneDimension";
        else if (valueRank == 0)
            return "OneOrMoreDimensions";
        else if (valueRank == -1)
            return "Scalar";
        else if (valueRank == -2)
            return "Any";
        else if (valueRank == -3)
            return "ScalarOrOneDimension";
        else
            return Integer.toString(valueRank);
    }

    public static UANode getTypeDefinition(UAInstance object) {
        var typeDefinitions = object.getForwardReferencedNodes(Nodes.ReferenceTypes.HAS_TYPE_DEFINITION);
        if (typeDefinitions.size() > 1) {
            throw new RuntimeException(object.getBrowseName() + "(" + object.getNodeId() + ")" + " does have more than one TypeDefinition");
        }
        return typeDefinitions.stream().findAny().orElse(null);
    }
}
