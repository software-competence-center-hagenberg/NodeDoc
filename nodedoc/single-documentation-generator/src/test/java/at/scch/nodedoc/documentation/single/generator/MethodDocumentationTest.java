package at.scch.nodedoc.documentation.single.generator;

import at.scch.nodedoc.documentation.displaymodel.DisplayMethod;
import at.scch.nodedoc.testkit.ClasspathModelRepository;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class MethodDocumentationTest extends GenerationTestBase {

    @Test
    void testMethodNamespaceIndices() {
        var singleDisplayNodeSet = generateSingleDisplayNodeSet(ClasspathModelRepository.Models.METHODS_MAIN);

        var displayMethod = (DisplayMethod) singleDisplayNodeSet.getContent().getObjectTypes().get(0).getSubSections().get(0);
        var arguments = displayMethod.getArguments().stream()
                .map(
                        argument -> argument.getArgumentType().template().render().toString()
                )
                .map(String::trim)
                .collect(Collectors.toList());
        assertThat(arguments).containsExactlyInAnyOrder(
                "2:StructureInDependency",
                "1:StructureInMain"
        );

    }
}
