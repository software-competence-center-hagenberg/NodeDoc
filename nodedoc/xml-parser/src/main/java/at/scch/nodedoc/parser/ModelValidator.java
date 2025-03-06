package at.scch.nodedoc.parser;

import at.scch.nodedoc.parser.rawModel.RawModel;
import at.scch.nodedoc.parser.rawModel.RawNodeSet;

public class ModelValidator implements Validator {

    public void validateOrThrow(RawNodeSet nodeSet) {
        nodeSet.getModels().forEach(this::validateOrThrow);
    }

    private void validateOrThrow(RawModel model) {
        if (model.getVersion() == null || model.getVersion().trim().isEmpty()) {
            throw new NodeSetModelValidationException("Version is required in model " + model.getModelUri());
        }

        if (model.getPublicationDate() == null) {
            throw new NodeSetModelValidationException("PublicationDate is required in model " + model.getModelUri());
        }
        model.getRequiredModels().forEach(this::validateOrThrow);
    }
}
