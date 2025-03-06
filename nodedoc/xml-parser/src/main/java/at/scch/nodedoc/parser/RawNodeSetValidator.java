package at.scch.nodedoc.parser;

import at.scch.nodedoc.parser.rawModel.RawNodeSet;

import java.util.Collection;

public class RawNodeSetValidator {

    private final Collection<Validator> validators;

    public RawNodeSetValidator(Collection<Validator> validators) {
        this.validators = validators;
    }

    public void validateOrThrow(RawNodeSet nodeSet) {
        validators.forEach(validator -> validator.validateOrThrow(nodeSet));
    }
}
