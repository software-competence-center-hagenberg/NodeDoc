package at.scch.nodedoc.db.repository;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.views.AllDocsResponse;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CouchDbUtils {

    @Getter
    private static class PageAndResponse {
        private final long pageNumber;
        private final AllDocsResponse response;

        public PageAndResponse(long pageNumber, AllDocsResponse response) {
            this.pageNumber = pageNumber;
            this.response = response;
        }
    }

    @Getter
    public static class Page<T> {
        private final long pageNumber;
        private final List<T> docs;

        public Page(long pageNumber, List<T> docs) {
            this.pageNumber = pageNumber;
            this.docs = docs;
        }
    }

    @SneakyThrows
    private static PageAndResponse fetchPage(Database db, int pageSize, long pageNumber, boolean includeDocs) {
        var request = db.getAllDocsRequestBuilder()
                .limit(pageSize)
                .skip(pageNumber * pageSize)
                .includeDocs(includeDocs)
                .build();
        return new PageAndResponse(pageNumber, request.getResponse());
    }

    public static <T> Stream<Page<T>> fetchAllDocsPaginated(Database db, int pageSize, Class<T> clazz) {
        return createPageStream(db, pageSize, true)
                .map(it -> new Page<>(it.pageNumber, it.getResponse().getDocsAs(clazz)));
    }

    @Getter
    public static class IdAndRev {
        private final String id;
        private final String rev;

        public IdAndRev(String id, String rev) {
            this.id = id;
            this.rev = rev;
        }
    }

    public static Stream<Page<IdAndRev>> fetchAllDocIdsAndRevsPaginated(Database db, int pageSize) {
        return createPageStream(db, pageSize, false)
                .map(it -> {
                    var idsAndRevs = it.getResponse().getIdsAndRevs().entrySet().stream()
                            .map(entry -> new IdAndRev(entry.getKey(), entry.getValue()))
                            .collect(Collectors.toList());
                    return new Page<>(it.pageNumber, idsAndRevs);
                });
    }

    private static Stream<PageAndResponse> createPageStream(Database db, int pageSize, boolean includeDocs) {
        return Stream.iterate(fetchPage(db, pageSize, 0, includeDocs), Objects::nonNull, it -> {
                    if (moreDocumentsToFetch(pageSize, it)) {
                        return fetchPage(db, pageSize, it.getPageNumber() + 1, includeDocs);
                    } else {
                        return null;
                    }
                })
                .filter(it -> !it.getResponse().getDocs().isEmpty());
    }

    private static boolean moreDocumentsToFetch(int pageSize, PageAndResponse pageAndResponse) {
        var numberOfDocumentsFetched = pageAndResponse.getResponse().getDocs().size() + pageAndResponse.getPageNumber() * pageSize;
        return numberOfDocumentsFetched < pageAndResponse.getResponse().getTotalRowCount();
    }

}
