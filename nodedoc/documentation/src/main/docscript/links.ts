import *  as joint from "jointjs";
import { ReferenceTypeGraphNode } from "./types/GraphNode";
import { isNodeIdEqual } from "./types/NodeId";
import { referenceTypes } from "./uastandard";
import { isObjKey } from "./utils";

const markerDefaults = {
    type: 'path',
    "stroke-width": 2,
    "stroke-location": 'outside',
    colorAttributes: [],
}

type ColorAttribute = 'stroke' | 'fill';

type MarkerProps = {
    d: string,
    type?: string,
    stroke?: string,
    fill?: string,
    "stroke-width"?: number,
    colorAttributes: ColorAttribute[],
}

export function configureLabel(link: joint.dia.Link, labelText: string) {
    link.appendLabel({
        attrs: {
            text: {
                text: labelText
            }
        }
    });
}

function createLink(color: string) {
    const link = new joint.shapes.standard.Link({
        attrs: {
            line: {
                connection: true,
                stroke: color,
                strokeWidth: 2,
                strokeLinejoin: 'round'
            },
            wrapper: {
                connection: true,
                strokeWidth: 10,
                strokeLinejoin: 'round'
            }
        }
    }, {
        markup: [{
            tagName: 'path',
            selector: 'wrapper',
            attributes: {
                'fill': 'none',
                'cursor': 'grab',
                'stroke': 'transparent'
            }
        }, {
            tagName: 'path',
            selector: 'line',
            attributes: {
                'fill': 'none',
                'pointer-events': 'none'
            }
        }]
    });

    link.router({ name: 'manhattan' });
    return link;
}

/** @deprecated */
export function getLinkHasTypeDefinition() {
    return createDefaultLink(legacyLinkTypes["HasTypeDefinition"], 'black');
}

/** @deprecated */
export function getLinkHasSubType() {
    return createDefaultLink(legacyLinkTypes["HasSubtype"], 'black');
}

export const markers = {
    singleLine: { d: 'M 10 -5 L 10 5', colorAttributes: ['stroke'] },
    doubleLine: { d: 'M 10 -5 L 10 5 M 15 -5 L 15 5', colorAttributes: ['stroke'] },
    singleArrowFilled: { d: 'M 10 -5 0 0 10 5 Z', colorAttributes: ['fill', 'stroke'] },
    singleArrowOutline: { d: 'M 10 -5 0 0 10 5 Z', fill: 'white', colorAttributes: ['stroke'] },
    doubleArrowOutline: { d: 'M 10 -5 0 0 10 5 Z M 21 -5 11 0 21 5 Z', fill: 'white', colorAttributes: ['stroke'] },
    doubleArrowFilled: { d: 'M 10 -5 0 0 10 5 Z M 21 -5 11 0 21 5 Z', colorAttributes: ['fill', 'stroke'] },
} as Record<string, MarkerProps>

type LinkType = {
    sourceMarker: MarkerProps | null,
    targetMarker: MarkerProps | null
}

/** @deprecated */
const legacyLinkTypes = {
    "HasComponent": { sourceMarker: null, targetMarker: markers.singleLine },
    "HasProperty": { sourceMarker: null, targetMarker: markers.doubleLine },
    "HasSubtype": { sourceMarker: markers.doubleArrowOutline, targetMarker: null },
    "HasTypeDefinition": { sourceMarker: null, targetMarker: markers.doubleArrowFilled },
    "HasEventSource": { sourceMarker: null, targetMarker: markers.singleArrowOutline },
    "HierarchicalReference": { sourceMarker: null, targetMarker: null },
    "AsymetricReference": { sourceMarker: null, targetMarker: markers.singleArrowFilled },
    "SymmetricReference": { sourceMarker: markers.singleArrowFilled, targetMarker: markers.singleArrowFilled },
    "GeneratesEvent": { sourceMarker: null, targetMarker: markers.singleArrowFilled },
}

const standardLinkTypes = [
    { referenceType: referenceTypes.hasComponent, sourceMarker: null, targetMarker: markers.singleLine },
    { referenceType: referenceTypes.hasProperty, sourceMarker: null, targetMarker: markers.doubleLine },
    { referenceType: referenceTypes.hasSubtype, sourceMarker: markers.doubleArrowOutline, targetMarker: null },
    { referenceType: referenceTypes.hasTypeDefinition, sourceMarker: null, targetMarker: markers.doubleArrowFilled },
    { referenceType: referenceTypes.hasEventSource, sourceMarker: null, targetMarker: markers.singleArrowOutline },
];

