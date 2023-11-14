package at.scch.nodedoc.documentation.displaymodel;

import at.scch.nodedoc.documentation.template.DisplayNodeSetContentTemplate;
import at.scch.nodedoc.documentation.template.DisplayTableOfContentsTemplate;
import at.scch.nodedoc.documentation.util.NumberingUtils;
import com.fizzed.rocker.RockerContent;
import com.fizzed.rocker.runtime.DefaultRockerModel;

import java.util.List;

public interface DisplayNodeSet {

    interface TableOfContents extends Renderable {

        List<? extends DisplayTableOfContentsEntry> getEntries();

        @Override
        default DefaultRockerModel structureTemplate(RockerContent content) {
            return DisplayTableOfContentsTemplate.template(this, content);
        }
    }

    interface Content extends Renderable {
        default List<? extends DisplaySection> getChapters() {
            var chapters = List.of(
                    new DisplayMainChapter("Container Types", getContainerTypes()),
                    new DisplayMainChapter("Object Types", getObjectTypes()),
                    new DisplayMainChapter("Variable Types", getVariableTypes()),
                    new DisplayMainChapter("Data Types", getDataTypes()),
                    new DisplayMainChapter("Reference Types", getReferenceTypes())
            );
            NumberingUtils.calculateHeadingNumbers(chapters, 1); // TODO move
            NumberingUtils.calculateFigureNumbers(chapters); // TODO move
            NumberingUtils.calculateTableNumbers(chapters); // TODO move
            return chapters;
        }

        List<? extends GenericDisplayType> getContainerTypes();
        List<? extends GenericDisplayType> getObjectTypes();
        List<? extends DisplayVariableType> getVariableTypes();
        List<? extends DisplayDataType> getDataTypes();
        List<? extends GenericDisplayType> getReferenceTypes();

        @Override
        default DefaultRockerModel structureTemplate(RockerContent content) {
            return DisplayNodeSetContentTemplate.template(this, content);
        }

    }

    TableOfContents getTableOfContents();
    Content getContent();

}
