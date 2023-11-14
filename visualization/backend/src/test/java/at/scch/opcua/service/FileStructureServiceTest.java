package at.scch.opcua.service;


import at.scch.opcua.dto.FilestructureDirectoryNode;
import at.scch.opcua.dto.FilestructureFileNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;

class FileStructureServiceTest {

    @Test
    void fileStructureTest01() {
        FileStructureService fileStructureService = new FileStructureService();

        var structure = fileStructureService.getFileStructure(new File("test-directory-structures/nodesets-01"));

        Assertions.assertEquals(1, structure.getChildren().size());
        var directoryNode = (FilestructureDirectoryNode)structure.getChildren().get(0);

        Assertions.assertEquals("www.euromap.org/euromap83/1.00/2019-01-28", directoryNode.getName());

        Assertions.assertEquals(1, directoryNode.getChildren().size());
        var fileNode = (FilestructureFileNode)directoryNode.getChildren().get(0);

        Assertions.assertEquals(
                Path.of("www.euromap.org", "euromap83", "versions", "1.00", "2019-01-28", "Euromap_test_1_00.html"),
                Path.of(fileNode.getDocuPath()));

        Assertions.assertEquals(
                Path.of("www.euromap.org", "euromap83", "versions", "1.00", "2019-01-28", "Euromap_test_1_00.xml"),
                Path.of(fileNode.getNodesetPath()));
    }

    @Test
    void fileStructureTest02() {
        FileStructureService fileStructureService = new FileStructureService();

        var structure = fileStructureService.getFileStructure(new File("test-directory-structures/nodesets-02"));

        Assertions.assertEquals(1, structure.getChildren().size());
        var directoryNode = (FilestructureDirectoryNode)structure.getChildren().get(0);

        Assertions.assertEquals("www.euromap.org/euromap83", directoryNode.getName());

        Assertions.assertEquals(2, directoryNode.getChildren().size());
        var subDir1 = (FilestructureDirectoryNode)directoryNode.getChildren().get(0);
        var subDir2 = (FilestructureDirectoryNode)directoryNode.getChildren().get(1);

        Assertions.assertEquals(1, subDir1.getChildren().size());
        Assertions.assertEquals("1.00/2019-01-28", subDir1.getName());

        Assertions.assertEquals(1, subDir2.getChildren().size());
        Assertions.assertEquals("2.00/2020-01-02", subDir2.getName());

        var fileNode1 = (FilestructureFileNode) subDir1.getChildren().get(0);
        var fileNode2 = (FilestructureFileNode) subDir2.getChildren().get(0);

        Assertions.assertEquals(
                Path.of("www.euromap.org", "euromap83", "versions", "1.00", "2019-01-28", "Euromap_test_1_00.html"),
                Path.of(fileNode1.getDocuPath()));

        Assertions.assertEquals(
                Path.of("www.euromap.org", "euromap83", "versions", "1.00", "2019-01-28", "Euromap_test_1_00.xml"),
                Path.of(fileNode1.getNodesetPath()));

        Assertions.assertEquals(
                Path.of("www.euromap.org", "euromap83", "versions", "2.00", "2020-01-02", "Euromap_test_2_00.html"),
                Path.of(fileNode2.getDocuPath()));

        Assertions.assertEquals(
                Path.of("www.euromap.org", "euromap83", "versions", "2.00", "2020-01-02", "Euromap_test_2_00.xml"),
                Path.of(fileNode2.getNodesetPath()));
    }

    @Test
    void fileStructureTest03() {
        FileStructureService fileStructureService = new FileStructureService();

        var structure = fileStructureService.getFileStructure(new File("test-directory-structures/nodesets-03"));

        Assertions.assertEquals(2, structure.getChildren().size());
        var directoryNode1 = (FilestructureDirectoryNode)structure.getChildren().get(0);
        var directoryNode2 = (FilestructureDirectoryNode)structure.getChildren().get(1);

        Assertions.assertEquals("www.euromap.org/euromap83/1.00/2019-01-28", directoryNode1.getName());
        Assertions.assertEquals("www.industry.com/industry/1.00/2019-01-28", directoryNode2.getName());

        Assertions.assertEquals(1, directoryNode1.getChildren().size());
        Assertions.assertEquals(1, directoryNode2.getChildren().size());
        var fileNode1 = (FilestructureFileNode)directoryNode1.getChildren().get(0);
        var fileNode2 = (FilestructureFileNode)directoryNode2.getChildren().get(0);

        Assertions.assertEquals(
                Path.of("www.euromap.org", "euromap83", "versions", "1.00", "2019-01-28", "Euromap_test_1_00.html"),
                Path.of(fileNode1.getDocuPath()));

        Assertions.assertEquals(
                Path.of("www.euromap.org", "euromap83", "versions", "1.00", "2019-01-28", "Euromap_test_1_00.xml"),
                Path.of(fileNode1.getNodesetPath()));

        Assertions.assertEquals(
                Path.of("www.industry.com", "industry", "versions", "1.00", "2019-01-28", "Industry_test_1_00.html"),
                Path.of(fileNode2.getDocuPath()));

        Assertions.assertEquals(
                Path.of("www.industry.com", "industry", "versions", "1.00", "2019-01-28", "Industry_test_1_00.xml"),
                Path.of(fileNode2.getNodesetPath()));
    }

    @Test
    void fileStructureTest04() {
        FileStructureService fileStructureService = new FileStructureService();

        var structure = fileStructureService.getFileStructure(new File("test-directory-structures/nodesets-04"));

        Assertions.assertEquals(1, structure.getChildren().size());
        var directoryNode = (FilestructureDirectoryNode)structure.getChildren().get(0);

        Assertions.assertEquals("www.euromap.org/euromap83/1.00/2019-01-28", directoryNode.getName());

        Assertions.assertEquals(1, directoryNode.getChildren().size());
        var fileNode = (FilestructureFileNode)directoryNode.getChildren().get(0);

        Assertions.assertEquals(
                Path.of("www.euromap.org", "euromap83", "versions", "1.00", "2019-01-28", "Euromap_test_1_00.html"),
                Path.of(fileNode.getDocuPath()));

        Assertions.assertNull(fileNode.getNodesetPath());
    }

    @Test
    void fileStructureTest05() {
        FileStructureService fileStructureService = new FileStructureService();

        var structure = fileStructureService.getFileStructure(new File("test-directory-structures/nodesets-05"));

        Assertions.assertEquals(1, structure.getChildren().size());
        var directoryNode = (FilestructureDirectoryNode)structure.getChildren().get(0);

        Assertions.assertEquals("www.euromap.org/euromap83/1.00/2019-01-28", directoryNode.getName());

        Assertions.assertEquals(1, directoryNode.getChildren().size());
        var fileNode = (FilestructureFileNode)directoryNode.getChildren().get(0);

        Assertions.assertNull(fileNode.getDocuPath());

        Assertions.assertEquals(
                Path.of("www.euromap.org", "euromap83", "versions", "1.00", "2019-01-28", "Euromap_test_1_00.xml"),
                Path.of(fileNode.getNodesetPath()));
    }
}