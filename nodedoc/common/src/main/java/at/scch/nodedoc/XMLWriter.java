package at.scch.nodedoc;

import at.scch.nodedoc.nodeset.UANodeSet;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;

public class XMLWriter {

    private final Transformer transformer;

    public XMLWriter() {
        var transformerFactory = TransformerFactory.newInstance();
        try {
            this.transformer = transformerFactory.newTransformer();
            this.transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeXML(UANodeSet nodeSet, OutputStream outputStream) throws TransformerException {
        var source = new DOMSource(nodeSet.toXMLDocument());
        var result = new StreamResult(outputStream);
        transformer.transform(source, result);
    }
}
