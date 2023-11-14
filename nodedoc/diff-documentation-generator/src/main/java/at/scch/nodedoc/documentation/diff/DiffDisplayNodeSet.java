package at.scch.nodedoc.documentation.diff;

import at.scch.nodedoc.diffengine.DiffContext;
import at.scch.nodedoc.documentation.displaymodel.DisplayModelHeadingNumber;
import at.scch.nodedoc.documentation.displaymodel.DisplayNodeSet;
import at.scch.nodedoc.documentation.displaymodel.DisplaySection;
import at.scch.nodedoc.documentation.displaymodel.DisplayTableOfContentsEntry;
import com.fizzed.rocker.runtime.DefaultRockerModel;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class DiffDisplayNodeSet implements DisplayNodeSet {

    public class DiffTableOfContents implements TableOfContents {

        @Getter
        public class Entry implements DisplayTableOfContentsEntry {

            private final DisplaySection displaySection;
            private final DisplayDifferenceType diffType;

            public Entry(DisplaySection displaySection, DisplayDifferenceType diffType) {
                this.displaySection = displaySection;
                this.diffType = diffType;
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
                        .map(DiffTableOfContents.this::buildEntry)
                        .collect(Collectors.toList());
            }

            @Override
            public DefaultRockerModel displayTemplate(DefaultRockerModel content) {
                return DiffDisplayTableOfContentsEntryTemplate.template(this, content);
            }
        }

        @Override
        public List<? extends DisplayTableOfContentsEntry> getEntries() {
            return getContent().getChapters().stream()
                    .map(this::buildEntry)
                    .collect(Collectors.toList());
        }

        private DisplayTableOfContentsEntry buildEntry(DisplaySection section) {
            DisplayDifferenceType diffType = DisplayDifferenceType.UNCHANGED;
            if (section instanceof DiffDisplayNode) {
                diffType = ((DiffDisplayNode) section).getDisplayDifferenceType();
            }
            return new DiffTableOfContents.Entry(section, diffType);
        }
    }


    @Getter
    public static class DiffContent implements Content {

        private final DiffContext.DiffView<String> modelUri;
        private final DiffContext.DiffView<String> version;
        private final List<GenericDiffDisplayType> containerTypes;
        private final List<GenericDiffDisplayType> objectTypes;
        private final List<DiffDisplayVariableType> variableTypes;
        private final List<DiffDisplayDataType> dataTypes;
        private final List<GenericDiffDisplayType> referenceTypes;

        public DiffContent(DiffContext.DiffView<String> modelUri, DiffContext.DiffView<String> version, List<GenericDiffDisplayType> containerTypes, List<GenericDiffDisplayType> objectTypes, List<DiffDisplayVariableType> variableTypes, List<DiffDisplayDataType> dataTypes, List<GenericDiffDisplayType> referenceTypes) {
            this.modelUri = modelUri;
            this.version = version;
            this.containerTypes = containerTypes;
            this.objectTypes = objectTypes;
            this.variableTypes = variableTypes;
            this.dataTypes = dataTypes;
            this.referenceTypes = referenceTypes;
        }

        @Override
        public DefaultRockerModel displayTemplate(DefaultRockerModel content) {
            return DiffDisplayNodeSetContentTemplate.template(this, content);
        }
    }

    private final DiffContent content;

    public DiffDisplayNodeSet(DiffContent content) {
        this.content = content;
    }

    @Override
    public TableOfContents getTableOfContents() {
        return new DiffTableOfContents();
    }

}
