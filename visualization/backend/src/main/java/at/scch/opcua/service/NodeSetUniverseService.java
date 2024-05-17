package at.scch.opcua.service;

import at.scch.nodedoc.ModelMetaData;
import at.scch.nodedoc.modelresolver.DependencyResolver;
import at.scch.nodedoc.modelresolver.ModelResolver;
import at.scch.nodedoc.nodeset.NodeSetUniverse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class NodeSetUniverseService {

    @Autowired
    private ModelResolver modelResolver;

    @Autowired
    private DependencyResolver dependencyResolver;

    public NodeSetUniverse loadNodeSetUniverse(ModelMetaData nodeSetMetaData, Collection<ModelMetaData> pinnedDependencies) {
        log.info("Load NodeSetUniverse for {} with pinned dependencies {}", nodeSetMetaData, pinnedDependencies);
        return modelResolver.loadNodeSetUniverse(
                Stream.concat(Stream.of(nodeSetMetaData), pinnedDependencies.stream())
                        .collect(Collectors.toSet()));
    }

    public NodeSetUniverse loadNodeSetUniverse(ModelMetaData nodeSetMetaData) {
        log.info("Load NodeSetUniverse for {} with default dependencies", nodeSetMetaData);
        var dependencies = dependencyResolver.collectDependencies(nodeSetMetaData);
        log.info("NodeSetUniverse {} uses dependencies {}", nodeSetMetaData, dependencies);
        return loadNodeSetUniverse(nodeSetMetaData, dependencies);
    }
}
