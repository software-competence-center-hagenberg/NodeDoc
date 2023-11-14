package at.scch.nodedoc.documentation.diff.generator;

import at.scch.nodedoc.documentation.diff.DiffDisplayNode;

import java.util.Comparator;

public class Utils {
    public static Comparator<DiffDisplayNode> byBrowseName() {
        return Comparator.comparing(type -> type.getHeadingTextValue().getBaseOrElseCompareValue());
    }
}
