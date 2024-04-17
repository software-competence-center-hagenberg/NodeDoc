import React from "react";
import { createPortal } from "react-dom";
import { Editor, TextDisplayStyle } from "./Editor";
import { NodeSetText } from "./NodeSetText";
import { getAllDocuments, loadDocuments, updateTextAndSaveDocument as storeUserText } from "./dbaccess";
import { TextId, isTextIdEqual } from "./TextId";
import { NodeSetVersion, VersionSelector } from "./VersionSelector";

import { performDownload } from "./download";

type EditorEnvironmentProps = {
    namespaceUri: string,
    version: string,
    publicationDate: string,
    editorDivs: HTMLElement[],
};

type EditorEnvironmentState = {
    allDocuments: NodeSetText[],
    selectedBaseVersion?: NodeSetVersion,
};

type CurrentTextAndTextDisplayStyle = {
    currentText: string,
    textDisplayStyle: TextDisplayStyle,
};

function calculateShownTextAndTextDisplayStyle(baseVersionTextDocument: NodeSetText | undefined, currentVersionTextDocument: NodeSetText): CurrentTextAndTextDisplayStyle {    
    if (currentVersionTextDocument.userText) {
        return {
            currentText: currentVersionTextDocument.userText,
            textDisplayStyle: TextDisplayStyle.UserDefinedTextInCurrentVersion, 
        };
    } else if (baseVersionTextDocument && baseVersionTextDocument.userText) {
        return {
            currentText: baseVersionTextDocument.userText,
            textDisplayStyle: TextDisplayStyle.UserDefinedTextFromBaseVersion
        };
    } else if (currentVersionTextDocument.xmlText) {
        return {
            currentText: currentVersionTextDocument.xmlText,
            textDisplayStyle: TextDisplayStyle.OriginalXMLFromCurrentVersion,
        };
    } else {
        return {
            currentText: "",
            textDisplayStyle: TextDisplayStyle.MissingTextInBaseAndCurrentVersion,
        };
    }
}

export class EditorEnvironment extends React.Component <EditorEnvironmentProps, EditorEnvironmentState> {
    
    constructor(p: EditorEnvironmentProps) {
        super(p);
        this.state = {
            allDocuments: [],
        }
        this.handleSelectBaseVersion = this.handleSelectBaseVersion.bind(this);
        this.importAllTextsIntoCurrentVersion = this.importAllTextsIntoCurrentVersion.bind(this);
    }

    async componentDidMount() {
        
        await loadDocuments(this.props.namespaceUri);

        this.setState({
            allDocuments: getAllDocuments(),
        })
    }

    async saveAsUserDefinedText(textId: TextId, newText: string) {
        const promise = storeUserText(this.props.namespaceUri, this.props.version, this.props.publicationDate, textId, newText);
        this.setState({
            allDocuments: getAllDocuments(),
        });
        await promise;
        this.setState({
            allDocuments: getAllDocuments(),
        });
    }

    getEditorsAndTexts() {
        return this.props.editorDivs.map(element => {
            const textId = JSON.parse(atob(element.dataset.textid!)) as TextId;

            const currentTextDocument = this.state.allDocuments.find(text => {
                return text.namespaceUri === this.props.namespaceUri &&
                    text.version === this.props.version &&
                    text.publicationDate === this.props.publicationDate &&
                    isTextIdEqual(text.textId, textId);
            });

            const selectedBaseVersion = this.state.selectedBaseVersion;
            const baseVersionTextDocument = selectedBaseVersion
                ? this.state.allDocuments.find(text => {
                    return text.namespaceUri === this.props.namespaceUri &&
                        text.version === selectedBaseVersion.version &&
                        text.publicationDate === selectedBaseVersion.publicationDate &&
                        isTextIdEqual(text.textId, textId);
                }) : undefined;

            const {currentText, textDisplayStyle} = currentTextDocument
                ? calculateShownTextAndTextDisplayStyle(baseVersionTextDocument, currentTextDocument)
                : {currentText: "", textDisplayStyle: TextDisplayStyle.OriginalXMLFromCurrentVersion }

            return {
                editorDiv: element,
                textId,
                currentTextDocument,
                baseVersionTextDocument,
                currentText,
                textDisplayStyle,
            };
        })
    }

