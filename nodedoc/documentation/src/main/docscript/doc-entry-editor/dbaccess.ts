import { NodeSetText } from "./NodeSetText";
import { TextId, isTextIdEqual } from "./TextId";
import config from "../config";

const baseUrl = config.couchDbUri;

let allDocuments: NodeSetText[] = [];

export function getAllDocuments() {
    return allDocuments;
}

export async function loadDocuments(namespaceUri: string) {
    allDocuments = (await performPostRequest(baseUrl + "/opc_nodes/_find", {
        selector: {
            namespaceUri: namespaceUri,
        },
        limit: 100000,
    })).docs as NodeSetText[];
}

export async function updateTextAndSaveDocument(namespaceUri: string, version: string, publicationDate: string, textId: TextId, newText: string) {
    const oldTextDocument = allDocuments.find(document => {
        return document.namespaceUri === namespaceUri 
            && document.version === version
            && document.publicationDate === publicationDate
            && isTextIdEqual(document.textId, textId);
    })!;

    let newDocument = {
        ...oldTextDocument,
        userText: newText,
    }

    const newAllDocuments = allDocuments.map(oldTextDocument => {
        if (oldTextDocument.namespaceUri === namespaceUri 
            && oldTextDocument.version === version
            && oldTextDocument.publicationDate === publicationDate
            && isTextIdEqual(oldTextDocument.textId, textId)) {
            return newDocument;
        } else {
            return oldTextDocument;
        }
    });

    allDocuments = newAllDocuments;

    const newRev = await saveDocument(newDocument);

    const newAllDocumentsWithUpdatedRev = newAllDocuments.map(oldTextDocument => {
        if (oldTextDocument.namespaceUri === namespaceUri 
            && oldTextDocument.version === version
            && oldTextDocument.publicationDate === publicationDate
            && isTextIdEqual(oldTextDocument.textId, textId)) {
            return {
                ...newDocument,
                _rev: newRev,
            };
        } else {
            return oldTextDocument;
        }
    });

    allDocuments = newAllDocumentsWithUpdatedRev;
}

async function saveDocument(document: NodeSetText) {
    const response = await performPostRequest(baseUrl + "/opc_nodes/", document);
    return response.rev;
}

async function performPostRequest(url: string, query: any) {
    const response = await fetch(url, {
        method: 'post',
        body: JSON.stringify(query),
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
    });
    return await response.json();
}