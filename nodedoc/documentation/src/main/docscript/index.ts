import { initDocEntryEditors } from "./doc-entry-editor";
import { drawOPCUAGraphHere, getOPCUAGraph } from "./graph";

(<any>window).getOPCUAGraph = getOPCUAGraph;
(<any>window).drawOPCUAGraphHere = drawOPCUAGraphHere;

(<any>window).initDocEntryEditors = initDocEntryEditors;
