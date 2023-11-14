import ReactDOM from "react-dom/client";
import React from "react";
import { EditorEnvironment } from "./EditorEnvironment";


export function initDocEntryEditors() {
    document.addEventListener('DOMContentLoaded', (event) => {
        const editorDivs = Array.from(document.getElementsByClassName("doc-entry-editor")) as HTMLElement[];
        const editorRoot = document.getElementById("editor-root")!;
        ReactDOM
            .createRoot(editorRoot)
            .render(
                <EditorEnvironment
                    namespaceUri={editorRoot.dataset.namespaceuri!}
                    version={editorRoot.dataset.version!}
                    publicationDate={editorRoot.dataset.publicationdate!}
                    editorDivs={editorDivs}/>
            );
    });
}
