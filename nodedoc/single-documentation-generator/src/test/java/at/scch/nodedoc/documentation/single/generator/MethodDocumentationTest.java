package at.scch.nodedoc.documentation.single.generator;

import at.scch.nodedoc.documentation.displaymodel.DisplayMethod;
import at.scch.nodedoc.documentation.displaymodel.table.DisplayTable;
import at.scch.nodedoc.testkit.ClasspathModelRepository;
import at.scch.nodedoc.util.StreamUtils;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    @Test
    void testMethodArgumentTableSectionColumns() {
        var singleDisplayNodeSet = generateSingleDisplayNodeSet(ClasspathModelRepository.Models.EUROMAP83_1_01);

        var displayMethod = singleDisplayNodeSet.getContent().getObjectTypes().stream()
                .flatMap(objType -> objType.getSubSections().stream())
                .flatMap(StreamUtils.filterCast(DisplayMethod.class))
                .filter(method -> method.getMethodName().template().render().toString().trim().equals("1:SetCyclicJobData"))
                .findFirst().get();

        var nominalPartsArgument = displayMethod.getMethodArgumentsTable().getMethodArgumentTableSection().getRows().stream()
                .map(DisplayTable.Row::getCells)
                .map(cells -> {
                    var columnDefinitions = displayMethod.getMethodArgumentsTable().getMethodArgumentTableSection().getColumnDefinitions();

                    return IntStream.range(0, cells.size())
                            .mapToObj(i -> columnDefinitions.get(i).getCellValueTemplate(cells.get(i).getValue()).render().toString())
                            .collect(Collectors.toList());
                })
                .filter(row -> row.get(0).trim().equals("NominalParts"))
                .findFirst().get();

        assertThat(nominalPartsArgument.get(1).trim()).isEqualTo("UInt64");
        assertThat(nominalPartsArgument.get(2).trim()).isEqualTo("Scalar");
    }
}
