package at.scch.nodedoc;

import lombok.Getter;

@Getter
public class MatchingNodeSetWithMinimumPublicationDateNotFoundException extends RuntimeException {

    private final ModelMetaData modelMetaData;

    public MatchingNodeSetWithMinimumPublicationDateNotFoundException(ModelMetaData modelMetaData) {
        super();
        this.modelMetaData = modelMetaData;
    }
}
