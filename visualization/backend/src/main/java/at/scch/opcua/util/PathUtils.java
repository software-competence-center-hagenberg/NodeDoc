package at.scch.opcua.util;

import java.io.File;
import java.nio.file.Path;

public class PathUtils {

    public static Path getRelativePath(File base, File toFile) {
        return base.toPath().relativize(toFile.toPath());
    }
}
