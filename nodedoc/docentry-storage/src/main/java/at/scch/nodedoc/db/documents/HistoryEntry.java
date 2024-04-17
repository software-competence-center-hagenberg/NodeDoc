package at.scch.nodedoc.db.documents;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // For JSON support
public class HistoryEntry {
    private String date;
    private String text;

    public HistoryEntry(String date, String text) {
        this.date = date;
        this.text = text;
    }
}