    handleSelectBaseVersion(version?: NodeSetVersion) {
        this.setState({
            selectedBaseVersion: version
        });
    }

    async importAllTextsIntoCurrentVersion() {
        const entriesForImport = this.getEditorsAndTexts()
            .filter(entry => entry.textDisplayStyle === TextDisplayStyle.UserDefinedTextFromBaseVersion);
        for (let entry of entriesForImport) {
            await this.saveAsUserDefinedText(entry.textId, entry.currentText);
        }
    }

    render() {
        const editorsAndTexts = this.getEditorsAndTexts();
        const stats = {
            elementsWithoutText: editorsAndTexts.filter(entry => entry.textDisplayStyle === TextDisplayStyle.MissingTextInBaseAndCurrentVersion).length,
            elementsWithCurrentXMLText: editorsAndTexts.filter(entry => entry.textDisplayStyle === TextDisplayStyle.OriginalXMLFromCurrentVersion).length,
            elementsWithBaseVersionUserText: editorsAndTexts.filter(entry => entry.textDisplayStyle === TextDisplayStyle.UserDefinedTextFromBaseVersion).length,
            elementsWithCurrentVersionUserText: editorsAndTexts.filter(entry => entry.textDisplayStyle === TextDisplayStyle.UserDefinedTextInCurrentVersion).length,
        }
        const availableVersions = [... new Set(this.state.allDocuments
            .filter(document => !(document.version === this.props.version && document.publicationDate === this.props.publicationDate))
            .map(document => {
                return JSON.stringify({
                    version: document.version,
                    publicationDate: document.publicationDate,
                } as NodeSetVersion);
            }))]
            .map(str => JSON.parse(str) as NodeSetVersion)
        return <>
            <div>
                <button onClick={() => performDownload(this.props.namespaceUri, this.props.version)}>Download</button>
                <h3>Text Import</h3>
                <span>Select base version:&nbsp;</span>
                <VersionSelector
                    selectedVersion={this.state.selectedBaseVersion}
                    allVersions={availableVersions}
                    onSelectVersion={this.handleSelectBaseVersion}
                />
                <h3>Documentation progress</h3>
                <p><i className="material-icons md-18 warning">warning</i> Number of elements without a text: {stats.elementsWithoutText}</p>
                <p>Number of elements with original XML texts: {stats.elementsWithCurrentXMLText}</p>
                <p>Number of elements with texts from base version: {stats.elementsWithBaseVersionUserText} {stats.elementsWithBaseVersionUserText > 0 ? <button onClick={this.importAllTextsIntoCurrentVersion}>Import texts into current version</button> : <></>}</p>
                <p>Number of elements with current versions texts: {stats.elementsWithCurrentVersionUserText}</p>
                <h3>Color coding</h3>
                <div style={{marginBottom: "5px"}} className="missing-text">No text defined</div>
                <div style={{marginBottom: "5px"}} className="original-xml-text">Original XML text from current version</div>
                <div style={{marginBottom: "5px"}} className="user-defined-text-from-base-version">User defined text from base version is shown</div>
                <div className="user-defined-text">Text defined in current version</div>
                <h3>Diagram</h3>
                <p>Pan: Click and hold left/middle mouse button</p>
                <p>Zoom: CTRL + mouse wheel</p>
            </div>
            {editorsAndTexts.map(entry => {                
                return createPortal(<>
                    { entry.currentTextDocument ?
                        <Editor
                            textId={entry.textId}
                            nodeSetPublicationDate={this.props.publicationDate}
                            textDisplayStyle={entry.textDisplayStyle}
                            key={entry.editorDiv.dataset.textid}
                            title={entry.editorDiv.dataset.title!}
                            placeholderText={entry.editorDiv.dataset.placeholder!}
                            initialHeadingLevel={parseInt(entry.editorDiv.dataset.headinglevel!)}
                            currentText={entry.currentText}
                            allDocuments={this.state.allDocuments}
                            onTextChanged={(v) => this.saveAsUserDefinedText(entry.textId, v)}
                            onUseBaseVersionText={() => this.saveAsUserDefinedText(entry.textId, entry.currentText)}/>
                        : <p>Loading...</p>
                    }
                </>, entry.editorDiv);
            })}
        </>
    }
}