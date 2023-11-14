import * as joint from "jointjs";
import $ from "jquery";
import svgPanZoom from "svg-pan-zoom";
import { MAX_GRAPH_HEIGHT, MAX_GRAPH_WIDTH, NODE_HEIGHT, NODE_WIDTH, SPLIT_TEXT_FONT_SIZE } from "./constants";
import { anchorPositions, AnchorType, configureLabel, createAndConnectLink, createAndConnectLinkForRefGraphNode, getLinkHasSubType, getLinkHasTypeDefinition, markers } from "./links";
import { ModelNode, ModelReference } from "./model";
import { createNode, createNodeForGraphNode, getNode } from "./nodes";
import { Direction, FilterMode, getNodeById, getReferencedNodes, getRootNode, getReferenceColorByName, GraphData, ResolvedGraphReference } from "./types/GraphData";
import { GraphNode, isInstance, isType, ReferenceTypeGraphNode } from "./types/GraphNode";
import { referenceTypes } from "./uastandard";
import { splitText, throwExpression } from "./utils";

type TypeDefinition = {
    nodes: Array<MagicNode>,
    links: Array<joint.dia.Link>,
    xPos: number,
    yPos: number,
    root: joint.dia.Element
};

type TypeNode = {
    node: joint.dia.Element,
    link: joint.dia.Link,
    xPos: number,
    yPos: number,
    name: string,
    subNodes: Array<MagicNode> | null
}

type MagicNode = {
    node: joint.dia.Element,
    x: number,
    y: number
}

/**
 * @deprecated
 * For each referenced node in refInfos a new JointJs Element is created. If 
 * type nodes for the referenced nodes have been created, they are positioned
 * according to the root nodes position.
 * @param {node} rootNode 
 * @param {JSON-array} refInfos 
 * @param {number} xPos 
 * @param {number} yPos 
 */
function drawRefInfos(rootNode: joint.dia.Cell, refInfos: Array<ModelReference>, xPos: number, yPos: number) {
    var maxWidth = NODE_WIDTH;
    var rightMostPoint = 0;
    var nodes: Array<MagicNode> = [];
    var links: Array<joint.dia.Link> = [];

    var typeNodes: Array<TypeNode> = [];

    if (refInfos != null) {
        refInfos.forEach(element => {

            var refInfo = drawRefInfo(rootNode, element, typeNodes, xPos, yPos);

            maxWidth = Math.max(maxWidth, refInfo.rootWidth);
            nodes.push({ node: refInfo.node, x: xPos, y: yPos });

            links = links.concat(refInfo.links);
            typeNodes = refInfo.typeNodes;

            yPos = refInfo.yPos + NODE_HEIGHT + 5;
        });
        yPos += 10;

        rightMostPoint = xPos + maxWidth + 70;

        typeNodes.forEach(tn => {
            links.push(tn.link);
            rightMostPoint = Math.max(rightMostPoint, tn.xPos + maxWidth + (tn.node.get('size')?.width ?? 0) + 80); // TODO: remove ?. and ??
            if (!tn.subNodes) {
                nodes.push({ node: tn.node, x: tn.xPos + maxWidth + 70, y: tn.yPos });
            } else {
                nodes = nodes.concat(tn.subNodes);
                tn.subNodes.forEach(element => {
                    element.x += maxWidth + 70;
                    rightMostPoint = Math.max(rightMostPoint, element.x + (element.node.get('size')?.width ?? 0) + 70); // TODO: remove ?. and ??
                });
            }
        });
    }
    return { nodes: nodes, links: links, xPos: rightMostPoint, yPos: yPos };
}

/**
 * @deprecated
 * For the refInfo object a new node is created and gets a link to the root node.
 * If a node has a data type or a type definition the according nodes and links 
 * are also created. In case of a type definition a recursive call is made to the
 * drawTypeDefinition function, which creates the subgraph for the node. 
 * @param {node} rootNode 
 * @param {JSON-object} refInfo 
 * @param {node[]} typeNodes 
 * @param {number} xPos 
 * @param {number} yPos 
 */
