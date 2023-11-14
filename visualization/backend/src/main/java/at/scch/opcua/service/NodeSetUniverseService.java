package at.scch.opcua.service;

import at.scch.nodedoc.ModelMetaData;
import at.scch.nodedoc.modelresolver.DependencyResolver;
import at.scch.nodedoc.modelresolver.ModelResolver;
import at.scch.nodedoc.nodeset.NodeSetUniverse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class NodeSetUniverseService {

    @Autowired
    private ModelResolver modelResolver;

    @Autowired
    private DependencyResolver dependencyResolver;

    public NodeSetUniverse loadNodeSetUniverse(ModelMetaData nodeSetMetaData, Collection<ModelMetaData> pinnedDependencies) {
        return modelResolver.loadNodeSetUniverse(
                Stream.concat(Stream.of(nodeSetMetaData), pinnedDependencies.stream())
                        .collect(Collectors.toSet()));
    }

    public NodeSetUniverse loadNodeSetUniverse(ModelMetaData nodeSetMetaData) {
        var dependencies = dependencyResolver.collectDependencies(nodeSetMetaData);
        return loadNodeSetUniverse(nodeSetMetaData, dependencies);
    }
}
