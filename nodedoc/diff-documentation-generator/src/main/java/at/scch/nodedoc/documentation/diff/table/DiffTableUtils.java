package at.scch.nodedoc.documentation.diff.table;

import at.scch.nodedoc.diffengine.DiffContext;
import at.scch.nodedoc.documentation.diff.DisplayDifferenceType;

public class DiffTableUtils {
    public static <T> DiffTableCell<DiffContext.DiffView<T>> diffViewToTableCell(DiffContext.DiffView<T> diffView) {
        return new DiffTableCell<>(diffView, DisplayDifferenceType.of(diffView.getDiffType()));
    }
}
