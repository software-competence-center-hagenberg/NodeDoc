import $ from "jquery";

function removeAttrClasses(elements: JQuery<HTMLElement>) {
    elements.removeClass('original-xml-text');
    elements.removeClass('user-defined-text-from-base-version');
    elements.removeClass('user-defined-text');
    elements.removeClass('missing-text');
    elements.removeAttr("contenteditable");
    elements.css("resize", "none");
    elements.removeAttr("onfocus");
    elements.removeAttr("onblur");
    elements.removeAttr("resize");
}

const embeddedPngClass = "png";

function postConversionCleanup() {
    document.querySelectorAll("img").forEach(img => {
        if (img.classList.contains(embeddedPngClass)) {
            img.remove();
        }
    });
    document.querySelectorAll("svg").forEach(svg => {
        svg.style.display = "block";
    });
}

export async function performDownloadWithSvgToPngConversion(namespaceUri: string, version: string) {
    let remainingConversions = 0;
    document.querySelectorAll("svg").forEach(svg => {
        const targetImage = document.createElement("img");
        targetImage.className = embeddedPngClass;
        svg.after(targetImage);
        const canvas = document.createElement('canvas');
        const context = canvas.getContext('2d')!;
        const loader = new Image();

        loader.width = canvas.width = targetImage.width = svg.clientWidth;
        loader.height = canvas.height = targetImage.height = svg.clientHeight;
        loader.onload = () => {            
            context.drawImage(loader, 0, 0, loader.width, loader.height);
            targetImage.src = canvas.toDataURL();
            remainingConversions--;
            if (remainingConversions === 0) {
                performDownload(namespaceUri, version);
                postConversionCleanup();
            }
        }
        const svgAsXML = (new XMLSerializer).serializeToString(svg);
        loader.src = 'data:image/svg+xml,' + encodeURIComponent(svgAsXML);
        svg.style.display = "none";
        remainingConversions++;
    });
}

export function performDownload(namespaceUri: string, version: string) {
    const element = document.createElement('a');
    const exportHtml = document.getElementsByTagName('html')[0].cloneNode(true) as HTMLHtmlElement;
    $("button", exportHtml).remove();
    $("script", exportHtml).remove();
    $(".description-history", exportHtml).remove();
    $("#editor-root", exportHtml).remove();
    $(".template-chapter:has(.no-text-defined)", exportHtml).remove();
    removeAttrClasses($(".text-area", exportHtml));

    element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(exportHtml.innerHTML));
    element.setAttribute('download', `${document.title}_${namespaceUri}_${version}_${new Date().toISOString()}.html`);

    element.style.display = 'none';
    document.body.appendChild(element);

    element.click();

    document.body.removeChild(element);
}