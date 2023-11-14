type DocumentationTextId = {
    type: "documentation",
    nodeId: string,
}

type DescriptionTextId = {
    type: "description",
    nodeId: string,
}

type FieldTextId = {
    type: "field",
    nodeId: string,
    fieldName: string,
}

type ArgumentTextId = {
    type: "argument",
    nodeId: string,
    argumentName: string,
}

type FreetextTextId = {
    type: "freetext",
    headerId: string,
}

export type TextId = 
    DocumentationTextId | 
    DescriptionTextId | 
    ArgumentTextId | 
    FreetextTextId | 
    FieldTextId;

export function isTextIdEqual(a: TextId, b: TextId) {
    return (a.type === "documentation" && b.type === "documentation" && a.nodeId === b.nodeId)
        || (a.type === "description" && b.type === "description" && a.nodeId === b.nodeId)
        || (a.type === "field" && b.type === "field" && a.nodeId === b.nodeId && a.fieldName === b.fieldName)
        || (a.type === "argument" && b.type === "argument" && a.nodeId === b.nodeId && a.argumentName === b.argumentName)
        || (a.type === "freetext" && b.type === "freetext" && a.headerId === b.headerId)
}