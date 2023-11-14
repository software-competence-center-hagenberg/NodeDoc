import React, { ChangeEvent } from "react";

export type NodeSetVersion = {
    version: string,
    publicationDate: string,
};

type VersionSelectorProps = {
    selectedVersion?: NodeSetVersion,
    allVersions: NodeSetVersion[],
    onSelectVersion: (version?: NodeSetVersion) => void,
};

function isVersionEqual(a: NodeSetVersion, b: NodeSetVersion) {
    return a.version === b.version && a.publicationDate === b.publicationDate;
}

const noSelectionValue = -1;

export class VersionSelector extends React.Component<VersionSelectorProps, {}> {

    constructor(p: VersionSelectorProps) {
        super(p);
        this.handleSelectChange = this.handleSelectChange.bind(this);
    }

    getSortedVersions() {
        const copy = [...this.props.allVersions];
        copy.sort((a, b) => {
            const versionCompare = a.version.localeCompare(b.version);
            return versionCompare === 0
                ? a.publicationDate.localeCompare(b.publicationDate)
                : versionCompare; 
        })
        return copy;
    }

    indexOf(version: NodeSetVersion) {
        return this.getSortedVersions().findIndex(v => isVersionEqual(version, v))
    }

    getSelectedValue() {
        const selectedVersion = this.props.selectedVersion;
        return selectedVersion ? this.indexOf(selectedVersion) : noSelectionValue;
    }

    handleSelectChange(e: ChangeEvent<HTMLSelectElement>) {
        const index = parseInt(e.target.value);
        if (index === -1) {
            this.props.onSelectVersion(undefined);
        } else {
            this.props.onSelectVersion(this.getSortedVersions()[index]);
        }
    }

    render() {
        return <select
            value={this.getSelectedValue()}
            onChange={this.handleSelectChange}
        >
            <option value={noSelectionValue}>No version</option>
            {this.getSortedVersions().map((version, index) =>
                <option key={JSON.stringify(version)} value={index}>{version.version} / {version.publicationDate}</option>
            )}
        </select>
    }
}