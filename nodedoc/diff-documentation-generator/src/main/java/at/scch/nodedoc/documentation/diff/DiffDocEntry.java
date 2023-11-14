package at.scch.nodedoc.documentation.diff;

import at.scch.nodedoc.diffengine.DiffContext;
import at.scch.nodedoc.documentation.displaymodel.Renderable;
import com.fizzed.rocker.RockerContent;
import com.fizzed.rocker.runtime.DefaultRockerModel;
import lombok.Getter;

@Getter
public class DiffDocEntry implements Renderable {

    private final DiffContext.DiffView<String> value;
    private final DisplayDifferenceType displayDifferenceType;
    private final String title;

    public DiffDocEntry(DiffContext.DiffView<String> value, DisplayDifferenceType displayDifferenceType, String title) {
        this.value = value;
        this.displayDifferenceType = displayDifferenceType;
        this.title = title; value.getBaseOrElseCompareValue();
    }

    @Override
    public DefaultRockerModel structureTemplate(RockerContent content) {
        return DiffDocEntryTemplate.template(this);
    }

    public boolean hasValue() {
        return !value.getBaseOrElseCompareValue().equals("") || !value.getCompareOrElseBaseValue().equals("");
    }
}
