import { Converter } from "showdown";

const converter = new Converter();
converter.setFlavor("github");
converter.setOption("tables", true);
converter.setOption("simpleLineBreaks", true);

export function convertToHTML(md: string, initialHeadingLevel: number) {
    converter.setOption("headerLevelStart", initialHeadingLevel);
    return converter.makeHtml(md);
}