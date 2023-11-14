package at.scch.nodedoc;

import at.scch.nodedoc.parser.rawModel.RawModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;

@Getter
@EqualsAndHashCode
@ToString
public class ModelMetaData {

    @ToString.Include(name = "Uri")
    private final String modelUri;
    @ToString.Include(name = "Version")
    private final String version;
    @ToString.Include(name = "Publication-Date")
    private final OffsetDateTime publicationDate;

    public ModelMetaData(String modelUri, String version, OffsetDateTime publicationDate) {
        this.modelUri = modelUri;
        this.version = version;
        this.publicationDate = publicationDate;
    }

    public static ModelMetaData of(RawModel rawModel) {
        return new ModelMetaData(rawModel.getModelUri(), rawModel.getVersion(), rawModel.getPublicationDate());
    }

    public String getModelUriNoHttp() {
        return modelUri.replaceAll("^https?://", "");
    }
}
