package at.scch.nodedoc.nodeset;

public interface UAType extends UANode {

    boolean isAbstract();
    UAType getBaseType();
}
