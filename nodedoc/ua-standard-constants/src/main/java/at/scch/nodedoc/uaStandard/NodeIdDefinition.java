package at.scch.nodedoc.uaStandard;

import java.util.regex.Pattern;

public class NodeIdDefinition {
    public static final Pattern NODE_ID_REGEX = Pattern.compile("(?:ns=(?<ns>\\d+);)?(?<type>[igsb])=(?<id>.+)");
}
