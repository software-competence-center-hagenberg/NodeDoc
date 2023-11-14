package at.scch.nodedoc.uaStandard;

import at.scch.nodedoc.nodeset.NodeId;

public class Nodes {
    public static class Objects {

        public static final NodeId<?> OPTIONAL = new NodeId.IntNodeId(Namespaces.UA, 80);
        public static final NodeId<?> OPTIONAL_PLACEHOLDER = new NodeId.IntNodeId(Namespaces.UA, 11508);
        public static final NodeId<?> MANDATORY = new NodeId.IntNodeId(Namespaces.UA, 78);
        public static final NodeId<?> MANDATORY_PLACEHOLDER = new NodeId.IntNodeId(Namespaces.UA, 11510);
    }

    public static class Variables {
        public static final NodeId<?> NODE_VERSION = new NodeId.IntNodeId(Namespaces.UA, 3068);
    }

    public static class DataTypes {

        public static final NodeId<?> ENUMERATION = new NodeId.IntNodeId(Namespaces.UA, 29);
        public static final NodeId<?> STRUCTURE = new NodeId.IntNodeId(Namespaces.UA, 22);
        public static final NodeId<?> BASE_DATATYPE = new NodeId.IntNodeId(Namespaces.UA, 24);
    }

    public static class ReferenceTypes {

        public static final NodeId<?> HAS_PROPERTY = new NodeId.IntNodeId(Namespaces.UA, 46);
        public static final NodeId<?> HAS_COMPONENT = new NodeId.IntNodeId(Namespaces.UA, 47);
        public static final NodeId<?> HAS_TYPE_DEFINITION = new NodeId.IntNodeId(Namespaces.UA, 40);
        public static final NodeId<?> HAS_MODELLING_RULE = new NodeId.IntNodeId(Namespaces.UA, 37);
        public static final NodeId<?> GENERATES_EVENT = new NodeId.IntNodeId(Namespaces.UA, 41);
        public static final NodeId<?> HAS_SUBTYPE = new NodeId.IntNodeId(Namespaces.UA, 45);
        public static final NodeId<?> HAS_ENCODING = new NodeId.IntNodeId(Namespaces.UA, 38);
    }
}
