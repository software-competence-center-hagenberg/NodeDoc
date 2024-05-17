package at.scch.nodedoc;

import at.scch.nodedoc.nodeset.UANodeSet;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;

@Slf4j
public class XMLWriter {

    private final Transformer transformer;

    @SneakyThrows({TransformerConfigurationException.class})
    public XMLWriter() {
        var transformerFactory = TransformerFactory.newInstance();
        this.transformer = transformerFactory.newTransformer();
        this.transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    }

    public void writeXML(UANodeSet nodeSet, OutputStream outputStream) throws TransformerException {
        log.info("Write NodeSet {} as XML", nodeSet.getModelUriNoHttp());
        var source = new DOMSource(nodeSet.toXMLDocument());
        var result = new StreamResult(outputStream);
        transformer.transform(source, result);
    }
}
