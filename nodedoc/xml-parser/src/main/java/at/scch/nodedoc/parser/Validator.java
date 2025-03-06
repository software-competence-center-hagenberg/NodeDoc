package at.scch.nodedoc.parser;

import at.scch.nodedoc.parser.rawModel.RawNodeSet;

public interface Validator {

    void validateOrThrow(RawNodeSet nodeSet);
}
