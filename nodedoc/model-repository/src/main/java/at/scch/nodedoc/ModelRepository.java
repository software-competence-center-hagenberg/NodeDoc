package at.scch.nodedoc;

import at.scch.nodedoc.parser.rawModel.RawNodeSet;

import java.io.File;
import java.io.InputStream;
import java.time.OffsetDateTime;

public interface ModelRepository {

    RawNodeSet loadNodeSet(ModelMetaData metaData);

    default RawNodeSet loadNodeSet(String modelUri, String version, OffsetDateTime publicationDate) {
        return loadNodeSet(new ModelMetaData(modelUri, version, publicationDate));
    }

    ModelMetaData saveNodeSet(InputStream contents);

    @Deprecated
    File getFileForNodeSet(ModelMetaData metaData);

}
