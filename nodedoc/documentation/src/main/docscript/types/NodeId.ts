type BaseNodeId = {
    namespace: string,
}

type IntNodeId = BaseNodeId & {
    type: "i",
    value: number,
}

type StringNodeId = BaseNodeId & {
    type: "s",
    value: string,
}

export type NodeId = IntNodeId | StringNodeId;

export function isNodeIdEqual(a: NodeId, b: NodeId) {
    return a.namespace === b.namespace && a.type === b.type && a.value === b.value;
}
