package at.scch.nodedoc.documentation.single.generator;

import at.scch.nodedoc.documentation.single.SingleDisplayNode;

import java.util.Comparator;

public class Utils {
    public static Comparator<SingleDisplayNode> byBrowseName() {
        return Comparator.comparing(type -> type.getHeadingTextValue());
    }
}
