package at.scch.nodedoc.documentation.diff;

import at.scch.nodedoc.diffengine.DiffContext;

public enum DisplayDifferenceType {

    ADDED,
    REMOVED,
    CHANGED,
    UNCHANGED;


    public static DisplayDifferenceType of(DiffContext.EntryDiffType entryDiffType) {
        switch (entryDiffType) {
            case ADDED:
                return ADDED;
            case REMOVED:
                return REMOVED;
            case UNCHANGED:
                return UNCHANGED;
            default:
                throw new IllegalStateException("Unexpected value: " + entryDiffType);
        }
    }

    public static DisplayDifferenceType of(DiffContext.ValueDiffType diffType) {
        switch (diffType) {
            case CHANGED:
                return CHANGED;
            case UNCHANGED:
                return UNCHANGED;

            default:
                throw new IllegalStateException("Unexpected value: " + diffType);
        }
    }

    public String cssClass() {
        switch (this) {
            case ADDED:
                return "added";
            case REMOVED:
                return "removed";
            case CHANGED:
                return "changed";
            case UNCHANGED:
                return "unchanged";
            default:
                throw new IllegalStateException("Unexpected value: " + this);
        }
    }
}
