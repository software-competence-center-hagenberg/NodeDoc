import { throwExpression } from "../utils"
import { GraphNode, isReferenceType, ReferenceTypeGraphNode } from "./GraphNode"
import { isNodeIdEqual, NodeId } from "./NodeId"

type GraphReference = {
    referenceType: NodeId,
    source: NodeId,
    target: NodeId,
    color: string,
}

export type NodeColor = {
    name: string,
    backgroundColor: string,
    shadowColor: string
}

type ReferenceColor = {
    name: string,
    color: string
}

export type GraphData = {
    root: NodeId,
    nodes: Array<GraphNode>,
    references: Array<GraphReference>,
    nodeColors: Array<NodeColor>,
    referenceColors: Array<ReferenceColor>,
}

export function getRootNode(data: GraphData) {
    return getNodeById(data, data.root) ?? throwExpression(`Root node with NodeId ${JSON.stringify(data.root)} does not exist`);
}

export enum FilterMode {
    Include,
    Exclude,
}

export enum Direction {
    AsymmetricForward,
    AsymmetricBackward,
    Symmetric,
}

type Filter = {
    referenceTypes?: {
        nodeIds: NodeId[],
        mode: FilterMode,
    },
    referencedNodePredicate?: (node: GraphNode) => boolean,
}

export type ResolvedGraphReference = {
    source: GraphNode;
    target: GraphNode;
    referenceType: ReferenceTypeGraphNode;
    color: string;
}

export function getReferencedNodes(data: GraphData, nodeId: NodeId, direction: Direction, filter?: Filter): ResolvedGraphReference[] {
    let referenceTypeFilterFunction = (nodeId: NodeId) => true;

    if (filter?.referenceTypes) {
        const nodeIds = filter?.referenceTypes.nodeIds;
        switch (filter?.referenceTypes.mode) {
            case FilterMode.Include:
                referenceTypeFilterFunction = (x) => isNodeIdIn(x, nodeIds);
                break;
            case FilterMode.Exclude:
                referenceTypeFilterFunction = (x) => !isNodeIdIn(x, nodeIds);
                break;
        }
    }

    let referencedNodeFilterFunction = (source: GraphNode, target: GraphNode) => true;

    if (filter?.referencedNodePredicate) {
        const predicate = filter?.referencedNodePredicate;
        referencedNodeFilterFunction = (source, target) => isNodeIdEqual(nodeId, source.nodeId) ? predicate(target) : predicate(source);
    }


    return data.references
        .filter(reference => referenceTypeFilterFunction(reference.referenceType))
        .map(reference => ({
            ...reference,
            referenceType: getNodeById(data, reference.referenceType) ?? throwExpression(`ReferenceType with NodeId ${JSON.stringify(reference.referenceType)} does not exist`)
        }))
        .map(reference => (isReferenceType(reference.referenceType) ? {
            ...reference,
            referenceType: reference.referenceType
        } : throwExpression(`${reference.referenceType} is not a ReferenceType`)))
        .filter(reference => {
            switch (direction) {
                case Direction.AsymmetricForward:
                    return !reference.referenceType.symmetric && isNodeIdEqual(reference.source, nodeId);
                case Direction.AsymmetricBackward:
                    return !reference.referenceType.symmetric && isNodeIdEqual(reference.target, nodeId);
                case Direction.Symmetric:
                    return reference.referenceType.symmetric && (isNodeIdEqual(reference.source, nodeId) || isNodeIdEqual(reference.target, nodeId));
            }
        })
        .map(reference => ({
            ...reference,
            source: getNodeById(data, reference.source) ?? throwExpression(`Node with NodeId ${JSON.stringify(reference.source)} does not exist`),
            target: getNodeById(data, reference.target) ?? throwExpression(`Node with NodeId ${JSON.stringify(reference.target)} does not exist`)
        }))
        .filter(reference => referencedNodeFilterFunction(reference.source, reference.target))
}

function isNodeIdIn(candidate: NodeId, nodeIds: NodeId[]) {
    return nodeIds.some(n => isNodeIdEqual(n, candidate));
}

export function getNodeById(data: GraphData, id: NodeId) {
    return data.nodes.find(node => isNodeIdEqual(node.nodeId, id));
}

export function getNodeColorByName(data: GraphData, colorName: string) {
    return data.nodeColors.find(color => color.name === colorName);
}

export function getReferenceColorByName(data: GraphData, colorName: string) {
    return data.referenceColors.find(color => color.name === colorName)?.color;
}
