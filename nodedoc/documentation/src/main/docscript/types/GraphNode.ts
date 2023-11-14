import { NodeId } from "./NodeId";

type BaseGraphNode = {
    nodeId: NodeId,
    browseName: string,
    chapterAnchor: string,
    tooltipText: string,
    color: string,
}

export type ObjectGraphNode = BaseGraphNode & {
    nodeClass: "UAObject",
}

export type ObjectTypeGraphNode = BaseGraphNode & {
    nodeClass: "UAObjectType",
}

export type VariableGraphNode = BaseGraphNode & {
    nodeClass: "UAVariable",
    dataType: NodeId,
}

export type VariableTypeGraphNode = BaseGraphNode & {
    nodeClass: "UAVariableType",
}

export type DataTypeGraphNode = BaseGraphNode & {
    nodeClass: "UADataType",
}

export type ReferenceTypeGraphNode = BaseGraphNode & {
    nodeClass: "UAReferenceType",
    symmetric: boolean,
}

export type MethodGraphNode = BaseGraphNode & {
    nodeClass: "UAMethod",
}

export type ViewGraphNode = BaseGraphNode & {
    nodeClass: "UAView",
}

export type GraphNode =
    ObjectGraphNode |
    ObjectTypeGraphNode |
    VariableGraphNode |
    VariableTypeGraphNode |
    DataTypeGraphNode |
    ReferenceTypeGraphNode |
    MethodGraphNode |
    ViewGraphNode;

export function isInstance(node: GraphNode): boolean {
    return node.nodeClass === "UAMethod" ||
        node.nodeClass === "UAObject" ||
        node.nodeClass === "UAVariable" ||
        node.nodeClass === "UAView";
}

export function isType(node: GraphNode): boolean {
    return node.nodeClass === "UADataType" ||
        node.nodeClass === "UAObjectType" ||
        node.nodeClass === "UAReferenceType" ||
        node.nodeClass === "UAVariableType";
}

export function isReferenceType(node: GraphNode): node is ReferenceTypeGraphNode {
    return node.nodeClass === "UAReferenceType";
}