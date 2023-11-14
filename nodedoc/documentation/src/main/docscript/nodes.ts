import * as joint from "jointjs";
import { NODE_BACKGROUND_COLOR, NODE_HEIGHT, NODE_SHADOW_COLOR, NODE_SHADOW_OFFSET, NODE_WIDTH } from "./constants";
import { getNodeColorByName, GraphData, NodeColor } from "./types/GraphData";
import { GraphNode } from "./types/GraphNode";
import { throwExpression } from "./utils";

 type Rectangle = {
    x: number,
    y: number,
    width: number,
    height: number,
}

function createGenericNode(labelMarkup: string, italicLabel: boolean, labelBoundingBox: Rectangle, tooltipText: string, linkTarget: string, visualMarkup: string, color: NodeColor) { // TODO: handle linkTarget === null
    const textBoxMarkup = `
        <foreignObject x="${labelBoundingBox.x}" y="${labelBoundingBox.y}" width="${labelBoundingBox.width}" height="${labelBoundingBox.height}" requiredExtensions="http://www.w3.org/1999/xhtml">
            <div class="graph-node-container" xmlns="http://www.w3.org/1999/xhtml">
                <div class="graph-node-label">
                    ${labelMarkup}
                </div>
            </div>
        </foreignObject>`;
    
    const element = new joint.shapes.basic.Generic({
        size: {
            width: NODE_WIDTH,
            height: NODE_HEIGHT,
        },
        attrs: {
            'title': {
                text: tooltipText,
            },
            ...(linkTarget !== '#' ? { 'a': {
                xlinkHref: linkTarget
            }} : {}),
            '.node': {
                fill: color.backgroundColor,
                stroke: '#000000',
                strokeWidth: 1.5,
            },
            '.shadow': {
                fill: color.shadowColor,
                strokeWidth: 0,
            }
        },
        markup: `<a><g class="rotatable ${italicLabel ? 'italic' : ''}"><g class="scalable">${visualMarkup}${textBoxMarkup}<title/></g></g></a>`
    });

    return element;
}

function getElementObject(labelMarkup: string, linkTarget: string, tooltipText: string, color: NodeColor) {
    const element = createGenericNode(labelMarkup, false, {x: 0, y: 0, width: NODE_WIDTH, height: NODE_HEIGHT}, tooltipText, linkTarget, '<rect class="node"/>', color);
    element.attr({
        'rect': {
            width: NODE_WIDTH,
            height: NODE_HEIGHT,
        }
    });

    return element;
}

function getElementObjectType(labelMarkup: string, linkTarget: string, tooltipText: string, color: NodeColor) {
    const rectHeight = NODE_HEIGHT - NODE_SHADOW_OFFSET;
    const rectWidth = NODE_WIDTH - NODE_SHADOW_OFFSET;

    const element = createGenericNode(labelMarkup, true, {x: 0, y: 0, width: rectWidth, height: rectHeight}, tooltipText, linkTarget, '<rect class="shadow"/><rect class="node"/>', color);
    element.attr({
        '.node': {
            width: rectWidth,
            height: rectHeight,
        },
        '.shadow': {
            width: rectWidth,
            height: rectHeight,
            x: NODE_SHADOW_OFFSET,
            y: NODE_SHADOW_OFFSET,
        }
    });

    return element;
}

function getElementVariable(labelMarkup: string, linkTarget: string, tooltipText: string, color: NodeColor) {
    const element = createGenericNode(labelMarkup, false, {x: 0, y: 0, width: NODE_WIDTH, height: NODE_HEIGHT}, tooltipText, linkTarget, '<rect class="node"/>', color);
    element.attr({
        'rect': {
            width: NODE_WIDTH,
            height: NODE_HEIGHT,
            rx: 5,
        }
    });

    return element;
}

