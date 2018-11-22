package snutella.queryresults;

import java.util.ArrayList;
import java.util.List;

public class QueryResultsManager {
    private static QueryResultsManager instance;
    private List<QueryResultItem> items;
    private List<QueryResultsListener> listeners;

    private QueryResultsManager() {
        this.items = new ArrayList<>();
        this.listeners = new ArrayList<>();
    }

    public static synchronized QueryResultsManager getInstance() {
        if (instance == null) {
            instance = new QueryResultsManager();
        }
        return instance;
    }

    public void reset() {
        this.items.clear();
    }

    public void addItems(List<QueryResultItem> items) {
        this.items.addAll(items);
        for (QueryResultsListener listener: listeners) {
            listener.resultsChanged(this.items);
        }
    }

    public void addListener(QueryResultsListener listener) {
        this.listeners.add(listener);
    }
}