function drawRefInfo(rootNode: joint.dia.Cell, refInfo: ModelReference, typeNodes: Array<TypeNode>, xPos: number, yPos: number) {
    var text = splitText(refInfo.BrowseName);
    var node = getNode(refInfo.NodeClass, text.label, refInfo.isAbstract, refInfo.link);
    if (text.split)
        node.attr('.label/fontSize', SPLIT_TEXT_FONT_SIZE);
    var links = [];

    var link = createAndConnectLink(refInfo.ReferenceType, rootNode, node);

    link.router('manhattan', {
        startDirections: ['bottom'],
        endDirections: ['left']
    });

    link.source(rootNode, {
        selector: 'body',
        anchor: {
            name: 'bottom',
            args: {
                dx: '-5%',
                dy: 0
            }
        }
    });
    links.push(link);


    if (refInfo.DataType != null) {
        let found = typeNodes.find(e => e.name == refInfo.DataType.BrowseName);
        if (found) {
            links.push(getLinkHasTypeDefinition().source(node).target(found.node));
        } else {
            var dt = drawDataType(node, refInfo.DataType, xPos, yPos);
            typeNodes.push(dt);
        }
    }
    if (refInfo.TypeDefinition != null) {
        const found = typeNodes.find(e => e.name === refInfo.TypeDefinition.BrowseName);
        var tdLink = getLinkHasTypeDefinition();
        tdLink.source(node);

        if (found == null) {
            var td = drawTypeDefinition(refInfo.TypeDefinition, xPos, yPos);
            tdLink.target(td.root);
            if (refInfo.link)
                td.root.attr('.link/xlinkHref', '#' + refInfo.link.replace("&amp;lt;", "&lt;").replace("&gt;", ">"));

            links = links.concat(td.links);
            typeNodes.push({
                node: td.root,
                link: tdLink,
                xPos: xPos + 20,
                yPos: yPos,
                name: refInfo.TypeDefinition.BrowseName,
                subNodes: td.nodes
            });
            yPos = td.yPos - NODE_HEIGHT;
        } else {
            tdLink.target(found.node);
            links.push(tdLink);
        }
    }

    return { node: node, rootWidth: text.size, links: links, typeNodes: typeNodes, yPos: yPos };
}

/**
 * @deprecated
 * This function creates the basic structure for a type definition. At first the 
 * root node is created and its base type, if one is defined and its label does 
 * not start with '0:Base'. The link between the root node and the base type node 
 * is also created. Then the references, subtypes and generated events are added
 * with their connection to the root node and their referenced nodes. 
 * @param {JSON-object} typeDef 
 * @param {number} xPos 
 * @param {number} yPos 
 * @param {boolean} drawBaseType 
 */
