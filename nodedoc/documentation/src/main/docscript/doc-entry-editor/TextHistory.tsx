import React, { ChangeEvent } from "react"
import { TextId, isTextIdEqual } from "./TextId"
import { convertToHTML } from "./markdown";
import { NodeSetText } from "./NodeSetText";
import { compareAsc, format, parseISO } from "date-fns";
import { byDateString, byLocaleCompare, byMultiple } from "./sortutils";

type TextHistoryProps = {
    textId: TextId,
    nodeSetPublicationDate: string,
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
        this.jumpToPreviousVersion = this.jumpToPreviousVersion.bind(this);
        this.jumpToNextVersion = this.jumpToNextVersion.bind(this);
    }

    buildOptions() {
        return this.props.allDocuments.filter(document => {
            return isTextIdEqual(document.textId, this.props.textId);
        }).filter(document => {
            return compareAsc(parseISO(document.publicationDate), parseISO(this.props.nodeSetPublicationDate)) <= 0;
        }).sort(byMultiple<NodeSetText>([
            byLocaleCompare(x => x.version),
            byDateString(x => x.publicationDate),
        ])).flatMap(document => {
            const xmlOption = document.xmlText ? [{
                title: `${document.version} / ${document.publicationDate} (XML)`,
                text: document.xmlText!
            }] : [];

            if (document.userTextHistory) {
                const historyOptions = document.userTextHistory?.sort(byDateString((x) => x.date)).map((historyEntry) => {
                    return {
                        title: `${document.version} / ${document.publicationDate} (User / ${historyEntry.date})`,
                        text: historyEntry.text,
                    }
                }) ?? [];
                return [
                    ...xmlOption,
                    ...historyOptions,
                ];
            } else {
                const userOption = document.userText ? [{
                    title: `${document.version} / ${document.publicationDate} (User)`,
                    text: document.userText!
                }] : [];

                return [
                    ...xmlOption,
                    ...userOption,
                ];
            }
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

    canJumpToPreviousVersion(): boolean {
        return this.state.selectedOption > 0;
    }

    jumpToPreviousVersion() {
        this.setState({
            selectedOption: this.state.selectedOption - 1,
        });
    }

    canJumpToNextVersion(): boolean {
        return this.state.selectedOption < this.state.options.length - 1;
    }

    jumpToNextVersion() {
        this.setState({
            selectedOption: this.state.selectedOption + 1,
        });
    }

    render() {
        return <div className="history-box">
            <select value={this.state.selectedOption} onChange={this.handleSelectChange}>
                <option value="-1" disabled={true}>Select Version...</option>
                {this.state.options.map((option, i) => (
                    <option key={i} value={i}>{option.title}</option>
                ))}
            </select>
            { this.state.selectedOption !== -1 ? <>
                <div>
                    <button disabled={!this.canJumpToPreviousVersion()} onClick={this.jumpToPreviousVersion}>Previous version</button>
                    &nbsp;
                    <button disabled={!this.canJumpToNextVersion()} onClick={this.jumpToNextVersion}>Next version</button>
                </div>
                <p>Preview:</p>
                <div className="preview" dangerouslySetInnerHTML={{__html: 
                    convertToHTML(this.getCurrentOption().text, this.props.initialHeadingLevel)
                }}></div>
                <button onClick={() => this.props.onSelectText(this.getCurrentOption().text)}>Accept</button>
            </> : <></>}
        </div>
    }
}