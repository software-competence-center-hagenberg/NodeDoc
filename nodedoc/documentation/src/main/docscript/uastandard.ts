import { NodeId } from "./types/NodeId";

export const referenceTypes = {
    hasEventSource: {
        namespace: "http://opcfoundation.org/UA/",
        type: "i",
        value: 36,
    } as NodeId,
    hasTypeDefinition: {
        namespace: "http://opcfoundation.org/UA/",
        type: "i",
        value: 40,
    } as NodeId,
    hasSubtype: {
        namespace: "http://opcfoundation.org/UA/",
        type: "i",
        value: 45,
    } as NodeId,
    hasProperty: {
        namespace: "http://opcfoundation.org/UA/",
        type: "i",
        value: 46,
    } as NodeId,
    hasComponent: {
        namespace: "http://opcfoundation.org/UA/",
        type: "i",
        value: 47,
    } as NodeId,
} as const;
