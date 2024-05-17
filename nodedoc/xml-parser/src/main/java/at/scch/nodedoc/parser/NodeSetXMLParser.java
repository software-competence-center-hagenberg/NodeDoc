package at.scch.nodedoc.parser;

import at.scch.nodedoc.parser.rawModel.RawNodeSet;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class NodeSetXMLParser {

    private final DocumentBuilder documentBuilder;
    private final SimpleNodeIdValidator simpleNodeIdValidator;

    public static final String XSD_PATH = "/at/scch/nodedoc/parser/UANodeSet.xsd";

    private static class NodeSetErrorHandler implements ErrorHandler {
        @Override
        public void warning(SAXParseException exception) throws SAXException {
            log.info("Encountered warning while parsing", exception);
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            log.info("Encountered error while parsing");
            throw new NodeSetSchemaValidationException(exception);
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            log.info("Encountered fatal error while parsing");
            throw new NodeSetSchemaValidationException(exception);
        }
    }

    public NodeSetXMLParser(SimpleNodeIdValidator simpleNodeIdValidator) {
        this.simpleNodeIdValidator = simpleNodeIdValidator;
        var documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        var schemaFactory = SchemaFactory.newDefaultInstance();

        try {
            var schema = schemaFactory.newSchema(new StreamSource(getClass().getResourceAsStream(XSD_PATH)));
            documentBuilderFactory.setSchema(schema);
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            documentBuilder.setErrorHandler(new NodeSetErrorHandler());
        } catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public RawNodeSet parseAndValidateXML(InputStream inputStream) throws IOException, SAXException {
        log.info("Parse and validate XML");
        var document = documentBuilder.parse(inputStream);
        var rawNodeSet = new RawNodeSet(document.getDocumentElement());
        simpleNodeIdValidator.validateOrThrow(rawNodeSet);
        return rawNodeSet;
    }

}
