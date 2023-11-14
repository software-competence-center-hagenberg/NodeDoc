export type ModelReference = ModelNode & { ReferenceType: string };

export type ModelNode = {
    BrowseName: string,
    NodeClass: string,
    isAbstract: boolean,
    link: string,
    BaseType: ModelNode,
    refInfos: Array<ModelReference>,
    HasSubtype: Array<ModelNode>,
    generatesEvent: Array<any>, // TODO
    TypeDefinition: ModelNode,
    DataType: ModelNode
};