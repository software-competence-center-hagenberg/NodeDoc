
import escapeHtml from "escape-html";
import React, { Ref, RefObject } from "react";
import ContentEditable, { ContentEditableEvent } from "react-contenteditable";
import { TextHistory } from "./TextHistory";
import { TextId } from "./TextId";
import { convertToHTML } from "./markdown";
import { NodeSetText } from "./NodeSetText";

export enum TextDisplayStyle {
    UserDefinedTextInCurrentVersion, // "left green border"
    OriginalXMLFromCurrentVersion, // "blue border"
    UserDefinedTextFromBaseVersion, // "left orange border"
    MissingTextInBaseAndCurrentVersion, // "full orange border"
}

type EditorProps = {
    textId: TextId,
    nodeSetPublicationDate: string,
    textDisplayStyle: TextDisplayStyle,
    title: string,
    placeholderText: string,
    currentText: string,
    initialHeadingLevel: number,
    allDocuments: NodeSetText[],
    onTextChanged: (newValue: string) => void,
    onUseBaseVersionText: () => void,
};

type ViewingState = {
    type: "viewing",
};

type EditingState = {
    type: "editing",
    editingText: string,
}

type EditorState = {
    historyVisible: boolean,
    textState: ViewingState
             | EditingState
};

export class Editor extends React.Component<EditorProps, EditorState> {
    
    private editorDivRef: RefObject<HTMLElement>;

    constructor(p: EditorProps) {
        super(p);
        this.state = {
            historyVisible: false,
            textState: {
                type: "viewing",
            }
        };
        
        this.editorDivRef = React.createRef();

        this.startEditing = this.startEditing.bind(this);
        this.finishEditing = this.finishEditing.bind(this);
        this.editDivChanged = this.editDivChanged.bind(this);
        this.toggleHistory = this.toggleHistory.bind(this);
        this.handleHistoryTextUpdate = this.handleHistoryTextUpdate.bind(this);
        this.scrollToNextMissingText = this.scrollToNextMissingText.bind(this);
    }

    startEditing() {
        this.setState({
            textState: {
                type: "editing",
                editingText: this.props.currentText,
            }
        });
    }

    finishEditing() {
        if (this.state.textState.type === "editing") {
            if (this.props.currentText !== this.state.textState.editingText) {
                this.props.onTextChanged(this.state.textState.editingText);
            }
        }
        this.setState({
            textState: {
                type: "viewing"
            }
        });
    }

    toggleHistory() {
        this.setState({
            historyVisible: !this.state.historyVisible
        })
    }

    handleHistoryTextUpdate(newText: string) {
        this.props.onTextChanged(newText);
        this.setState({
            historyVisible: false,
        });
    }

    editDivChanged(e: ContentEditableEvent) {
        this.setState({
            textState: {
                type: "editing",
                editingText: e.currentTarget.innerText,
            }
        });
    }

    getHtmlContent() {
        switch(this.state.textState.type) {
            case "editing":
                return escapeHtml(this.state.textState.editingText);
            case "viewing":
                if (this.props.currentText !== "") {
                    return convertToHTML(this.props.currentText, this.props.initialHeadingLevel);
                } else {
                    return this.props.placeholderText;
                }
        }
    }

    getCustomTextStyleClass() {
        switch (this.props.textDisplayStyle) {
            case TextDisplayStyle.UserDefinedTextFromBaseVersion:
                return "user-defined-text-from-base-version";
            case TextDisplayStyle.UserDefinedTextInCurrentVersion:
                return "user-defined-text";
            case TextDisplayStyle.MissingTextInBaseAndCurrentVersion:
                return "missing-text";
            case TextDisplayStyle.OriginalXMLFromCurrentVersion:
                return "original-xml-text";
        }
    }

    scrollToNextMissingText() {
        const divs = Array.from(document.getElementsByClassName("text-area"))
            .filter(div => div.classList.contains("missing-text") || div.classList.contains("user-defined-text-from-base-version")) as HTMLDivElement[];
        const currentDiv = this.editorDivRef.current!;

        const divAfterCurrentDiv = divs.find(div => currentDiv?.compareDocumentPosition(div) & Node.DOCUMENT_POSITION_FOLLOWING);

        const scrollOptions = {
            block: "center",
            behavior: "smooth",
        } as ScrollIntoViewOptions;

        if (divAfterCurrentDiv) {
            divAfterCurrentDiv.scrollIntoView(scrollOptions);
            divAfterCurrentDiv.focus();
        } else if (divs.length > 0) {
            divs[0].scrollIntoView(scrollOptions);
            divs[0].focus();
        }
        
    }

    render() {
        return <>
            <p>{this.props.title}</p>
            <ContentEditable
                className={`text-area ${this.getCustomTextStyleClass()}`}
                innerRef={this.editorDivRef}
                style={this.state.textState.type === "editing" ? {whiteSpace: "pre-wrap"} : {}}
                html={this.getHtmlContent()}
                onFocus={this.startEditing}
                onBlur={this.finishEditing}
                onChange={this.editDivChanged}
            />
            <div className="editor-menu">
                <button onClick={() => this.editorDivRef.current?.focus()} title="Edit text">
                    <i className="material-icons md-18 edit-icon">edit</i>
                </button>
                <button onClick={this.toggleHistory} title="Browse text history">
                    <i className="material-icons md-18 history-icon">history</i>
                </button>
                <button onClick={this.scrollToNextMissingText} title="Jump to next missing text">
                    <i className="material-icons md-18">navigate_next</i>
                </button>
                { this.props.textDisplayStyle === TextDisplayStyle.UserDefinedTextFromBaseVersion
                    ? <button onClick={() => this.props.onUseBaseVersionText()} title="Use text from base version"><i className="material-icons md-18">done</i></button>
                    : <></>}
            </div>
            { this.state.historyVisible ? 
                <TextHistory
                    allDocuments={this.props.allDocuments}
                    textId={this.props.textId}
                    nodeSetPublicationDate={this.props.nodeSetPublicationDate}
                    initialHeadingLevel={this.props.initialHeadingLevel}
                    onSelectText={this.handleHistoryTextUpdate}
                /> : <></>}
        </>;
    }
}