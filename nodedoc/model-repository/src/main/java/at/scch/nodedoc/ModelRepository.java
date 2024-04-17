package at.scch.nodedoc;

import at.scch.nodedoc.parser.rawModel.RawNodeSet;
import org.javatuples.Pair;

import java.io.File;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.Collection;

public interface ModelRepository {

    RawNodeSet loadNodeSet(ModelMetaData metaData);

    default RawNodeSet loadNodeSet(String modelUri, String version, OffsetDateTime publicationDate) {
        return loadNodeSet(new ModelMetaData(modelUri, version, publicationDate));
    }

    ModelMetaData saveNodeSet(InputStream contents);

    @Deprecated
    File getFileForNodeSet(ModelMetaData metaData);

    boolean nodeSetWithModelUriExists(ModelMetaData modelMetaData);

    Pair<Boolean, Collection<ModelMetaData>> deleteAllNodeSetsStartingAt(String relativePath);

}
