import React, { ChangeEvent } from "react"
import { TextId, isTextIdEqual } from "./TextId"
import { convertToHTML } from "./markdown";
import { NodeSetText } from "./NodeSetText";

type TextHistoryProps = {
    textId: TextId,
    initialHeadingLevel: number,
    onSelectText: (newText: string) => void,
    allDocuments: NodeSetText[],
};

type TextOption = {
    title: string,
    text: string,
};

type TextHistoryState = {
    selectedOption: number,
    options: TextOption[]
};

export class TextHistory extends React.Component<TextHistoryProps, TextHistoryState> {

    constructor(p: TextHistoryProps) {
        super(p);
        this.state = {
            selectedOption: -1,
            options: [],
        }
        this.handleSelectChange = this.handleSelectChange.bind(this);
    }

    buildOptions() {
        return this.props.allDocuments.filter(document => {
            return isTextIdEqual(document.textId, this.props.textId);
        }).sort((a, b) => {
            const versionCompare = a.version.localeCompare(b.version);
            return versionCompare === 0
                ? a.publicationDate.localeCompare(b.publicationDate)
                : versionCompare; 
        }).flatMap(document => {
            const xmlOption = document.xmlText ? [{
                title: `${document.version} / ${document.publicationDate} (original XML)`,
                text: document.xmlText!
            }] : [];

            const userOption = document.userText ? [{
                title: `${document.version} / ${document.publicationDate} (defined by User)`,
                text: document.userText!
            }] : [];

            return [
                ...xmlOption,
                ...userOption,
            ];
        });
    }

    componentDidMount(): void {
        this.setState({
            selectedOption: -1,
            options: this.buildOptions(),
        })
    }

    handleSelectChange(e: ChangeEvent<HTMLSelectElement>) {
        this.setState({
            selectedOption: parseInt(e.target.value),
        })
    }

    getCurrentOption() {
        return this.state.options[this.state.selectedOption];
    }

    render() {
        return <div className="history-box">
            <select value={this.state.selectedOption} onChange={this.handleSelectChange}>
                <option value="-1" disabled={true}>Select NodeSet...</option>
                {this.state.options.map((option, i) => (
                    <option key={i} value={i}>{option.title}</option>
                ))}
            </select>
            { this.state.selectedOption !== -1 ? <>
                <p>Preview:</p>
                <div className="preview" dangerouslySetInnerHTML={{__html: 
                    convertToHTML(this.getCurrentOption().text, this.props.initialHeadingLevel)
                }}></div>
                <button onClick={() => this.props.onSelectText(this.getCurrentOption().text)}>Accept</button>
            </> : <></>}
        </div>
    }
}