package snutella.queryresults;

import java.util.*;

public class QueryResultsManager {
    private static QueryResultsManager instance;
    private Set<QueryResultItem> items;
    private List<QueryResultsListener> listeners;

    private QueryResultsManager() {
        this.items = new HashSet<>();
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
        for (QueryResultsListener listener: listeners) {
            listener.resultsChanged(this.items);
        }
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
