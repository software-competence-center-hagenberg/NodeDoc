package at.scch.nodedoc.parser;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class NodeSetXMLParserValidationTest {

    @SneakyThrows
    private void parseAndValidateXML(String file) {
        var validator = new RawNodeSetValidator(List.of(
                new SimpleNodeIdValidator(),
                new ModelValidator(),
                new BrowseNameValidator()
        ));
        var parser = new NodeSetXMLParser(validator);
        parser.parseAndValidateXML(NodeSetXMLParserTest.class.getResourceAsStream("/nodesets/invalid/" + file));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "schema-error-1.xml",
            "schema-error-2.xml",
    })
    void testXMLError(String file) {
        assertThatThrownBy(() -> {
            parseAndValidateXML(file);
        }).isInstanceOf(NodeSetSchemaValidationException.class);
    }

    // these NodeSets contain "ns=12345678" in NodeIds
    @ParameterizedTest
    @ValueSource(strings = {
            "invalid-namespace-index-Alias.xml",
            "invalid-namespace-index-NodeId.xml",
            "invalid-namespace-index-referenced-node.xml",
            "invalid-namespace-index-Argument.xml",
            "invalid-namespace-index-Field.xml",
            "invalid-namespace-index-MethodDeclarationId.xml",
            "invalid-namespace-index-ReferenceType.xml",
            "invalid-namespace-index-Variable.xml",
            "invalid-namespace-index-VariableType.xml",
            "invalid-namespace-index-ParentNodeId.xml",
    })
    void testInvalidNamespaceIndex(String file) {
        assertThatThrownBy(() -> {
            parseAndValidateXML(file);
        }).isInstanceOf(NodeSetNodeIdValidationException.class);
    }

    @Test
    void testInvalidNamespaceIndexCombined() {
        var exception = catchThrowableOfType(() -> {
            parseAndValidateXML("invalid-namespace-index-combined.xml");
        }, NodeSetNodeIdValidationException.class);
        assertThat(exception.getInvalidNodeIds()).hasSize(10);
    }

    // these NodeSets contain "UnknownAlias" as NodeIds
    @ParameterizedTest
    @ValueSource(strings = {
            "unknown-alias-NodeId.xml",
            "unknown-alias-referenced-node.xml",
            "unknown-alias-Argument.xml",
            "unknown-alias-Field.xml",
            "unknown-alias-MethodDeclarationId.xml",
            "unknown-alias-ReferenceType.xml",
            "unknown-alias-Variable.xml",
            "unknown-alias-VariableType.xml",
            "unknown-alias-ParentNodeId.xml",
    })
    void testUnknownAlias(String file) {
        Assertions.assertThatThrownBy(() -> {
            parseAndValidateXML(file);
        }).isInstanceOf(NodeSetNodeIdValidationException.class);
    }

    @Test
    void testUnknownAliasCombined() {
        var exception = catchThrowableOfType(() -> {
            parseAndValidateXML("unknown-alias-combined.xml");
        }, NodeSetNodeIdValidationException.class);
        assertThat(exception.getInvalidNodeIds()).hasSize(9);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "missing-version.xml",
            "missing-publicationdate.xml",
            "missing-version-in-requiredmodel.xml",
            "missing-publicationdate-in-requiredmodel.xml"
    })
    void testMissingModelData(String file) {
        assertThatThrownBy(() -> {
            parseAndValidateXML(file);
        }).isInstanceOf(NodeSetModelValidationException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid-browsename-empty.xml",
            "invalid-browsename-missingindex.xml"
    })
    void testInvalidBrowseName(String file) {
        assertThatThrownBy(() -> {
            parseAndValidateXML(file);
        }).isInstanceOf(NodeSetBrowseNameValidationException.class);
    }
}
