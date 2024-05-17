package at.scch.nodedoc.html;

import at.scch.nodedoc.nodeset.NodeClass;
import at.scch.nodedoc.nodeset.NodeId;
import at.scch.nodedoc.nodeset.UANode;
import at.scch.nodedoc.uaStandard.Nodes;

import java.util.ArrayList;
import java.util.List;

public class Utilities {

    public static String getNodeClassString(UANode node) {
        return getNodeClassString(node.getNodeClass());
    }

    public static String getNodeClassString(NodeClass nodeClass) {
        return nodeClass.toString().replace("UA", "");
    }

    public static String getModellingRuleAbbr(UANode modellingRuleNode) {

        if (modellingRuleNode.getNodeId().equals(Nodes.Objects.OPTIONAL)) {
            return "O";
        } else if (modellingRuleNode.getNodeId().equals(Nodes.Objects.OPTIONAL_PLACEHOLDER)) {
            return "OP";
        } else if (modellingRuleNode.getNodeId().equals(Nodes.Objects.MANDATORY)) {
            return "M";
        } else if (modellingRuleNode.getNodeId().equals(Nodes.Objects.MANDATORY_PLACEHOLDER)) {
            return "MP";
        } else {
            return modellingRuleNode.getBrowseName();
        }
    }

    public static List<String> getAccessLevelList(int accessLevel) {

        var accessLevelNames = List.of(
                "CurrentRead", "CurrentWrite", "HistoryRead", "HistoryWrite", "SemanticChange", "StatusWrite", "TimestampWrite", "Reserved"
        );
        // 255 - all bits are set
        if (accessLevel > 255) {
            throw new InvalidAccessLevelException("AccessLevel " + accessLevel + " out of bounds");
        }
        List<String> setAccessLevels = new ArrayList<>();
        for (int i = 0; accessLevel != 0; ++i, accessLevel >>>= 1) {
            if ((accessLevel & 1) != 0) {
                setAccessLevels.add(accessLevelNames.get(i));
            }
        }
        return setAccessLevels;
    }

    public static String stripPrefix(String prefixed) {
        if (prefixed != null && prefixed.contains(":")) {
            return prefixed.split(":")[1];
        }
        return prefixed;
    }

    public static String escapeLessThanSign(String unescaped) {
        return unescaped.replaceAll("<", "&lt;");
    }

    public static String escapeGreaterThanSign(String unescaped) {
        return unescaped.replaceAll(">", "&gt;");
    }

    public static String htmlBrowseName(String browseName) {
        return escapeLessThanSign(browseName);
    }

    public static String addHtmlLineBreakToCamelCaseString(String camelCase) {
        return addLineBreakToCamelCaseString(camelCase, "<br/>");
    }

    private static String addLineBreakToCamelCaseString(String camelCase, String lb) {
        int center = camelCase.length() / 2;

        for (int i = 0; i < center; ++i) {
            if (Character.isUpperCase(camelCase.charAt(center + i))) {
                return camelCase.substring(0, center + i) + lb + camelCase.substring(center + i);
            }
            if (Character.isUpperCase(camelCase.charAt(center - i))) {
                return camelCase.substring(0, center - i) + lb + camelCase.substring(center - i);
            }
        }
        return camelCase;
    }

    public static String getNodeIdAnchor(NodeId<?> nodeId) {
        if (nodeId instanceof NodeId.IntNodeId) {
            return "node:" + nodeId.getNamespaceUri()
                    + ";i=" + nodeId.getId();
        } else if (nodeId instanceof NodeId.StringNodeId) {
            return "node:" + nodeId.getNamespaceUri()
                    + ";s=" + nodeId.getId();
        } else if (nodeId instanceof NodeId.GuidNodeId) {
            return "node:" + nodeId.getNamespaceUri()
                    + ";g=" + nodeId.getId();
        } else if (nodeId instanceof NodeId.OpaqueNodeId) {
            return "node:" + nodeId.getNamespaceUri()
                    + ";b=" + nodeId.getId();
        } else {
            throw new RuntimeException("Unknown NodeId class " + nodeId.getClass());
        }
    }

    public static String getNodeIdAnchorForInstance(NodeId<?> typeNodeId, NodeId<?> instanceNodeId) {
        return getNodeIdAnchor(typeNodeId) + "/" + getNodeIdAnchor(instanceNodeId);
    }
}
