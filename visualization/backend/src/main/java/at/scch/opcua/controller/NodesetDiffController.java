package at.scch.opcua.controller;


import at.scch.opcua.dto.DiffInfo;
import at.scch.opcua.service.NodesetDiffService;
import at.scch.opcua.service.NodesetDiffStorageService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@CrossOrigin
public class NodesetDiffController {

    @Autowired
    private NodesetDiffService nodesetDiffService;

    @Autowired
    private NodesetDiffStorageService nodesetDiffStorageService;

    @ApiOperation(value = "Gets the diff-html for two given nodeset paths.")
    @PostMapping("/generateDiff/")
    public void generateDiff(@RequestParam("base") String baseNodeset, @RequestParam("compare") String compareNodeset) {
        nodesetDiffService.generateDiff(baseNodeset, compareNodeset);
    }

    @ApiOperation(value = "Get list of all stored diffs")
    @GetMapping("/diffs/")
    public List<DiffInfo> getDiffs() {
        var diffs = nodesetDiffStorageService.getStoredDiffs();
        diffs.sort(Comparator.comparing(DiffInfo::getGenerated).reversed());
        return diffs;
    }

    @ApiOperation(value = "Delete a diff")
    @DeleteMapping("diff")
    public void deleteDiff(@RequestParam("path") String diffPath){
        nodesetDiffService.deleteDiff(diffPath);
    }
}
