package at.scch.nodedoc.documentation.diff.generator;

import at.scch.nodedoc.diffengine.DiffContext;

public enum RenderMode {

    SHOW_CHANGES,
    SHOW_AS_UNCHANGED_WITH_BASE_VERSION,
    SHOW_AS_UNCHANGED_WITH_COMPARE_VERSION;

    public <T> DiffContext.DiffView<T> apply(DiffContext.DiffView<T> diffView) {
        switch (this) {
            case SHOW_CHANGES:
                return diffView;
            case SHOW_AS_UNCHANGED_WITH_BASE_VERSION:
                return diffView.toUnchangedKeepBaseValue();
            case SHOW_AS_UNCHANGED_WITH_COMPARE_VERSION:
                return diffView.toUnchangedKeepCompareValue();
            default:
                throw new IllegalStateException("Unexpected value: " + this);
        }
    }
}
