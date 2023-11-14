package at.scch.nodedoc.nodeset;

public interface UAMethod extends UAInstance {

    UAMethod getMethodDeclaration();
    boolean isExecutable();
    boolean isUserExecutable();
}