function drawTypeDefinition(typeDef: ModelNode, xPos: number, yPos: number, drawBaseType = true): TypeDefinition {
    var nodes: Array<MagicNode> = [];
    var links: Array<joint.dia.Link> = [];

    var text = splitText(typeDef.BrowseName);
    var root = getNode(typeDef.NodeClass, text.label, typeDef.isAbstract, typeDef.link);

    if (text.split)
        root.attr('.label/fontSize', SPLIT_TEXT_FONT_SIZE);
    var rootWidth = text.size;


    if (drawBaseType && typeDef.BaseType != null && !typeDef.BaseType.BrowseName.startsWith('0:Base')) {
        const btDef = typeDef.BaseType;
        text = splitText(btDef.BrowseName);
        rootWidth = Math.max(rootWidth, text.size);
        var baseType = getNode(btDef.NodeClass, text.label, btDef.isAbstract, btDef.link);
        if (text.split)
            baseType.attr('.label/fontSize', SPLIT_TEXT_FONT_SIZE);
        nodes.push({ node: baseType, x: xPos, y: yPos });

        var btLink = getLinkHasSubType()
            .source(baseType, {
                selector: 'body',
                anchor: {
                    name: 'bottom',
                    args: {
                        dx: '-5%',
                        dy: 0
                    }
                }
            }).target(root, {
                selector: 'body',
                anchor: {
                    name: 'top',
                    args: {
                        dx: '-5%',
                        dy: -1
                    }
                }
            });
        links.push(btLink);
        yPos += 90;
    }
    nodes.push({ node: root, x: xPos, y: yPos });

    var startYPos = yPos;
    var rightMostPoint = 0;

    // references
    yPos += NODE_HEIGHT + 20;
    xPos += rootWidth / 2 + 40;

    var references = drawRefInfos(root, typeDef.refInfos, xPos, yPos);
    nodes = nodes.concat(references.nodes);
    links = links.concat(references.links);

    var bottomPoint = Math.max(yPos, references.yPos);
    yPos = references.yPos;
    xPos = Math.max(xPos, references.xPos) + 20;

    // subtypes
    if (typeDef.HasSubtype != null) {
        var tmpYPos = startYPos;
        var td: TypeDefinition;
        typeDef.HasSubtype.forEach(st => {

            td = drawTypeDefinition(st, xPos, tmpYPos, false);
            nodes = nodes.concat(td.nodes);
            links = links.concat(td.links);

            var stLink = getLinkHasSubType()
                .source(root, {
                    selector: 'body',
                    anchor: {
                        name: 'right',
                    }
                }).target(td.root, {
                    selector: 'body',
                    anchor: {
                        name: 'left'
                    }
                });

            stLink.router('manhattan', {
                startDirections: ['right'],
                endDirections: ['left']
            });
            tmpYPos = td.yPos + 10;
            rightMostPoint = Math.max(rightMostPoint, td.xPos);
            links.push(stLink);
        });
        bottomPoint = Math.max(bottomPoint, td!.yPos); // TODO: remove !
        startYPos = Math.max(tmpYPos, startYPos);
    }
    // generates event
    if (typeDef.generatesEvent != null) {
        var tmpYPos = startYPos;
        var first = true;
        typeDef.generatesEvent.forEach(event => {

            var td = drawTypeDefinition(event, xPos, tmpYPos, true);
            nodes = nodes.concat(td.nodes);
            links = links.concat(td.links);

            var eventLink = createAndConnectLink('GeneratesEvent', root, td.root);  // TODO: Fix nulls

            if (first) {
                configureLabel(eventLink, 'GeneratesEvent');
                first = false;
            }

            eventLink.router('manhattan', {
                startDirections: ['right'],
                endDirections: ['left'],
                perpendicular: false
            });
            tmpYPos = td.yPos + 10;
            rightMostPoint = Math.max(rightMostPoint, td.xPos);
            links.push(eventLink);
            bottomPoint = Math.max(bottomPoint, td.yPos);
        });
    }
    return { nodes: nodes, links: links, xPos: Math.max(rightMostPoint, xPos), yPos: Math.max(yPos, bottomPoint), root: root };
}

function drawDataType(rootNode: joint.dia.Cell, dt: ModelNode, xPos: number, yPos: number): TypeNode {
    var text = splitText(dt.BrowseName);
    var dtNode = getNode(dt.NodeClass, text.label, dt.isAbstract, dt.link);

    var dtLink = getLinkHasTypeDefinition();
    dtLink.source(rootNode);
    dtLink.target(dtNode);

    return { name: dt.BrowseName, node: dtNode, link: dtLink, xPos: xPos, yPos: yPos, subNodes: null };
}


/**
 * @deprecated
 * A new graph and paper gets created. All nodes and links are created, positioned and
 * added to the graph. If the graph is to broad or long it is scaled to the maximum height
 * or width. In this function the pan and zoom functionalities are also defined.
 * @param {string} selector
 * @param {JSON-object} graphData
 */
