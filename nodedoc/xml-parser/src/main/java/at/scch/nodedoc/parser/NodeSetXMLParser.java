package at.scch.nodedoc.parser;

import at.scch.nodedoc.parser.rawModel.RawNodeSet;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

public class NodeSetXMLParser {

    private final DocumentBuilder documentBuilder;

    public NodeSetXMLParser() {
        var documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public RawNodeSet parseXML(InputStream inputStream) throws IOException, SAXException {
        var document = documentBuilder.parse(inputStream);
        return new RawNodeSet(document.getDocumentElement());
    }

}
