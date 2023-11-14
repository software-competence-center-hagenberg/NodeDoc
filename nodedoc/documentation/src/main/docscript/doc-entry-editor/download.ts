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