export function getOPCUAGraph(selector: string, graphData: ModelNode) {
    var graph = new joint.dia.Graph();
    var paper = new joint.dia.Paper({
        el: document.getElementById(selector)!, // TODO: remove !
        model: graph,
        interactive: false
    });

    var content = drawTypeDefinition(graphData, 50, 10);

    content.nodes.forEach(element => {
        element.node.position(element.x, element.y);
        element.node.addTo(graph);
    });
    graph.addCells(content.links);

    const scaleX = content.xPos > MAX_GRAPH_WIDTH ? MAX_GRAPH_WIDTH / content.xPos : 1;
    const scaleY = content.yPos > MAX_GRAPH_HEIGHT ? MAX_GRAPH_HEIGHT / content.yPos : 1;

    const scale = Math.max(0.4, Math.min(scaleX, scaleY));
    paper.scale(scale, scale);

    paper.fitToContent({ padding: { top: 150, bottom: 150 } });

    // pan and zoom
    var targetElement = $('#' + selector)[0];
    targetElement.style.width = '100%';
    targetElement.style.minWidth = '1000px';
    targetElement.style.border = '1px solid grey';

    var panAndZoom = svgPanZoom(targetElement.childNodes[2] as HTMLElement, { // TODO: check "as HTMLElement"
        viewportSelector: targetElement.childNodes[0].childNodes[0] as HTMLElement, // TODO: check "as HTMLElement"
        fit: false,
        zoomScaleSensitivity: 0.2,
        panEnabled: true,
        controlIconsEnabled: false,
        zoomEnabled: false,
        customEventsHandler: {
            init: function (options: any) { // TODO: options???

                $(window).keydown(function (e) {
                    if (e.key === 'Control')
                        options.instance.enableZoom();
                    else
                        options.instance.disableZoom();
                });

                $(window).keyup(function (e) {
                    options.instance.disableZoom();
                });
            },
            haltEventListeners: [],
            destroy: function (options: any) { // TODO: options???
                // for (var eventName in this.listeners) {
                //     options.svgElement.removeEventListener(eventName, this.listeners[eventName])
                // }
            }
        }
    });

    paper.on('blank:mouseover', function (event: joint.dia.Event) {
        // cellView.target.style.cursor = 'grab';
        event.target.style.cursor = 'grab'; // TODO: check if it works
    });

    //Enable pan when a blank area is click (held) on
    paper.on('cell:pointerdown blank:pointerdown', function (cellView: joint.dia.CellView, evt: joint.dia.Event, x: number, y: number) {
        panAndZoom.enablePan();
    });

    //Disable pan when the mouse button is released
    paper.on('cell:pointerup blank:pointerup', function (cellView: joint.dia.CellView, event: joint.dia.Event) {
        panAndZoom.disablePan();
    });
}

export function drawOPCUAGraphHere(data: GraphData) {
    const scriptTag = document.currentScript ?? throwExpression("Script tag should not be null");
    const div = document.createElement("div");
    div.style.border = "1px solid grey";
    scriptTag.parentNode!.insertBefore(div, scriptTag);
    const { graph, paper } = setupGraphAndPaper(div);
    drawNodes(data, graph);
    paper.fitToContent({ padding: 200 });
    setupPanZoom(paper, div);
}

function setupGraphAndPaper(element: HTMLElement) {
    const graph = new joint.dia.Graph();
    const paper = new joint.dia.Paper({
        el: element,
        model: graph,
        interactive: false
    });

    return { graph, paper };
}

function setupPanZoom(paper: joint.dia.Paper, element: HTMLElement) {
    const svg = element.querySelector("svg") as SVGElement;
    const panAndZoom = svgPanZoom(svg, {
        zoomScaleSensitivity: 0.2,
        fit: false,
        zoomEnabled: false,
        panEnabled: false,
        customEventsHandler: {
            init: (options) => {
                window.addEventListener("keydown", (event) => {
                    if (event.key === "Control") {
                        options.instance.enableZoom();
                        element.style.cursor = 'zoom-in';
                    }
                });
                window.addEventListener("keyup", (event) => {
                    if (event.key === "Control") {
                        options.instance.disableZoom();
                    }
                });
            },
            haltEventListeners: [],
            destroy: () => {}
        }
    });

    paper.on('blank:mouseover', (event: joint.dia.Event) => {
        event.target.style.cursor = 'grab';        
    });

    //Enable pan when a blank area is click (held) on
    paper.on('blank:pointerdown', (evt: joint.dia.Event, x: number, y: number) => {
        panAndZoom.enablePan();
    });

    //Disable pan when the mouse button is released
    paper.on('blank:pointerup', (event: joint.dia.Event) => {
        panAndZoom.disablePan();
    });
}