const defaultLinkTypes = {
    symmetric: { sourceMarker: markers.singleArrowFilled, targetMarker: markers.singleArrowFilled },
    asymmetric: { sourceMarker: null, targetMarker: markers.singleArrowFilled },
    hierarchical: { sourceMarker: null, targetMarker: null } // TODO
}

function getDefaultLinkType(referenceType: ReferenceTypeGraphNode) {
    if (referenceType.symmetric) {
        return defaultLinkTypes.symmetric;
    } else {
        return defaultLinkTypes.asymmetric;
    }

    // TODO handle hierarchical references
}

function createMarkerWithColor(marker: MarkerProps, color: string) {
    const coloredMarker = {
        ...markerDefaults,
        ...marker,
    }

    for (const attribute of marker.colorAttributes) {
        coloredMarker[attribute] = color;
    }

    return coloredMarker;
}

function createDefaultLink(linkType: LinkType, color: string) {
    const link = createLink(color);
    if (linkType.sourceMarker !== null) {
        link.attr({
            line: {
                sourceMarker: createMarkerWithColor(linkType.sourceMarker, color)
            }
        });
    } else {
        link.removeAttr("line/sourceMarker");
    }
    if (linkType.targetMarker !== null) {
        link.attr({
            line: {
                targetMarker: createMarkerWithColor(linkType.targetMarker, color)
            }
        });
    } else {
        link.removeAttr("line/targetMarker");
    }
    return link;
}

export type AnchorType = {
    direction: "top" | "right" | "bottom" | "left",
    offset: {
        dx: number | string,
        dy: number | string,
    },
}

export const anchorPositions = {
    top: {
        direction: "top",
        offset: { dx: 0, dy: 0 },
    },
    right: {
        direction: "right",
        offset: { dx: 0, dy: 0 },
    },
    bottomLeft: {
        direction: "bottom",
        offset: { dx: "-25%", dy: 0 },
    },
    bottomCenter: {
        direction: "bottom",
        offset: { dx: 0, dy: 0 },
    },
    bottomRight: {
        direction: "bottom",
        offset: { dx: "25%", dy: 0 },
    },
    left: {
        direction: "left",
        offset: { dx: 0, dy: 0 },
    },
} as const;

/**
 * @deprecated
 * Creates a new Link of the referenceType and sets the source and target of the link. 
 * @param {string} referenceType 
 * @param {node} source 
 * @param {node} target
 */
export function createAndConnectLink(referenceType: string, source: joint.dia.Cell, target: joint.dia.Cell) {
    const linkType = isObjKey(referenceType, legacyLinkTypes) ? legacyLinkTypes[referenceType] : legacyLinkTypes["AsymetricReference"];
    const link = createDefaultLink(linkType, 'black');

    link.router({ name: 'manhattan' });
    link.source(source);

    link.target(target, {
        selector: 'body',
        anchor: {
            name: 'left',
            args: {
                dx: 20,
            }
        }
    });

    return link;
}

export function createAndConnectLinkForRefGraphNode(referenceType: ReferenceTypeGraphNode, source: joint.dia.Cell, target: joint.dia.Cell, startAnchor: AnchorType, endAnchor: AnchorType, color: string) {
    const linkType = standardLinkTypes.find((linkType) => isNodeIdEqual(linkType.referenceType, referenceType.nodeId))
        ?? getDefaultLinkType(referenceType);
    return createAndConnectLinkForLinkType(linkType, source, target, startAnchor, endAnchor, color);
}

function createAndConnectLinkForLinkType(linkType: LinkType, source: joint.dia.Cell, target: joint.dia.Cell, startAnchor: AnchorType, endAnchor: AnchorType, color: string) {
    const link = createDefaultLink(linkType, color);

    link.router({ name: 'manhattan', args: { startDirections: [startAnchor.direction], endDirections: [endAnchor.direction], padding: 20 } });
    link.source(source, { anchor: { name: startAnchor.direction, args: { dx: startAnchor.offset.dx, dy: startAnchor.offset.dy } } });
    link.target(target, { anchor: { name: endAnchor.direction, args: { dx: endAnchor.offset.dx, dy: endAnchor.offset.dy } } });

    return link;
}