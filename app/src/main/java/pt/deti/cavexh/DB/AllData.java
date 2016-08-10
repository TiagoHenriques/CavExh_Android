package pt.deti.cavexh.DB;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by tiago on 18/05/16.
 */
public class AllData implements Serializable {

    private static final long serialVersionUID = 7863262235394607247L;

    /**
     * Map with all the items fetched from the database.
     */
    private Map<Integer, ListItem> mItems;

    /**
     * Authors list structure.
     */
    private Map<String, String> authorsList;

    /**
     * Historu information structure.
     */
    private History history;

    public AllData(Map<Integer, ListItem> items,  Map<String, String> authorsList, History history) {
        this.mItems = items;
        this.authorsList = authorsList;
        this.history = history;
    }

    public Map<Integer, ListItem> getmItems() {
        return mItems;
    }

    public Map<String, String> getAuthorsList() {
        return authorsList;
    }

    public History getHistory() {
        return history;
    }
}
