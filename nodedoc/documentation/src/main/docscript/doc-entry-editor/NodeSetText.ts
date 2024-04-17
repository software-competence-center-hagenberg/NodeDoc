import { HistoryEntry } from "./HistoryEntry";
import { TextId } from "./TextId";

export type NodeSetText = {
	_id: string,
	_rev: string,
	namespaceUri: string,
	version: string,
	publicationDate: string,
	textId: TextId,
	xmlText?: string,
	userText?: string,
	userTextHistory?: HistoryEntry[],
}