function getElementVariableType(labelMarkup: string, linkTarget: string, tooltipText: string, color: NodeColor) {
    const rectHeight = NODE_HEIGHT - NODE_SHADOW_OFFSET;
    const rectWidth = NODE_WIDTH - NODE_SHADOW_OFFSET;

    const element = createGenericNode(labelMarkup, true, {x: 0, y: 0, width: rectWidth, height: rectHeight}, tooltipText, linkTarget, '<rect class="shadow"/><rect class="node"/>', color);
    element.attr({
        '.node': {
            width: rectWidth,
            height: rectHeight,
            rx: 5,
        },
        '.shadow': {
            width: rectWidth,
            height: rectHeight,
            x: NODE_SHADOW_OFFSET,
            y: NODE_SHADOW_OFFSET,
            rx: 5,
        }
    });

    return element;
}

function getElementDataType(labelMarkup: string, linkTarget: string, tooltipText: string, color: NodeColor) {
    const polygonHeight = NODE_HEIGHT - NODE_SHADOW_OFFSET;
    const polygonWidth = NODE_WIDTH - NODE_SHADOW_OFFSET;

    /*    1-----------2
         /             \
        0               3
         \             /
          5-----------4
    */
    const polygonPathPoints = [
        [0, polygonHeight/2],
        [NODE_SHADOW_OFFSET/2, 0],
        [polygonWidth-NODE_SHADOW_OFFSET/2, 0],
        [polygonWidth, polygonHeight/2],
        [polygonWidth-NODE_SHADOW_OFFSET/2, polygonHeight],
        [NODE_SHADOW_OFFSET/2, polygonHeight]
    ]
    const mainPolygonPath = polygonPathPoints.map(p => `${p[0]},${p[1]}`).join(' ');
    const shadowPolygonPath = polygonPathPoints.map(p => `${p[0]+NODE_SHADOW_OFFSET},${p[1]+NODE_SHADOW_OFFSET}`).join(' ');

    const element = createGenericNode(labelMarkup, true, {x: 0, y: 0, width: polygonWidth, height: polygonHeight}, tooltipText, linkTarget, 
    `<polygon class="shadow" points="${shadowPolygonPath}"/><polygon class="node" points="${mainPolygonPath}"/>`, color);

    return element;
}

function getElementReferenceType(labelMarkup: string, linkTarget: string, tooltipText: string, color: NodeColor) {
    const polygonHeight = NODE_HEIGHT - NODE_SHADOW_OFFSET;
    const polygonWidth = NODE_WIDTH - NODE_SHADOW_OFFSET;

    /*    1-----------2
         /           /
        0           3
         \           \
          5-----------4
    */
    const polygonPathPoints = [
        [0, polygonHeight/2],
        [NODE_SHADOW_OFFSET/2, 0],
        [polygonWidth, 0],
        [polygonWidth-NODE_SHADOW_OFFSET/2, polygonHeight/2],
        [polygonWidth, polygonHeight],
        [NODE_SHADOW_OFFSET/2, polygonHeight]
    ]
    const mainPolygonPath = polygonPathPoints.map(p => `${p[0]},${p[1]}`).join(' ');
    const shadowPolygonPath = polygonPathPoints.map(p => `${p[0]+NODE_SHADOW_OFFSET},${p[1]+NODE_SHADOW_OFFSET}`).join(' ');

    const element = createGenericNode(labelMarkup, true, {x: 0, y: 0, width: polygonWidth, height: polygonHeight}, tooltipText, linkTarget, 
    `<polygon class="shadow" points="${shadowPolygonPath}"/><polygon class="node" points="${mainPolygonPath}"/>`, color);

    return element;
}

