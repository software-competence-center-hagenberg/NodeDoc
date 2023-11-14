package at.scch.nodedoc.nodeset;

import java.util.List;

public interface UAReferenceType extends UAType {

    boolean getSymmetric();
    List<String> getInverseNames();
}