function drawNodes(data: GraphData, graph: joint.dia.Graph) {
    const rootElement = drawRootNode(data, graph);
    const baseTypes = getReferencedNodes(data, data.root, Direction.AsymmetricBackward, { referenceTypes: { mode: FilterMode.Include, nodeIds: [referenceTypes.hasSubtype] } })
    drawAndConnectGeneric(rootElement, baseTypes, graph, (ref) => ref.source.browseName, createAndAddReferencedSourceNode, (refType, source, target, color) => createAndConnectLinkForRefGraphNode(refType, target, source, anchorPositions.bottomCenter, anchorPositions.top, color), { x: 0, y: -0.1 }, data, (p) => ({ ...p, x: p.x + 1 }));

    const initialRefNodeMultiplicator = 4;
    let p = { x: 0, y: initialRefNodeMultiplicator };

    const nextAnchors = [anchorPositions.bottomRight, anchorPositions.right];
    let currentAnchor: AnchorType = anchorPositions.bottomLeft;
    function nextAnchorPosition() {
        currentAnchor = nextAnchors.shift() ?? throwExpression("should never happen");
    }

    const referencedInstances = getReferencedNodes(data, data.root, Direction.AsymmetricForward, { referencedNodePredicate: isInstance })
    p = drawAndConnectGeneric(rootElement, referencedInstances, graph, (ref) => ref.target.browseName, createAndAddReferencedTargetNode, (refType, source, target, color) => createAndConnectLinkForRefGraphNode(refType, source, target, currentAnchor, anchorPositions.left, color), p, data);

    p.y = initialRefNodeMultiplicator;
    const referencedTypes = getReferencedNodes(data, data.root, Direction.AsymmetricForward, { referencedNodePredicate: isType, referenceTypes: { mode: FilterMode.Exclude, nodeIds: [referenceTypes.hasSubtype] } })
    p = drawAndConnectGeneric(rootElement, referencedTypes, graph, (ref) => ref.target.browseName, createAndAddReferencedTargetNode, (refType, source, target, color) => createAndConnectLinkForRefGraphNode(refType, source, target, currentAnchor, anchorPositions.left, color), p, data);

    let verticesForSymRefs: joint.dia.Link.Vertex[] = [];
    if (referencedInstances.length > 0 || referencedTypes.length > 0) {
        nextAnchorPosition();
        verticesForSymRefs = [{ x: NODE_WIDTH, y: 4.5 * NODE_HEIGHT }];
    }

    p.y = initialRefNodeMultiplicator;
    const symRefs = getReferencedNodes(data, data.root, Direction.Symmetric);
    p = drawAndConnectGeneric(rootElement, symRefs, graph, (ref) => ref.target.browseName, createAndAddReferencedTargetNode, (refType, source, target, color) => createAndConnectLinkForRefGraphNode(refType, source, target, currentAnchor, anchorPositions.left, color).vertices(verticesForSymRefs), p, data);

    if (symRefs.length > 0) {
        nextAnchorPosition();
    }

    let verticesForSubtypes: joint.dia.Link.Vertex[] = [];
    if ((referencedInstances.length > 0 || referencedTypes.length > 0) !== symRefs.length > 0) {
        verticesForSubtypes = [{ x: NODE_WIDTH, y: 4.5 * NODE_HEIGHT }];
    }

    p.y = initialRefNodeMultiplicator;
    const subtypes = getReferencedNodes(data, data.root, Direction.AsymmetricForward, { referenceTypes: { mode: FilterMode.Include, nodeIds: [referenceTypes.hasSubtype] } })
    p = drawAndConnectGeneric(rootElement, subtypes, graph, (ref) => ref.target.browseName, createAndAddReferencedTargetNode, (refType, source, target, color) => createAndConnectLinkForRefGraphNode(refType, source, target, currentAnchor, anchorPositions.left, color).vertices(verticesForSubtypes), p, data);

}

function drawRootNode(data: GraphData, graph: joint.dia.Graph) {
    const rootNode = getRootNode(data);
    const element = createNodeForGraphNode(rootNode, data);
    element.position(0, 2.5 * NODE_HEIGHT);
    element.addTo(graph);
    return element;
}