function getElementMethod(labelMarkup: string, linkTarget: string, tooltipText: string, color: NodeColor) {
    const pathPoints = [
        `0,${NODE_HEIGHT/2}`,
        `${NODE_WIDTH/2},0`,
        `${NODE_WIDTH},${NODE_HEIGHT/2}`,
        `${NODE_WIDTH/2},${NODE_HEIGHT}`,
    ]

    const controlPointXFraction = 1;
    const controlPointYFraction = 0.3;

    const controlPointX1 = NODE_WIDTH / 2 - NODE_WIDTH / 2 * controlPointXFraction;
    const controlPointX2 = NODE_WIDTH / 2 + NODE_WIDTH / 2 * controlPointXFraction;

    const controlPointY1 = NODE_HEIGHT / 2 - NODE_HEIGHT / 2 * controlPointYFraction;
    const controlPointY2 = NODE_HEIGHT - controlPointY1;

    const element = createGenericNode(labelMarkup, false, {x: NODE_SHADOW_OFFSET, y: 0, width: NODE_WIDTH-2*NODE_SHADOW_OFFSET, height: NODE_HEIGHT}, tooltipText, linkTarget, 
    `<path class="node" d="M ${pathPoints[0]} 
                        C 0,${controlPointY1} ${controlPointX1},0 ${pathPoints[1]} 
                        ${controlPointX2},0 ${NODE_WIDTH},${controlPointY1} ${pathPoints[2]} 
                        ${NODE_WIDTH},${controlPointY2} ${controlPointX2},${NODE_HEIGHT} ${pathPoints[3]} 
                        ${controlPointX1},${NODE_HEIGHT} 0,${controlPointY2} ${pathPoints[0]}"/>`, color);

    return element;
}

function getElementView(labelMarkup: string, linkTarget: string, tooltipText: string, color: NodeColor) {

    /*    1-----------2
         /             \
        /               \
       0-----------------3
    */
    const polygonPathPoints = [
        [0, NODE_HEIGHT],
        [NODE_SHADOW_OFFSET, 0],
        [NODE_WIDTH-NODE_SHADOW_OFFSET, 0],
        [NODE_WIDTH, NODE_HEIGHT],
    ]
    const polygonPath = polygonPathPoints.map(p => `${p[0]},${p[1]}`).join(' ');

    const element = createGenericNode(labelMarkup, false, {x: 0, y: 0, width: NODE_WIDTH, height: NODE_HEIGHT}, tooltipText, linkTarget, 
    `<polygon class="node" points="${polygonPath}"/>`, color);

    return element;
}

/**
 * @deprecated
 * Creates a new JointJs element according to the nodeClass and sets the label. 
 * Also the label-style is set italic if the nodeClass is a UAType and isAbstract is true. 
 * @param {string} nodeClass 
 * @param {string} label 
 * @param {boolean} isAbstract 
 */
 export function getNode(nodeClass: string, label: string, isAbstract: boolean, link: string) {
    return createNode(nodeClass, label, link, label, {name: "", backgroundColor: NODE_BACKGROUND_COLOR, shadowColor: NODE_SHADOW_COLOR});
}

export function createNodeForGraphNode(node: GraphNode, data: GraphData) {
    const color = getNodeColorByName(data, node.color) ?? throwExpression(`Color ${node.color} not found`)
    return createNode(node.nodeClass, node.browseName, node.chapterAnchor, node.tooltipText, color);
}

export function createNode(nodeClass: string, labelMarkup: string, linkTarget: string, tooltipText: string, color: NodeColor) {
    var node;
    // TODO: remove escaping
    if (linkTarget) {
        linkTarget = linkTarget.replace("&amp;lt;", "&lt;").replace("&gt;", ">");
    }
    linkTarget = '#' + linkTarget;
    switch (nodeClass) {
        case "UAObject":
            node = getElementObject(labelMarkup, linkTarget, tooltipText, color);
            break;
        case "UAObjectType":
            node = getElementObjectType(labelMarkup, linkTarget, tooltipText, color);
            break;
        case "UAVariable":
            node = getElementVariable(labelMarkup, linkTarget, tooltipText, color);
            break;
        case "UAVariableType":
            node = getElementVariableType(labelMarkup, linkTarget, tooltipText, color);
            break;
        case "UADataType":
            node = getElementDataType(labelMarkup, linkTarget, tooltipText, color);
            break;
        case "UAReferenceType":
            node = getElementReferenceType(labelMarkup, linkTarget, tooltipText, color);
            break;
        case "UAMethod":
            node = getElementMethod(labelMarkup, linkTarget, tooltipText, color);
            break;
        case "UAView":
            node = getElementView(labelMarkup, linkTarget, tooltipText, color);
            break;
        default:
            node = getElementObject(labelMarkup, linkTarget, tooltipText, color);
    }

    return node;
}
