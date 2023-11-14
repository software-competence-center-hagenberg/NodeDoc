package at.scch.nodedoc.modelresolver;

import at.scch.nodedoc.ModelMetaData;
import at.scch.nodedoc.ModelRepository;
import at.scch.nodedoc.parser.rawModel.RawModel;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DependencyResolver {
    private final ModelRepository modelRepository;

    public DependencyResolver(ModelRepository modelRepository) {
        this.modelRepository = modelRepository;
    }

    /**
     * Filters the metaDataCollection to only contain the newest entry according to the publication date.
     *
     * @param metaDataCollection Stream of a metadata collection that needs to be filtered
     * @return A set only containing the newest {@link ModelMetaData} per ModelUri
     */
    public Collection<ModelMetaData> getNewestModelMetaData(Stream<ModelMetaData> metaDataCollection) {
        return metaDataCollection
                .collect(Collectors.toMap(
                        ModelMetaData::getModelUri,
                        Function.identity(),
                        BinaryOperator.maxBy(Comparator.comparing(ModelMetaData::getPublicationDate)))
                ).values();
    }

    /**
     * Collects ModelMetaData of all requiredNodeSets of a NodeSet model.
     *
     * @param modelMetaData         metaData of the current model
     * @param collectedDependencies metaData of all currently pinned models
     * @return a Map"<"modelUri, ModelMetaData> of collected Dependencies
     */
    private Map<String, ModelMetaData> collectDependenciesRecursive(ModelMetaData modelMetaData, Map<String, ModelMetaData> collectedDependencies) {
        var rawNodeSet = modelRepository.loadNodeSet(modelMetaData);

        for (RawModel requiredModel : rawNodeSet.getModels().get(0).getRequiredModels()) {
            var referencedModel = ModelMetaData.of(requiredModel);

            var currentlyResolvedModel = collectedDependencies.get(referencedModel.getModelUri());
            if (currentlyResolvedModel == null || referencedModel.getPublicationDate().compareTo(currentlyResolvedModel.getPublicationDate()) > 0) {
                collectedDependencies.put(referencedModel.getModelUri(), referencedModel);
                collectDependenciesRecursive(referencedModel, collectedDependencies);
            }
        }
        return collectedDependencies;
    }

    /**
     * Only collects dependencies of the nodeSet with the given metadata.
     *
     * @param metaData
     * @return Collection of referenced NodeSet dependencies
     */
    public Collection<ModelMetaData> collectDependencies(ModelMetaData metaData) {
        return collectDependenciesRecursive(metaData, new HashMap<>()).values();
    }

}
