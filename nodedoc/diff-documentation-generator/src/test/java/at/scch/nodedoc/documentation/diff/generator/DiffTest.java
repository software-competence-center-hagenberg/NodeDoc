package at.scch.nodedoc.documentation.diff.generator;

import at.scch.nodedoc.diffengine.DiffContext;
import at.scch.nodedoc.documentation.diff.DiffDisplayNodeSet;
import at.scch.nodedoc.documentation.diff.graph.DiffGraphDisplayDataBuilder;
import at.scch.nodedoc.nodeset.NodeSetUniverse;
import org.junit.jupiter.api.BeforeAll;

public class DiffTest {

    protected final String MAIN_NAMESPACE_URI = "http://test.org";
    private static DiffDisplayNodeSetGenerator diffDisplayNodeSetGenerator;

    @BeforeAll
    static void setup() {
        var diffDisplayMethodGenerator = new DiffDisplayMethodGenerator();
        var diffDisplayDataTypeTableSectionGenerator = new DiffDisplayDataTypeTableSectionGenerator();
        var diffDisplayInstanceGenerator = new DiffDisplayInstanceGenerator(diffDisplayMethodGenerator);
        var diffDisplayReferencesTableSectionGenerator = new DiffDisplayReferencesTableSectionGenerator();
        var diffDisplayTypeGenerator = new DiffDisplayTypeGenerator(diffDisplayReferencesTableSectionGenerator, diffDisplayDataTypeTableSectionGenerator, diffDisplayInstanceGenerator);
        var diffGraphDisplayDataBuilder = new DiffGraphDisplayDataBuilder();
        diffDisplayNodeSetGenerator = new DiffDisplayNodeSetGenerator(diffDisplayTypeGenerator, diffGraphDisplayDataBuilder);
    }

    protected DiffDisplayNodeSet generateDiff(NodeSetUniverse baseUniverse, NodeSetUniverse compareUniverse) {
        var diffContext = new DiffContext(baseUniverse, compareUniverse);
        return diffDisplayNodeSetGenerator.generateDiffDocumentation(diffContext, MAIN_NAMESPACE_URI);
    }
}