function createAndAddReferencedSourceNode(refData: ResolvedGraphReference, graph: joint.dia.Graph, posMultiplicator: Point, data: GraphData) {
    const node = createNodeForGraphNode(refData.source, data);
    node.addTo(graph);
    node.position(posMultiplicator.x * (NODE_WIDTH * 1.5), posMultiplicator.y * (NODE_HEIGHT * 1.5));
    return { referencedNode: node, posMultiplicator };
}

function createAndAddReferencedTargetNode(refData: ResolvedGraphReference, graph: joint.dia.Graph, posMultiplicator: Point, data: GraphData) {
    const node = createNodeForGraphNode(refData.target, data);
    const currPosMult = { ...posMultiplicator };
    node.addTo(graph);
    node.position((NODE_WIDTH * 0.5) + posMultiplicator.x * (NODE_WIDTH * 1.5), posMultiplicator.y * (NODE_HEIGHT * 1.5));
    if (data && (refData.target.nodeClass === "UAObject" || refData.target.nodeClass === "UAVariable")) {
        const typeDefs = getReferencedNodes(data, refData.target.nodeId, Direction.AsymmetricForward, { referenceTypes: { mode: FilterMode.Include, nodeIds: [referenceTypes.hasTypeDefinition] } });
        typeDefs.forEach(reference => {
            const tdNode = createNodeForGraphNode(reference.target, data);
            tdNode.addTo(graph);
            tdNode.position((NODE_WIDTH * 0.5) + (currPosMult.x + 1) * (NODE_WIDTH * 1.5), currPosMult.y * (NODE_HEIGHT * 1.5));
            const referenceColor = getReferenceColorByName(data, reference.color) ?? throwExpression(`Reference color ${refData.color} not found`);
            createAndConnectLinkForRefGraphNode(reference.referenceType, node, tdNode, anchorPositions.right, anchorPositions.left, referenceColor).addTo(graph);
            currPosMult.y += 1;
        });
        if (typeDefs.length > 0) {
            currPosMult.y -= 1;
        }
        currPosMult.x += 1;

    }
    return { referencedNode: node, posMultiplicator: { ...currPosMult, x: currPosMult.x + 1 } };
}

type Point = {
    x: number,
    y: number,
}

type NodeCreatorFunction = (node: ResolvedGraphReference, graph: joint.dia.Graph, posMultiplicator: Point, data: GraphData) => { referencedNode: joint.dia.Element, posMultiplicator: Point };
type LinkCreatorFunction = (referenceType: ReferenceTypeGraphNode, source: joint.dia.Cell, target: joint.dia.Cell, color: string) => joint.shapes.standard.Link;


function drawAndConnectGeneric(
    rootElement: joint.dia.Element,
    references: ResolvedGraphReference[],
    graph: joint.dia.Graph,
    referenceSortTextExtractor: (reference: ResolvedGraphReference) => string,
    createAndAddNode: NodeCreatorFunction,
    createAndConnectLink_: LinkCreatorFunction,
    posMultiplicator: Point,
    data: GraphData,
    getNextPosMult: (current: Point) => Point = (point) => ({ ...point, y: point.y + 1 })) {
    const currPosMult = { ...posMultiplicator };

    references.sort((a, b) => referenceSortTextExtractor(a) > referenceSortTextExtractor(b) ? 1 : -1); // TODO
    references.forEach(refData => {
        const refNode = createAndAddNode(refData, graph, posMultiplicator, data);
        posMultiplicator.y = refNode.posMultiplicator.y;
        currPosMult.x = Math.max(currPosMult.x, refNode.posMultiplicator.x);
        currPosMult.y = Math.max(currPosMult.y, refNode.posMultiplicator.y);
        const referenceColor = getReferenceColorByName(data, refData.color) ?? throwExpression(`Reference color ${refData.color} not found`);
        createAndConnectLink_(refData.referenceType, rootElement, refNode.referencedNode, referenceColor).addTo(graph);
        posMultiplicator = getNextPosMult(posMultiplicator);
    })
    return currPosMult;
}