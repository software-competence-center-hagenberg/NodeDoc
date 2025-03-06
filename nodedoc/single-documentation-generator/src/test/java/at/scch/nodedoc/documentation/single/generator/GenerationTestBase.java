package at.scch.nodedoc.documentation.single.generator;

import at.scch.nodedoc.ModelMetaData;
import at.scch.nodedoc.db.repository.NodeDescriptionRepository;
import at.scch.nodedoc.documentation.DocumentationGenerator;
import at.scch.nodedoc.documentation.single.SingleDisplayNodeSet;
import at.scch.nodedoc.documentation.single.graph.SingleGraphDisplayDataBuilder;
import at.scch.nodedoc.modelresolver.DependencyResolver;
import at.scch.nodedoc.modelresolver.ModelResolver;
import at.scch.nodedoc.modelresolver.NodeIdParser;
import at.scch.nodedoc.modelresolver.ReferenceResolver;
import at.scch.nodedoc.testkit.ClasspathModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GenerationTestBase {

    ModelResolver modelResolver;
    DependencyResolver dependencyResolver;

    SingleDisplayNodeSetGenerator singleDisplayNodeSetGenerator;

    @Mock
    NodeDescriptionRepository nodeDescriptionRepository;
    DocumentationGenerator documentationGenerator;

    @BeforeEach
    void setup() {
        var modelRepository = new ClasspathModelRepository();
        var nodeIdParser = new NodeIdParser();
        var referenceResolver = new ReferenceResolver(nodeIdParser);
        modelResolver = new ModelResolver(modelRepository, nodeIdParser, referenceResolver);
        dependencyResolver = new DependencyResolver(modelRepository);

        when(nodeDescriptionRepository.getAllNodeSetTextsForNodeSet(any(), any(), any()))
                .thenReturn(List.of());

        var docEntryGenerator = new DocEntryEditorGenerator();
        var singleDisplayReferencesTableSectionGenerator = new SingleDisplayReferencesTableSectionGenerator();
        var singleDisplayDataTypeTableSectionGenerator = new SingleDisplayDataTypeTableSectionGenerator(docEntryGenerator);
        var singleDisplayMethodGenerator = new SingleDisplayMethodGenerator(docEntryGenerator);
        var singleDisplayInstanceGenerator = new SingleDisplayInstanceGenerator(singleDisplayMethodGenerator, docEntryGenerator);
        var singleDisplayTypeGenerator = new SingleDisplayTypeGenerator(singleDisplayReferencesTableSectionGenerator, singleDisplayDataTypeTableSectionGenerator, singleDisplayInstanceGenerator, docEntryGenerator);
        var singleGraphDisplayDataBuilder = new SingleGraphDisplayDataBuilder();
        singleDisplayNodeSetGenerator = new SingleDisplayNodeSetGenerator(singleDisplayTypeGenerator, singleGraphDisplayDataBuilder, docEntryGenerator, nodeDescriptionRepository);

        documentationGenerator = new DocumentationGenerator();
    }

    SingleDisplayNodeSet generateSingleDisplayNodeSet(ModelMetaData nodeSetMetaData) {
        var dependencies = dependencyResolver.collectDependencies(nodeSetMetaData);
        var universe = modelResolver.loadNodeSetUniverse(
                Stream.concat(Stream.of(nodeSetMetaData), dependencies.stream())
                        .collect(Collectors.toSet()));
        var singleDisplayNodeSet = singleDisplayNodeSetGenerator.generateSingleDocumentation(universe, nodeSetMetaData.getModelUri());
        return singleDisplayNodeSet;
    }
}
