package at.scch.nodedoc.documentation.single;

import at.scch.nodedoc.documentation.displaymodel.DisplayModelHeadingNumber;
import at.scch.nodedoc.documentation.displaymodel.DisplayNodeSet;
import at.scch.nodedoc.documentation.displaymodel.DisplaySection;
import at.scch.nodedoc.documentation.displaymodel.DisplayTableOfContentsEntry;
import com.fizzed.rocker.runtime.DefaultRockerModel;
import lombok.Getter;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class SingleDisplayNodeSet implements DisplayNodeSet {

    public class SingleTableOfContents implements TableOfContents {

        @Getter
        public class Entry implements DisplayTableOfContentsEntry {

            private final DisplaySection displaySection;

            public Entry(DisplaySection displaySection) {
                this.displaySection = displaySection;
            }

            @Override
            public DisplayModelHeadingNumber getHeadingNumber() {
                return displaySection.getHeadingNumber();
            }

            @Override
            public DisplaySection.DisplayHeadingText getHeadingText() {
                return displaySection.getHeadingText();
            }

            @Override
            public List<? extends DisplayTableOfContentsEntry> getSubEntries() {
                return displaySection.getSubSections().stream()
                        .map(SingleTableOfContents.this::buildEntry)
                        .collect(Collectors.toList());
            }

            @Override
            public DefaultRockerModel displayTemplate(DefaultRockerModel content) {
                return SingleDisplayTableOfContentsEntryTemplate.template(this, content);
            }
        }

        @Override
        public List<? extends DisplayTableOfContentsEntry> getEntries() {
            return getContent().getChapters().stream()
                    .map(this::buildEntry)
                    .collect(Collectors.toList());
        }

        private DisplayTableOfContentsEntry buildEntry(DisplaySection section) {
            return new Entry(section);
        }
    }


    @Getter
    public static class SingleContent implements Content {

        private final String modelUri;
        private final String version;
        private final String publicationDate;
        private final List<GenericSingleDisplayType> containerTypes;
        private final List<GenericSingleDisplayType> objectTypes;
        private final List<SingleDisplayVariableType> variableTypes;
        private final List<SingleDisplayDataType> dataTypes;
        private final List<GenericSingleDisplayType> referenceTypes;
        private final Element namespaceIndicesEditor;
        private final List<String> usedNamespaces;

        public SingleContent(String modelUri, String version, String publicationDate, List<GenericSingleDisplayType> containerTypes, List<GenericSingleDisplayType> objectTypes, List<SingleDisplayVariableType> variableTypes, List<SingleDisplayDataType> dataTypes, List<GenericSingleDisplayType> referenceTypes, Element namespaceIndicesEditor, List<String> usedNamespaces) {
            this.modelUri = modelUri;
            this.version = version;
            this.publicationDate = publicationDate;
            this.containerTypes = containerTypes;
            this.objectTypes = objectTypes;
            this.variableTypes = variableTypes;
            this.dataTypes = dataTypes;
            this.referenceTypes = referenceTypes;
            this.namespaceIndicesEditor = namespaceIndicesEditor;
            this.usedNamespaces = usedNamespaces;
        }

        @Override
        public DefaultRockerModel displayTemplate(DefaultRockerModel content) {
            return SingleDisplayNodeSetContentTemplate.template(this, content);
        }
    }

    private final SingleContent content;

    public SingleDisplayNodeSet(SingleContent content) {
        this.content = content;
    }

    @Override
    public TableOfContents getTableOfContents() {
        return new SingleTableOfContents();
    }

}
